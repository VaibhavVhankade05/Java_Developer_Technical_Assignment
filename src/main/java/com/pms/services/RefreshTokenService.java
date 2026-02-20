package com.pms.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pms.entities.RefreshToken;
import com.pms.entities.User;
import com.pms.repositories.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService 
{

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(User user) {
        
        try {
            refreshTokenRepository.deleteByUserId(user.getId());
            log.debug("Deleted existing tokens for user: {}", user.getId());
        } catch (Exception e) {
            log.error("Error deleting existing tokens: {}", e.getMessage());
        }

        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .user(user)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(token);
        log.debug("Created new refresh token for user: {}", user.getId());
        
        return savedToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            log.warn("Refresh token expired: {}", token.getToken());
            throw new RuntimeException("Refresh token expired");
        }
        log.debug("Token verified: {}", token.getToken());
        return token;
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.debug("Deleted tokens for user: {}", userId);
    }
}