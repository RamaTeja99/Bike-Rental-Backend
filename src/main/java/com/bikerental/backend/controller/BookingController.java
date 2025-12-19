package com.bikerental.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bikerental.backend.dto.ApiResponse;
import com.bikerental.backend.dto.BookingDTO;
import com.bikerental.backend.dto.CreateBookingRequest;
import com.bikerental.backend.service.BookingService;

import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Slf4j
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> createBooking(
        @Valid @RequestBody CreateBookingRequest request) {
        try {
            String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
            
            log.info("Creating booking for user: {}", userId);
            BookingDTO booking = bookingService.createBooking(userId, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BookingDTO>builder()
                    .data(booking)
                    .message("Booking created successfully")
                    .success(true)
                    .statusCode(201)
                    .build());
        } catch (Exception e) {
            log.error("Error creating booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<BookingDTO>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookingDTO>>> getMyBookings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        try {
            String userId = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
            
            Pageable pageable = PageRequest.of(page, size);
            Page<BookingDTO> bookings = bookingService.getBookingsByUser(userId, pageable);
            
            return ResponseEntity.ok(ApiResponse.<Page<BookingDTO>>builder()
                .data(bookings)
                .message("Bookings fetched successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error fetching bookings", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Page<BookingDTO>>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDTO>> getBookingById(
        @PathVariable String bookingId) {
        try {
            BookingDTO booking = bookingService.getBookingById(bookingId);
            return ResponseEntity.ok(ApiResponse.<BookingDTO>builder()
                .data(booking)
                .message("Booking fetched successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error fetching booking", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<BookingDTO>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(404)
                    .build());
        }
    }

    @PostMapping("/{bookingId}/complete")
    @PreAuthorize("hasRole('VERIFIER')")
    public ResponseEntity<ApiResponse<BookingDTO>> completeBooking(
        @PathVariable String bookingId) {
        try {
            BookingDTO booking = bookingService.completeBooking(bookingId);
            return ResponseEntity.ok(ApiResponse.<BookingDTO>builder()
                .data(booking)
                .message("Booking completed successfully")
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

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingDTO>> cancelBooking(
        @PathVariable String bookingId) {
        try {
            BookingDTO booking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(ApiResponse.<BookingDTO>builder()
                .data(booking)
                .message("Booking cancelled successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error cancelling booking", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<BookingDTO>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }
}
