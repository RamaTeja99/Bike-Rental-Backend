package com.bikerental.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bikerental.backend.dto.ApiResponse;
import com.bikerental.backend.dto.BookingDTO;
import com.bikerental.backend.service.BookingService;

@RestController
@RequestMapping("/api/verifier")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Slf4j
@PreAuthorize("hasRole('VERIFIER')")
public class VerifierController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/bookings/{bookingId}/complete")
    public ResponseEntity<ApiResponse<BookingDTO>> completeBooking(
        @PathVariable String bookingId) {
        try {
            BookingDTO booking = bookingService.completeBooking(bookingId);
            return ResponseEntity.ok(ApiResponse.<BookingDTO>builder()
                .data(booking)
                .message("Booking completed by verifier")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error completing booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<BookingDTO>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }
}
