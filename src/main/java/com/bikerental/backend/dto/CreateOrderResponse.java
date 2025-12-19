package com.bikerental.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderResponse {
    private String orderId;
    private String bookingId;
    private Long amount;
    private String currency;
    private String keyId;
    private String message;
    private boolean success;
}
