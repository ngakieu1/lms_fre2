package com.topcit.aims.aims.presentation.controller;

import com.topcit.aims.aims.application.LoginService;
import com.topcit.aims.aims.application.LoginUseCase;
import com.topcit.aims.aims.application.OtpService;
import com.topcit.aims.aims.application.RefreshTokenService;
import com.topcit.aims.aims.domain.model.User;
import com.topcit.aims.aims.domain.repository.UserRepository;
import com.topcit.aims.aims.dto.request.LoginRequest;
import com.topcit.aims.aims.dto.request.LogoutRequest;
import com.topcit.aims.aims.dto.request.RefreshTokenRequest;
import com.topcit.aims.aims.dto.request.ResetPasswordRequest;
import com.topcit.aims.aims.infrastructure.persistence.entity.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OtpService otpService;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request){
        String newAccessToken = refreshTokenService.refreshAccessToken(
                request.getRefreshToken()
        );
        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request){
        String studentId = request.get("studentId");
        String phoneNumber = request.get("phoneNumber");
        UserJpaEntity user = userRepository.findByUsername(studentId)
                .orElseThrow(() -> new RuntimeException("Student ID not found"));
        if (user.getPhoneNumber()==null || !user.getPhoneNumber().equals(phoneNumber)){
            return ResponseEntity.badRequest().body(Map.of("message", "Phone number does not match records"));
        }
        String otp = otpService.generateOtp(studentId);
        otpService.sendSms(phoneNumber, otp);
        return ResponseEntity.ok(Map.of("message", "OTP sent successfully"));
    }
    @PostMapping("/forgot-password/verify")
    public ResponseEntity<?> verifyAndReset(@RequestBody ResetPasswordRequest request) {
        boolean isValid = otpService.validateOtp(request.getStudentId(), request.getOtp());
        if (!isValid) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid OTP"));
        }
        UserJpaEntity user = userRepository.findByUsername(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student ID not found"));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        otpService.clearOtp(request.getStudentId());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }
}

