package com.example.antaliya_taxi_service.dto.Booking;

import com.example.antaliya_taxi_service.enums.TripType;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateDTO {

    // === ЛИЧНАЯ ИНФОРМАЦИЯ КЛИЕНТА ===

    @NotBlank(message = "Имя обязательно")
    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    private String customerName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    private String customerEmail;

    @NotBlank(message = "Телефон обязателен")
    @Size(max = 20, message = "Телефон не должен превышать 20 символов")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Некорректный формат телефона")
    private String customerPhone;

    // === СВЯЗИ ===

    @NotNull(message = "Выберите автомобиль")
    private Long vehicleId;

    private Long routeId; // Для такси (опционально)

    private Long tourId; // Для туров (опционально)

    // === ТИП ПОЕЗДКИ ===

    @NotNull(message = "Выберите тип поездки")
    private TripType tripType;

    // === ОСНОВНОЙ ТРАНСФЕР ===

    @NotBlank(message = "Место получения обязательно")
    @Size(max = 200, message = "Место получения не должно превышать 200 символов")
    private String pickupLocation;

    @NotBlank(message = "Место назначения обязательно")
    @Size(max = 200, message = "Место назначения не должно превышать 200 символов")
    private String dropoffLocation;

    @NotNull(message = "Дата и время отправления обязательны")
    @Future(message = "Дата отправления должна быть в будущем")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime departureDateTime;

    @Size(max = 20, message = "Номер рейса не должен превышать 20 символов")
    private String flightNumber;

    @Size(max = 300, message = "Адрес отеля не должен превышать 300 символов")
    private String hotelAddress;

    // === ОБРАТНЫЙ ТРАНСФЕР ===

    @Builder.Default
    private Boolean hasReturnTransfer = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime returnDateTime;

    @Size(max = 20, message = "Номер обратного рейса не должен превышать 20 символов")
    private String returnFlightNumber;

    @Size(max = 10, message = "Время встречи не должно превышать 10 символов")
    private String returnMeetingTime;

    @Size(max = 200, message = "Место получения (обратно) не должно превышать 200 символов")
    private String returnPickupLocation;

    @Size(max = 200, message = "Место назначения (обратно) не должно превышать 200 символов")
    private String returnDropoffLocation;

    // === ПАССАЖИРЫ ===

    @NotNull(message = "Количество взрослых обязательно")
    @Min(value = 1, message = "Минимум 1 взрослый")
    @Max(value = 20, message = "Максимум 20 взрослых")
    @Builder.Default
    private Integer adultCount = 1;

    @Min(value = 0, message = "Количество детей не может быть отрицательным")
    @Max(value = 10, message = "Максимум 10 детей")
    @Builder.Default
    private Integer childCount = 0;

    // === ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ===

    @Builder.Default
    private Boolean needsChildSeat = false;

    @Builder.Default
    private Boolean needsNameGreeting = false;

    @Size(max = 1000, message = "Особые пожелания не должны превышать 1000 символов")
    private String specialRequests;

    // === ВАЛИДАЦИЯ ===

    @AssertTrue(message = "При обратном трансфере обязательно указать дату возврата")
    public boolean isReturnDateValid() {
        if (hasReturnTransfer != null && hasReturnTransfer) {
            return returnDateTime != null;
        }
        return true;
    }

    @AssertTrue(message = "Общее количество пассажиров не должно превышать вместимость автомобиля")
    public boolean isPassengerCountValid() {
        if (adultCount != null && childCount != null) {
            return (adultCount + childCount) <= 20; // Будет проверяться отдельно с автомобилем
        }
        return true;
    }


}
