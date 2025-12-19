package com.bikerental.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bikerental.backend.entity.BookingEntity;
import com.bikerental.backend.entity.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, String> {
    Page<BookingEntity> findByUserId(String userId, Pageable pageable);
    Page<BookingEntity> findByUserIdAndStatus(String userId, BookingStatus status, Pageable pageable);
    List<BookingEntity> findByBikeId(String bikeId);
    List<BookingEntity> findByBikeIdAndStatus(String bikeId, BookingStatus status);
    Page<BookingEntity> findByStatus(BookingStatus status, Pageable pageable);
    
    @Query("SELECT b FROM BookingEntity b WHERE b.bike.id = ?1 AND b.status IN ('PENDING', 'IN_PROGRESS')")
    List<BookingEntity> findActiveBookingsByBikeId(String bikeId);
    
    @Query("""
       SELECT COUNT(b) FROM BookingEntity b
       WHERE b.bike.id = :bikeId
         AND (
              (b.startTime < :endTime AND b.endTime > :startTime)
           OR (b.startTime >= :startTime AND b.startTime < :endTime)
         )
       """)
long countOverlappingBookings(@Param("bikeId") String bikeId,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);

}
