package com.topcit.aims.aims.application;

import com.topcit.aims.aims.domain.repository.RefreshTokenRepository;
import com.topcit.aims.aims.infrastructure.persistence.entity.RefreshToken;
import com.topcit.aims.aims.infrastructure.persistence.entity.UserJpaEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private final long REFRESH_EXPIRE_DAYS = 7; // ton tai trong vong 7 ngay

    @Transactional
    public RefreshToken createRefreshToken(UserJpaEntity user) {

        refreshTokenRepository.deleteByUser(user); // mỗi user 1 refresh token

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(REFRESH_EXPIRE_DAYS));

        return refreshTokenRepository.save(refreshToken);
    }
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }
    @Transactional
    public void logout(String refreshToken){
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
