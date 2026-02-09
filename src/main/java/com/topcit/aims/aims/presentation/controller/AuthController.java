package com.topcit.aims.aims.presentation.controller;

import com.topcit.aims.aims.application.LoginService;
import com.topcit.aims.aims.application.LoginUseCase;
import com.topcit.aims.aims.application.RefreshTokenService;
import com.topcit.aims.aims.dto.request.LoginRequest;
import com.topcit.aims.aims.dto.request.LogoutRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        return loginService.login(
                request.getUsername(),
                request.getPassword()
        );
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request){
        refreshTokenService.logout(request.getRefreshToken());
        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully"
        ));
    }
}

