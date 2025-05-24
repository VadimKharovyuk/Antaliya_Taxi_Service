package com.example.antaliya_taxi_service.dto.Booking;

import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.enums.TripType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingUpdateDTO {

    @NotNull(message = "ID обязателен для обновления")
    private Long id;

    // === ЛИЧНАЯ ИНФОРМАЦИЯ КЛИЕНТА ===

    @Size(max = 100, message = "Имя не должно превышать 100 символов")
    private String customerName;

    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    private String customerEmail;

    @Size(max = 20, message = "Телефон не должен превышать 20 символов")
    private String customerPhone;

    // === СВЯЗИ ===

    private Long vehicleId;
    private Long routeId;
    private Long tourId;

    // === ТИП ПОЕЗДКИ ===

    private TripType tripType;

    // === ОСНОВНОЙ ТРАНСФЕР ===

    @Size(max = 200, message = "Место получения не должно превышать 200 символов")
    private String pickupLocation;

    @Size(max = 200, message = "Место назначения не должно превышать 200 символов")
    private String dropoffLocation;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime departureDateTime;

    @Size(max = 20, message = "Номер рейса не должен превышать 20 символов")
    private String flightNumber;

    @Size(max = 300, message = "Адрес отеля не должен превышать 300 символов")
    private String hotelAddress;

    // === ОБРАТНЫЙ ТРАНСФЕР ===

    private Boolean hasReturnTransfer;

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

    @Min(value = 1, message = "Минимум 1 взрослый")
    @Max(value = 20, message = "Максимум 20 взрослых")
    private Integer adultCount;

    @Min(value = 0, message = "Количество детей не может быть отрицательным")
    @Max(value = 10, message = "Максимум 10 детей")
    private Integer childCount;

    // === ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ===

    private Boolean needsChildSeat;
    private Boolean needsNameGreeting;

    @Size(max = 1000, message = "Особые пожелания не должны превышать 1000 символов")
    private String specialRequests;

    // === АДМИНСКИЕ ПОЛЯ ===

    private BookingStatus status;

    @Size(max = 1000, message = "Заметки админа не должны превышать 1000 символов")
    private String adminNotes;

    // === ЦЕНООБРАЗОВАНИЕ (только для админов) ===

    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    private BigDecimal totalPrice;
}

