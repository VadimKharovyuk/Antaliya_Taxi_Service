package com.example.antaliya_taxi_service.dto.Booking;


import com.example.antaliya_taxi_service.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingNotificationDTO {

    private String bookingReference;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // === СТАТУС ===

    private BookingStatus oldStatus;
    private BookingStatus newStatus;
    private String statusChangeReason;

    // === ДЕТАЛИ ===

    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime departureDateTime;
    private String vehicleName;

    // === УВЕДОМЛЕНИЕ ===

    private String notificationType; // EMAIL, SMS, PUSH
    private String subject;
    private String message;
    private String actionUrl; // Ссылка для действий

    // === КОНТАКТЫ ===

    private String driverName;
    private String driverPhone;
    private String companyPhone;
}
