package com.topcit.aims.aims.security;

import com.topcit.aims.aims.infrastructure.persistence.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Component
public class JwtProvider {

    // >= 32 bytes
    private static final String SECRET_KEY =
            "mYB8JpJ7p6m7N9rD3x8Qf4tQ1hL2vS8jU5aM3kZ9rQ8xT2cV1oH4dN0bW6yR7qP9";

    private static final long ACCESS_EXPIRE = 30*60*1000; // ton tai trong 30 phut

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", "ROLE_" + role.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRE))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }
    public String getRoleFromToken(String token){
        return getClaims(token).get("role", String.class);
    }
}

