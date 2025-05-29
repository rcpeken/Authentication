package com.yigidolar.apiproject.Service;

import com.yigidolar.apiproject.Entity.PasswordResetToken;
import com.yigidolar.apiproject.Entity.User;
import com.yigidolar.apiproject.Repository.PasswordResetTokenRepository;
import com.yigidolar.apiproject.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final LoginAttemptService loginAttemptService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;


    @SneakyThrows
    public void register(String username, String password, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Kullanıcı zaten var");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        userRepository.save(user);
        emailService.sendVerifyEmail(user.getEmail(), user.getUsername(), user.getId());
    }

    public User login(String username, String password, String email) {
        if (loginAttemptService.isBlocked(email)) {
            throw new RuntimeException("Çok fazla başarısız deneme. Lütfen daha sonra tekrar deneyin.");
        }


        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("E posta ile kullanıcı bulunamadı"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.loginFailed(email);
            throw new RuntimeException("Şifre yanlış");
        }
        if (!user.isVerified()) {
            throw new RuntimeException("Lütfen e-posta adresinizi doğrulayın");
        }
        loginAttemptService.loginSucceeded(email);
        return user;

    }

    public void markEmailAsVerified(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    public void createPasswordResetToken(User user, String token, String verifyCode) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUserID(user.getId());
        resetToken.setExpiryDate(Instant.now().plusSeconds(3600));
        resetToken.setCode(verifyCode);

        passwordResetTokenRepository.save(resetToken);
    }

    public boolean verifyPasswordResetCode(User user, String code) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByUserID(user.getId()).orElseThrow(() -> new RuntimeException("Bu kullanıcının sıfırlama isteği bulunamadı."));
        return resetToken.getCode().equals(code);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Geçersiz token"));

        User users = userRepository.findById(resetToken.getUserID()).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Token süresi dolmuş");
        }

        User user = users;
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);


        passwordResetTokenRepository.delete(resetToken);
    }
}