package com.topcit.aims.aims.application;

import com.topcit.aims.aims.domain.repository.UserRepository;
import com.topcit.aims.aims.infrastructure.persistence.entity.RefreshToken;
import com.topcit.aims.aims.infrastructure.persistence.entity.UserJpaEntity;
import com.topcit.aims.aims.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;


    public Map<String, String> login(String username, String rawPassword) {

        UserJpaEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtProvider.generateAccessToken(
                user.getUsername(), user.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken(),
                "role", user.getRole().getName()
        );
    }
}