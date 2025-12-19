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
import com.bikerental.backend.dto.BikeDTO;
import com.bikerental.backend.service.BikeService;

@RestController
@RequestMapping("/api/bikes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@Slf4j
public class BikeController {

    @Autowired
    private BikeService bikeService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BikeDTO>>> getAvailableBikes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BikeDTO> bikes = bikeService.getAvailableBikes(pageable);
            return ResponseEntity.ok(ApiResponse.<Page<BikeDTO>>builder()
                .data(bikes)
                .message("Bikes fetched successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error fetching bikes", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Page<BikeDTO>>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BikeDTO>>> searchBikes(
        @RequestParam String query,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BikeDTO> bikes = bikeService.searchBikes(query, pageable);
            return ResponseEntity.ok(ApiResponse.<Page<BikeDTO>>builder()
                .data(bikes)
                .message("Search results fetched successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error searching bikes", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Page<BikeDTO>>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }

    @GetMapping("/{bikeId}")
    public ResponseEntity<ApiResponse<BikeDTO>> getBikeById(@PathVariable String bikeId) {
        try {
            BikeDTO bike = bikeService.getBikeById(bikeId);
            return ResponseEntity.ok(ApiResponse.<BikeDTO>builder()
                .data(bike)
                .message("Bike fetched successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error fetching bike", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<BikeDTO>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(404)
                    .build());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BikeDTO>> createBike(@RequestBody BikeDTO bikeDTO) {
        try {
            BikeDTO created = bikeService.createBike(bikeDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BikeDTO>builder()
                    .data(created)
                    .message("Bike created successfully")
                    .success(true)
                    .statusCode(201)
                    .build());
        } catch (Exception e) {
            log.error("Error creating bike", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<BikeDTO>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }

    @PutMapping("/{bikeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BikeDTO>> updateBike(
        @PathVariable String bikeId,
        @RequestBody BikeDTO bikeDTO) {
        try {
            BikeDTO updated = bikeService.updateBike(bikeId, bikeDTO);
            return ResponseEntity.ok(ApiResponse.<BikeDTO>builder()
                .data(updated)
                .message("Bike updated successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error updating bike", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<BikeDTO>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }

    @DeleteMapping("/{bikeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBike(@PathVariable String bikeId) {
        try {
            bikeService.deleteBike(bikeId);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Bike deleted successfully")
                .success(true)
                .statusCode(200)
                .build());
        } catch (Exception e) {
            log.error("Error deleting bike", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .message(e.getMessage())
                    .success(false)
                    .statusCode(400)
                    .build());
        }
    }
}
