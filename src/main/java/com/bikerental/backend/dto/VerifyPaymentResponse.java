package com.bikerental.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyPaymentResponse {
    private String bookingId;
    private String paymentId;
    private String status;
    private String message;
    private boolean success;
}
