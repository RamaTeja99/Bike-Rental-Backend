package com.bikerental.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bikerental.backend.entity.BikeEntity;
import com.bikerental.backend.entity.BikeStatus;

import java.util.List;

@Repository
public interface BikeRepository extends JpaRepository<BikeEntity, String> {
    Page<BikeEntity> findByStatus(BikeStatus status, Pageable pageable);
    Page<BikeEntity> findByBrand(String brand, Pageable pageable);
    Page<BikeEntity> findByBrandAndStatus(String brand, BikeStatus status, Pageable pageable);
    List<BikeEntity> findByStatusAndCurrentLocation(BikeStatus status, String location);
    Page<BikeEntity> findByModelContainingIgnoreCase(String model, Pageable pageable);
    Page<BikeEntity> findByModelContainingIgnoreCaseOrBrandContainingIgnoreCase(
        String model, String brand, Pageable pageable);
}
