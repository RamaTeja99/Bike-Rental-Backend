package com.bikerental.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bikerental.backend.dto.ApiResponse;
import com.bikerental.backend.dto.CreateOrderResponse;
import com.bikerental.backend.dto.VerifyPaymentRequest;
import com.bikerental.backend.dto.VerifyPaymentResponse;
import com.bikerental.backend.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
        @RequestParam String bookingId) {
        try {
            log.info("Creating Razorpay order for booking: {}", bookingId);
            CreateOrderResponse response = paymentService.createOrder(bookingId);
            
            return ResponseEntity.ok(ApiResponse.<CreateOrderResponse>builder()
                .data(response)
                .message("Order created successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error creating order", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<CreateOrderResponse>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<ApiResponse<VerifyPaymentResponse>> verifyPayment(
        @Valid @RequestBody VerifyPaymentRequest request) {
        try {
            log.info("Verifying payment for order: {}", request.getOrderId());
            VerifyPaymentResponse response = paymentService.verifyPayment(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.<VerifyPaymentResponse>builder()
                    .data(response)
                    .message("Payment verified successfully")
                    .success(true)
                    .statusCode(200)
                    .build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<VerifyPaymentResponse>builder()
                        .data(response)
                        .message("Payment verification failed")
                        .success(false)
                        .statusCode(400)
                        .build());
            }
        } catch (Exception e) {
            log.error("Error verifying payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<VerifyPaymentResponse>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }
}
