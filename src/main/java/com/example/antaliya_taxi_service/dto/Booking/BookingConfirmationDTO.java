package com.example.antaliya_taxi_service.dto.Booking;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingConfirmationDTO {

    private String bookingReference;
    private String customerName;
    private String customerEmail;

    // === ДЕТАЛИ ПОЕЗДКИ ===

    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime departureDateTime;
    private String formattedDepartureDate;

    // === АВТОМОБИЛЬ ===

    private String vehicleName;
    private String vehicleClass;
    private String vehicleImageUrl;

    // === ЦЕНА ===

    private BigDecimal totalPrice;
    private String formattedPrice;
    private String currency;

    // === КОНТАКТЫ ===

    private String companyPhone;
    private String companyEmail;
    private String emergencyPhone;

    // === ИНСТРУКЦИИ ===

    private String pickupInstructions;
    private String paymentInstructions;
    private String cancellationPolicy;

    // === ДОПОЛНИТЕЛЬНО ===

    private Boolean hasReturnTransfer;
    private LocalDateTime returnDateTime;
    private String formattedReturnDate;

    private Integer totalPassengers;
    private Boolean needsChildSeat;
    private String specialRequests;
}