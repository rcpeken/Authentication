package com.yigidolar.apiproject.Controller;

import com.yigidolar.apiproject.DTO.*;
import com.yigidolar.apiproject.Entity.RefreshToken;
import com.yigidolar.apiproject.Entity.User;
import com.yigidolar.apiproject.Repository.UserRepository;
import com.yigidolar.apiproject.Service.EmailService;
import com.yigidolar.apiproject.Service.RefreshTokenService;
import com.yigidolar.apiproject.Service.TokenBlacklistService;
import com.yigidolar.apiproject.Service.UserService;
import com.yigidolar.apiproject.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public MessageResponse register(@RequestBody RegisterRequest request) {
        userService.register(request.getUsername(), request.getPassword(), request.getEmail());
        return new MessageResponse("Kayıt Başarılı");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody(required = false) LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword(), request.getEmail());

        String accessToken = jwtUtil.generateToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @PostMapping("/logout")
    public MessageResponse logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Instant expiryDate = jwtUtil.getExpirationFromToken(token);
            tokenBlacklistService.blacklistToken(token, expiryDate);
            return new MessageResponse("Çıkış yapıldı, token blacklist'e eklendi.");
        }
        return new MessageResponse("Token bulunamadı.");
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader, @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();


        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    if (refreshTokenService.isTokenExpired(refreshToken)) {
                        refreshTokenService.deleteByUsername(refreshToken.getUsername());
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh token expired");
                    }

                    String newAccessToken = jwtUtil.generateToken(refreshToken.getUsername());

                    return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, requestRefreshToken));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Refresh token not found"));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        boolean isVerified = emailService.verifyEmail(email, code);

        if (isVerified) {
            userService.markEmailAsVerified(email);
            return ResponseEntity.ok("E-posta doğrulandı");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Doğrulama kodu hatalı");
        }
    }

    @SneakyThrows
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        String token = UUID.randomUUID().toString();
        String verifyCode = generateVerificationCode();

        userService.createPasswordResetToken(user, token, verifyCode);
        emailService.sendResetPasswordEmail(user.getEmail(), user.getUsername(), verifyCode);

        return ResponseEntity.ok("Token: " + token);
    }

    @PostMapping("/verify-password-code")
    public ResponseEntity<?> verifyPasswordCode(@RequestParam String email, @RequestParam String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        boolean isVerified = userService.verifyPasswordResetCode(user, code);

        if (isVerified)
            return ResponseEntity.ok("Kod doğrulandı, şifre yenileme aşamasına geçebilirsiniz.");
        else
            return ResponseEntity.ok("Kod doğrulanamadı");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Şifre başarıyla sıfırlandı");
    }

    private String generateVerificationCode() {
        int code = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }
}
