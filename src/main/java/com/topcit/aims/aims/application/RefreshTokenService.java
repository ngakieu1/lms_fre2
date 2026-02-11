package com.topcit.aims.aims.application;

import com.topcit.aims.aims.domain.repository.RefreshTokenRepository;
import com.topcit.aims.aims.infrastructure.persistence.entity.RefreshToken;
import com.topcit.aims.aims.infrastructure.persistence.entity.UserJpaEntity;
import com.topcit.aims.aims.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

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
    public String refreshAccessToken(String refreshTokenValue){
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(refreshTokenValue)
                .orElseThrow(()-> new RuntimeException("Refresh token not found"));
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }
        UserJpaEntity user = refreshToken.getUser();
        return jwtProvider.generateAccessToken(
                user.getUsername(),
                user.getRole()
        );
    }

    @Transactional
    public void logout(String refreshToken){
        refreshTokenRepository.deleteByToken(refreshToken);
    }
}
