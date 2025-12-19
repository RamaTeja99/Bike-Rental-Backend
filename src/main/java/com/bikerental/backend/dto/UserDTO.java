package com.bikerental.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.bikerental.backend.entity.UserRole;
import com.bikerental.backend.entity.VerificationStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String phoneNumber;
    private String fullName;
    private String email;
    private UserRole role;
    private VerificationStatus verificationStatus;
    private Boolean aadharVerified;
    private Boolean licenseVerified;
    private Boolean panVerified;
    private Boolean physicalVerificationOneTime;
    private String profilePhotoUrl;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
