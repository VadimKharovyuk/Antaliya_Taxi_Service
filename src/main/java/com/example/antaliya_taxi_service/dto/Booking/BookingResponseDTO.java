package com.example.antaliya_taxi_service.dto.Booking;
import com.example.antaliya_taxi_service.dto.vehicle.VehicleCardDto;
import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.enums.TripType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {

    private Long id;

    // === ЛИЧНАЯ ИНФОРМАЦИЯ КЛИЕНТА ===

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // === СВЯЗИ (упрощенные данные) ===

    private Long vehicleId;
    private VehicleCardDto vehicle; // Информация об автомобиле
    private Long routeId;
    private String routeName; // Название маршрута
    private Long tourId;
    private String tourName; // Название тура

    // === ТИП ПОЕЗДКИ ===

    private TripType tripType;

    // === ОСНОВНОЙ ТРАНСФЕР ===

    private String pickupLocation;
    private String dropoffLocation;
    private LocalDateTime departureDateTime;
    private String flightNumber;
    private String hotelAddress;

    // === ОБРАТНЫЙ ТРАНСФЕР ===

    private Boolean hasReturnTransfer;
    private LocalDateTime returnDateTime;
    private String returnFlightNumber;
    private String returnMeetingTime;
    private String returnPickupLocation;
    private String returnDropoffLocation;

    // === ПАССАЖИРЫ ===

    private Integer adultCount;
    private Integer childCount;
    private Integer totalPassengers; // Вычисляемое поле

    // === ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ===

    private Boolean needsChildSeat;
    private Boolean needsNameGreeting;
    private String specialRequests;

    // === ЦЕНООБРАЗОВАНИЕ ===

    private BigDecimal basePrice;
    private BigDecimal vehicleMultiplier;
    private BigDecimal tripMultiplier;
    private BigDecimal totalPrice;
    private Currency currency;
    private String formattedPrice; // "150.00 USD"

    // === СТАТУС И УПРАВЛЕНИЕ ===

    private BookingStatus status;
    private String bookingReference;
    private String adminNotes;

    // === АУДИТ ===

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime completedAt;

    // === ВЫЧИСЛЯЕМЫЕ ПОЛЯ ===

    private String statusDisplayName; // "Ожидает подтверждения"
    private String formattedDepartureDate; // "25 декабря 2024, 14:30"
    private String formattedReturnDate; // "28 декабря 2024, 16:00"
    private Boolean canBeCancelled; // Можно ли отменить
    private Boolean canBeModified; // Можно ли изменить
}
