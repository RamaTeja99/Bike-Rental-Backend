package com.bikerental.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bikerental.backend.dto.ApiResponse;
import com.bikerental.backend.dto.AuthResponse;
import com.bikerental.backend.dto.UserDTO;
import com.bikerental.backend.dto.VerifyOtpRequest;
import com.bikerental.backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
        @Valid @RequestBody VerifyOtpRequest request) {
        try {
            log.info("Verifying OTP for phone: {}", request.getPhoneNumber());
            AuthResponse response = authService.verifyOtp(request);
            return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .data(response)
                .message("OTP verified successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error verifying OTP", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<AuthResponse>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(
        @RequestHeader("Authorization") String token) {
        try {
            // Token is already validated by JwtAuthenticationFilter
            String userId = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
            
            UserDTO user = authService.getCurrentUser(userId);
            return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .data(user)
                .message("User fetched successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error fetching current user", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<UserDTO>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(401)
                    .build());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody UserDTO userDTO) {
        try {
            String userId = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
            
            UserDTO updated = authService.updateUserProfile(userId, userDTO);
            return ResponseEntity.ok(ApiResponse.<UserDTO>builder()
                .data(updated)
                .message("Profile updated successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error updating profile", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<UserDTO>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }
}

