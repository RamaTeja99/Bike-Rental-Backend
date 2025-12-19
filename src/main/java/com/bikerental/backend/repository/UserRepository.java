package com.bikerental.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bikerental.backend.entity.UserEntity;
import com.bikerental.backend.entity.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByRole(UserRole role);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
