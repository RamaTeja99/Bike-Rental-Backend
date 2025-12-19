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
import com.bikerental.backend.service.BookingService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<Page<BookingDTO>>> getAllBookings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BookingDTO> bookings = bookingService.getAllBookings(pageable);
            
            return ResponseEntity.ok(ApiResponse.<Page<BookingDTO>>builder()
                .data(bookings)
                .message("All bookings fetched successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error fetching all bookings", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Page<BookingDTO>>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }
}
