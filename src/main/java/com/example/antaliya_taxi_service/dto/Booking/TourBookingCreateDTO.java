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
public class TourBookingCreateDTO {

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

    // === ОБЯЗАТЕЛЬНЫЕ СВЯЗИ ДЛЯ ТУРА ===
    @NotNull(message = "Выберите тур")
    private Long tourId;

    @NotNull(message = "Выберите автомобиль")
    private Long vehicleId;

    // === ТИП ПОЕЗДКИ ФИКСИРОВАННЫЙ ===
    @Builder.Default
    private TripType tripType = TripType.TOUR;

    // === ДАТА И ВРЕМЯ ТУРА ===
    @NotNull(message = "Дата и время тура обязательны")
    @Future(message = "Дата тура должна быть в будущем")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime tourDateTime;

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

    // === ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ДЛЯ ТУРОВ ===
    @Builder.Default
    private Boolean needsChildSeat = false;

    @Builder.Default
    private Boolean needsNameGreeting = false;

    @Size(max = 1000, message = "Особые пожелания не должны превышать 1000 символов")
    private String specialRequests;

    // === КОНТАКТНАЯ ИНФОРМАЦИЯ (опционально для туров) ===
    @Size(max = 300, message = "Адрес отеля не должен превышать 300 символов")
    private String hotelAddress;


    // === ВАЛИДАЦИЯ ===
    @AssertTrue(message = "Общее количество пассажиров не должно превышать 20")
    public boolean isPassengerCountValid() {
        if (adultCount != null && childCount != null) {
            return (adultCount + childCount) <= 20;
        }
        return true;
    }

    // === МЕТОДЫ ДЛЯ ПРЕОБРАЗОВАНИЯ В ОБЩИЙ BookingCreateDTO ===
    public BookingCreateDTO toBookingCreateDTO() {
        return BookingCreateDTO.builder()
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .vehicleId(vehicleId)
                .tourId(tourId)
                .tripType(TripType.TOUR)
                .departureDateTime(tourDateTime)
                .adultCount(adultCount)
                .childCount(childCount)
                .hasReturnTransfer(false) // Туры обычно не имеют обратного трансфера
                .needsChildSeat(needsChildSeat)
                .needsNameGreeting(needsNameGreeting)
                .specialRequests(specialRequests)
                .hotelAddress(hotelAddress)
                // Места отправления и назначения заполнятся из тура
                .pickupLocation("Место встречи согласно программе тура")
                .dropoffLocation("Место завершения согласно программе тура")
                .build();
    }
}
