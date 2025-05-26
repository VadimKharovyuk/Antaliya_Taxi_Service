package com.example.antaliya_taxi_service.repository;

import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Поиск бронирования по номеру
     */
    Optional<Booking> findByBookingReference(String bookingReference);

    /**
     * Проверка существования бронирования с данным номером
     */
    boolean existsByBookingReference(String bookingReference);



    /**
     * ИСПРАВЛЕННЫЙ ЗАПРОС: Поиск активных бронирований для автомобиля в указанном диапазоне времени
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.vehicle.id = :vehicleId " +
            "AND b.status IN (:activeStatuses) " +
            "AND ((b.departureDateTime BETWEEN :startTime AND :endTime) " +
            "OR (b.returnDateTime BETWEEN :startTime AND :endTime))")
    long countByVehicleIdAndDateTimeRange(@Param("vehicleId") Long vehicleId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("activeStatuses") List<BookingStatus> activeStatuses);



    Optional<Booking> findByBookingReferenceAndCustomerEmailIgnoreCase(String normalizedReference, String normalizedEmail);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :lastWeek")
    Long getNewBookingsCount(@Param("lastWeek") LocalDateTime lastWeek);
}