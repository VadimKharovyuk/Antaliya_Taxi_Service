package com.example.antaliya_taxi_service.dto.Booking;


import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.enums.TripType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCardDto {

    private Long id;
    private String bookingReference;
    private String customerName;
    private String customerPhone;

    // === МАРШРУТ ===

    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime departureDateTime;
    private String formattedDepartureDate;

    // === АВТОМОБИЛЬ ===

    private String vehicleName; // "Mercedes Vito 2024"
    private String vehicleClass;

    // === ПАССАЖИРЫ ===

    private Integer totalPassengers;

    // === ЦЕНА ===

    private BigDecimal totalPrice;
    private String formattedPrice;

    // === СТАТУС ===

    private BookingStatus status;
    private String statusDisplayName;
    private String statusColor; // Для CSS класса

    // === ТИП ===

    private TripType tripType;
    private Boolean hasReturnTransfer;

    // === ВРЕМЯ ===

    private LocalDateTime createdAt;
    private String formattedCreatedDate;
}
