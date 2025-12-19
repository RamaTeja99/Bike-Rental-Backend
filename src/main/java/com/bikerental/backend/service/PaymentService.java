package com.bikerental.backend.service;

import com.bikerental.backend.dto.CreateOrderResponse;
import com.bikerental.backend.dto.VerifyPaymentRequest;
import com.bikerental.backend.dto.VerifyPaymentResponse;
import com.bikerental.backend.entity.BookingEntity;
import com.bikerental.backend.entity.BookingStatus;
import com.bikerental.backend.entity.PaymentEntity;
import com.bikerental.backend.entity.PaymentStatus;
import com.bikerental.backend.repository.BookingRepository;
import com.bikerental.backend.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    @Autowired
    public void initRazorpayClient() {
        try {
            this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        } catch (RazorpayException e) {
            log.error("Failed to initialize Razorpay client", e);
        }
    }

    public CreateOrderResponse createOrder(String bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", booking.getTotalAmount()
                    .multiply(java.math.BigDecimal.valueOf(100))
                    .longValue());
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt#" + bookingId);

            JSONObject notes = new JSONObject();
            notes.put("bookingId", bookingId);
            notes.put("userId", booking.getUser().getId());
            notes.put("bikeId", booking.getBike().getId());
            orderRequest.put("notes", notes);

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");

            PaymentEntity payment = PaymentEntity.builder()
                .booking(booking)
                .razorpayOrderId(orderId)
                .amount(booking.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .build();
            paymentRepository.save(payment);

            return CreateOrderResponse.builder()
                .orderId(orderId)
                .bookingId(bookingId)
                .amount(booking.getTotalAmount()
                    .multiply(java.math.BigDecimal.valueOf(100))
                    .longValue())
                .currency("INR")
                .keyId(razorpayKeyId)
                .success(true)
                .message("Order created successfully")
                .build();
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order", e);
            throw new RuntimeException("Failed to create payment order");
        }
    }

    public VerifyPaymentResponse verifyPayment(VerifyPaymentRequest request) {
        try {
            String generatedSignature = generateSignature(
                request.getOrderId(),
                request.getPaymentId(),
                razorpayKeySecret
            );

            if (!generatedSignature.equals(request.getSignature())) {
                log.error("Invalid payment signature");
                return VerifyPaymentResponse.builder()
                    .status("FAILED")
                    .message("Payment verification failed")
                    .success(false)
                    .build();
            }

            PaymentEntity payment = paymentRepository.findByRazorpayOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

            payment.setRazorpayPaymentId(request.getPaymentId());
            payment.setRazorpaySignature(request.getSignature());
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            BookingEntity booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            return VerifyPaymentResponse.builder()
                .bookingId(booking.getId())
                .paymentId(request.getPaymentId())
                .status("SUCCESS")
                .message("Payment verified successfully")
                .success(true)
                .build();
        } catch (Exception e) {
            log.error("Error verifying payment", e);
            return VerifyPaymentResponse.builder()
                .status("FAILED")
                .message("Payment verification error")
                .success(false)
                .build();
        }
    }

    private String generateSignature(String orderId, String paymentId, String secret)
        throws NoSuchAlgorithmException, InvalidKeyException {
        String payload = orderId + "|" + paymentId;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hmacData = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hmacData) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
