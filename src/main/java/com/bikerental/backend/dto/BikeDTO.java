package com.bikerental.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bikerental.backend.entity.BikeStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeDTO {
    private String id;
    private String model;
    private String brand;
    private String registrationNumber;
    private BigDecimal pricePerHour;
    private BikeStatus status;
    private String currentLocation;
    private Integer mileage;
    private String bikePhotoUrl;
    private String description;
    private Integer yearOfManufacture;
    private String color;
}

