package com.bikerental.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bikerental.backend.dto.AuthResponse;
import com.bikerental.backend.dto.UserDTO;
import com.bikerental.backend.dto.VerifyOtpRequest;
import com.bikerental.backend.entity.UserEntity;
import com.bikerental.backend.entity.UserRole;
import com.bikerental.backend.entity.VerificationStatus;
import com.bikerental.backend.repository.UserRepository;
import com.bikerental.backend.security.JwtTokenProvider;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        String phoneNumber = request.getPhoneNumber();
        
        UserEntity user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseGet(() -> createNewUser(phoneNumber));

        user.setLastLogin(LocalDateTime.now());
        user = userRepository.save(user);

        String token = tokenProvider.generateToken(
            user.getId(),
            user.getPhoneNumber(),
            user.getRole().toString()
        );

        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .user(mapUserToDTO(user))
            .message("Login successful")
            .success(true)
            .build();
    }

    private UserEntity createNewUser(String phoneNumber) {
        UserEntity newUser = UserEntity.builder()
            .phoneNumber(phoneNumber)
            .role(UserRole.CUSTOMER)
            .verificationStatus(VerificationStatus.PENDING)
            .build();
        return userRepository.save(newUser);
    }

    public UserDTO getCurrentUser(String userId) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return mapUserToDTO(user);
    }

    public UserDTO updateUserProfile(String userId, UserDTO userDTO) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getFullName() != null) user.setFullName(userDTO.getFullName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());

        user = userRepository.save(user);
        return mapUserToDTO(user);
    }

    private UserDTO mapUserToDTO(UserEntity user) {
        return UserDTO.builder()
            .id(user.getId())
            .phoneNumber(user.getPhoneNumber())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .role(user.getRole())
            .verificationStatus(user.getVerificationStatus())
            .aadharVerified(user.getAadharVerified())
            .licenseVerified(user.getLicenseVerified())
            .panVerified(user.getPanVerified())
            .physicalVerificationOneTime(user.getPhysicalVerificationOneTime())
            .profilePhotoUrl(user.getProfilePhotoUrl())
            .lastLogin(user.getLastLogin())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}
