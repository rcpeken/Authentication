package com.yigidolar.apiproject.Service;

import com.yigidolar.apiproject.Entity.BlacklistedToken;
import com.yigidolar.apiproject.Repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistRepo;

    public void blacklistToken(String token, Instant expiryDate) {
        var blacklistedToken = new BlacklistedToken(token, expiryDate);
        blacklistRepo.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistRepo.existsByToken(token);
    }
}
