package com.bikerental.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bikerental.backend.dto.BookingDTO;
import com.bikerental.backend.dto.CreateBookingRequest;
import com.bikerental.backend.entity.BikeEntity;
import com.bikerental.backend.entity.BikeStatus;
import com.bikerental.backend.entity.BookingEntity;
import com.bikerental.backend.entity.BookingStatus;
import com.bikerental.backend.entity.UserEntity;
import com.bikerental.backend.entity.VerificationStatus;
import com.bikerental.backend.repository.BikeRepository;
import com.bikerental.backend.repository.BookingRepository;
import com.bikerental.backend.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BikeRepository bikeRepository;

    public BookingDTO createBooking(String userId, CreateBookingRequest request) {
        // Validate user
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Check verification status
        if (!user.getVerificationStatus().equals(VerificationStatus.VERIFIED) &&
            !user.getPhysicalVerificationOneTime()) {
            throw new RuntimeException("User verification required to book bike");
        }

        // Validate bike
        BikeEntity bike = bikeRepository.findById(request.getBikeId())
            .orElseThrow(() -> new RuntimeException("Bike not found"));

        if (!bike.getStatus().equals(BikeStatus.READY)) {
            throw new RuntimeException("Bike is not available for booking");
        }

        // Validate time
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        // Calculate total amount
        long hours = ChronoUnit.HOURS.between(request.getStartTime(), request.getEndTime());
        if (hours == 0) hours = 1; // Minimum 1 hour
        BigDecimal totalAmount = bike.getPricePerHour().multiply(BigDecimal.valueOf(hours));

        // Create booking
        BookingEntity booking = BookingEntity.builder()
            .user(user)
            .bike(bike)
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .totalAmount(totalAmount)
            .pickupLocation(request.getPickupLocation())
            .dropoffLocation(request.getDropoffLocation())
            .notes(request.getNotes())
            .build();

        booking = bookingRepository.save(booking);

        // Update bike status
        bike.setStatus(BikeStatus.IN_PROCESS);
        bikeRepository.save(bike);

        return mapBookingToDTO(booking);
    }

    public Page<BookingDTO> getBookingsByUser(String userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable)
            .map(this::mapBookingToDTO);
    }

    public Page<BookingDTO> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
            .map(this::mapBookingToDTO);
    }

    public BookingDTO getBookingById(String bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        return mapBookingToDTO(booking);
    }

    public BookingDTO completeBooking(String bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.COMPLETED);
        booking = bookingRepository.save(booking);

        // Update bike status
        BikeEntity bike = booking.getBike();
        bike.setStatus(BikeStatus.READY);
        bikeRepository.save(bike);

        // Disable one-time physical verification
        UserEntity user = booking.getUser();
        user.setPhysicalVerificationOneTime(false);
        userRepository.save(user);

        return mapBookingToDTO(booking);
    }

    public BookingDTO cancelBooking(String bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        // Update bike status
        BikeEntity bike = booking.getBike();
        bike.setStatus(BikeStatus.READY);
        bikeRepository.save(bike);

        return mapBookingToDTO(booking);
    }

    private BookingDTO mapBookingToDTO(BookingEntity booking) {
        return BookingDTO.builder()
            .id(booking.getId())
            .userId(booking.getUser().getId())
            .bikeId(booking.getBike().getId())
            .bikeName(booking.getBike().getModel())
            .bikeModel(booking.getBike().getModel())
            .startTime(booking.getStartTime())
            .endTime(booking.getEndTime())
            .totalAmount(booking.getTotalAmount())
            .status(booking.getStatus())
            .pickupLocation(booking.getPickupLocation())
            .dropoffLocation(booking.getDropoffLocation())
            .createdAt(booking.getCreatedAt())
            .build();
    }
}
