package com.example.antaliya_taxi_service.dto.Booking;


import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.enums.TripType;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPriceCalculationDTO {

    // === ВХОДНЫЕ ДАННЫЕ ===

    private Long routeId;
    private Long tourId;
    private VehicleClass vehicleClass;
    private TripType tripType;
    private Integer passengerCount;
    private Boolean hasReturnTransfer;

    // === ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ===

    private Boolean needsChildSeat;
    private Boolean needsNameGreeting;

    // === РЕЗУЛЬТАТ РАСЧЕТА ===

    private BigDecimal basePrice; // Базовая цена маршрута/тура
    private BigDecimal vehicleMultiplier; // Множитель класса автомобиля
    private BigDecimal tripMultiplier; // Множитель типа поездки
    private BigDecimal servicesFee; // Доплата за дополнительные услуги
    private BigDecimal totalPrice; // Итоговая цена

    private Currency currency;
    private String formattedPrice;

    // === ДЕТАЛИЗАЦИЯ ===

    private String priceBreakdown; // Детальная разбивка цены
    private String discountInfo; // Информация о скидках
    private String additionalInfo; // Дополнительная информация о ценообразовании
}
