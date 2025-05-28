package com.example.antaliya_taxi_service.dto.Booking;
import com.example.antaliya_taxi_service.enums.TripType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteBookingCreateDTO {

    // === СВЯЗИ С ДРУГИМИ СУЩНОСТЯМИ ===

    @NotNull(message = "Маршрут должен быть выбран")
    private Long routeId;

    @NotNull(message = "Автомобиль должен быть выбран")
    private Long vehicleId;

    // === ЛИЧНАЯ ИНФОРМАЦИЯ КЛИЕНТА ===

    @NotBlank(message = "Имя и фамилия обязательны")
    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    private String customerName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Введите корректный email")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    private String customerEmail;

    @NotBlank(message = "Номер телефона обязателен")
    @Size(max = 20, message = "Номер телефона не должен превышать 20 символов")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]+$", message = "Введите корректный номер телефона")
    private String customerPhone;

    // === ТИП ПОЕЗДКИ ===

    @NotNull(message = "Тип поездки должен быть выбран")
    private TripType tripType;

    // === ОСНОВНОЙ ТРАНСФЕР (предзаполняется из Route) ===

    private String pickupLocation;     // Заполняется автоматически из Route
    private String dropoffLocation;    // Заполняется автоматически из Route

    @NotNull(message = "Дата и время отправления обязательны")
    @Future(message = "Дата отправления должна быть в будущем")
    private LocalDateTime departureDateTime;

    @Size(max = 20, message = "Номер рейса не должен превышать 20 символов")
    private String flightNumber;

    @Size(max = 300, message = "Адрес отеля не должен превышать 300 символов")
    private String hotelAddress;

    // === ОБРАТНЫЙ ТРАНСФЕР (только для ROUND_TRIP) ===

    @Builder.Default
    private Boolean hasReturnTransfer = false;

    private LocalDateTime returnDateTime;

    @Size(max = 20, message = "Номер обратного рейса не должен превышать 20 символов")
    private String returnFlightNumber;

    @Size(max = 10, message = "Время встречи не должно превышать 10 символов")
    private String returnMeetingTime;

    @Size(max = 200, message = "Место получения не должно превышать 200 символов")
    private String returnPickupLocation;

    @Size(max = 200, message = "Место назначения не должно превышать 200 символов")
    private String returnDropoffLocation;

    // === ПАССАЖИРЫ ===

    @NotNull(message = "Количество взрослых обязательно")
    @Min(value = 1, message = "Должен быть минимум 1 взрослый")
    @Max(value = 10, message = "Максимум 10 взрослых")
    @Builder.Default
    private Integer adultCount = 1;

    @NotNull(message = "Количество детей обязательно")
    @Min(value = 0, message = "Количество детей не может быть отрицательным")
    @Max(value = 8, message = "Максимум 8 детей")
    @Builder.Default
    private Integer childCount = 0;

    // === ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ===

    @Builder.Default
    private Boolean needsChildSeat = false;

    @Builder.Default
    private Boolean needsNameGreeting = false;

    @Size(max = 1000, message = "Особые пожелания не должны превышать 1000 символов")
    private String specialRequests;

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===

    /**
     * Получить общее количество пассажиров
     */
    public Integer getTotalPassengers() {
        return (adultCount != null ? adultCount : 0) + (childCount != null ? childCount : 0);
    }

    /**
     * Проверить, является ли поездка туда-обратно
     */
    public boolean isRoundTrip() {
        return TripType.ROUND_TRIP.equals(tripType);
    }

    /**
     * Проверить, нужна ли встреча в аэропорту
     */
    public boolean isAirportPickup() {
        return flightNumber != null && !flightNumber.trim().isEmpty();
    }

    /**
     * Проверить, указан ли адрес отеля
     */
    public boolean hasHotelAddress() {
        return hotelAddress != null && !hotelAddress.trim().isEmpty();
    }
}