package com.yigidolar.apiproject.Service;

import com.yigidolar.apiproject.Entity.RefreshToken;
import com.yigidolar.apiproject.Repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final long refreshTokenDurationMs= 7*24*60*60*1000;

    public RefreshToken createRefreshToken(String username){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepository.save(refreshToken);
    }
    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isTokenExpired(RefreshToken token){
        return token.getExpiryDate().isBefore(Instant.now());
    }
    @Transactional
    public void deleteByUsername(String username){
        refreshTokenRepository.deleteByUsername(username);
    }

}
