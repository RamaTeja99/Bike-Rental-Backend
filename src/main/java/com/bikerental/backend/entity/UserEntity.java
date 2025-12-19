package com.bikerental.backend.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private String fullName;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    // Document Verification
    private Boolean aadharVerified;
    private String aadharNumber;
    private String aadharUrl;

    private Boolean licenseVerified;
    private String licenseNumber;
    private String licenseUrl;

    private Boolean panVerified;
    private String panNumber;
    private String panUrl;

    // Physical Verification (one-time use)
    private Boolean physicalVerificationOneTime;

    // DigiLocker
    private Boolean digiLockerConnected;
    private String digiLockerId;

    // Profile
    private String profilePhotoUrl;
    private LocalDateTime lastLogin;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<BookingEntity> bookings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VerificationEntity> verifications;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        verificationStatus = VerificationStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

