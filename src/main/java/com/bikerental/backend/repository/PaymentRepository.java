package com.bikerental.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bikerental.backend.entity.PaymentEntity;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
    Optional<PaymentEntity> findByRazorpayOrderId(String razorpayOrderId);
    Optional<PaymentEntity> findByRazorpayPaymentId(String razorpayPaymentId);
    Optional<PaymentEntity> findByBookingId(String bookingId);
}
