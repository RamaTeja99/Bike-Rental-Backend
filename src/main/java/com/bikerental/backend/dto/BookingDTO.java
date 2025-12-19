package com.bikerental.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bikerental.backend.entity.BookingStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private String id;
    private String userId;
    private String bikeId;
    private String bikeName;
    private String bikeModel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime createdAt;
}

