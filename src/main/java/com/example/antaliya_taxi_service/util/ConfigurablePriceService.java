package com.example.antaliya_taxi_service.util;

import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.enums.TripType;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import com.example.antaliya_taxi_service.model.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ConfigurablePriceService {

    // Базовые настройки из конфигурации
    @Value("${app.pricing.base-price:50.0}")
    private BigDecimal defaultBasePrice;

    @Value("${app.pricing.currency:EUR}")
    private String defaultCurrency;

    @Value("${app.pricing.child-seat-fee:10.0}")
    private BigDecimal childSeatFee;

    @Value("${app.pricing.name-greeting-fee:5.0}")
    private BigDecimal nameGreetingFee;

    // Почасовые тарифы
    @Value("${app.pricing.hourly.economy:30.0}")
    private BigDecimal economyHourlyRate;

    @Value("${app.pricing.hourly.comfort:39.0}")
    private BigDecimal comfortHourlyRate;

    @Value("${app.pricing.hourly.business:48.0}")
    private BigDecimal businessHourlyRate;

    @Value("${app.pricing.hourly.vip:60.0}")
    private BigDecimal vipHourlyRate;

    // Доплаты
    @Value("${app.pricing.night-surcharge:1.2}")
    private BigDecimal nightSurcharge;

    @Value("${app.pricing.holiday-surcharge:1.15}")
    private BigDecimal holidaySurcharge;

    // Рабочие часы
    @Value("${app.pricing.working-hours.start:6}")
    private int workingHoursStart;

    @Value("${app.pricing.working-hours.end:22}")
    private int workingHoursEnd;

    /**
     * Форматирует цену для отображения на основе класса автомобиля
     */
    public String formatPrice(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleClass() == null) {
            return "Цена по запросу";
        }

        return formatPriceByClass(vehicle.getVehicleClass());
    }

    /**
     * Форматирует цену по классу автомобиля
     */
    public String formatPriceByClass(VehicleClass vehicleClass) {
        if (vehicleClass == null) {
            return "Цена по запросу";
        }

        BigDecimal hourlyRate = getHourlyRateByClass(vehicleClass);
        return String.format("от %.0f€/час", hourlyRate);
    }

    /**
     * Получает почасовую ставку по классу автомобиля
     */
    public BigDecimal getHourlyRateByClass(VehicleClass vehicleClass) {
        if (vehicleClass == null) {
            return economyHourlyRate;
        }

        switch (vehicleClass) {
            case ECONOMY:
                return economyHourlyRate;
            case COMFORT:
                return comfortHourlyRate;
            case BUSINESS:
                return businessHourlyRate;
            case VIP:
                return vipHourlyRate;
            default:
                return economyHourlyRate;
        }
    }

    /**
     * Получает множитель класса автомобиля
     */
    public BigDecimal getVehicleClassMultiplier(VehicleClass vehicleClass) {
        if (vehicleClass == null) {
            log.warn("Класс автомобиля не указан, используется множитель по умолчанию");
            return VehicleClass.getDefaultMultiplier();
        }

        BigDecimal multiplier = vehicleClass.getPriceMultiplier();
        log.debug("Получен множитель {} для класса автомобиля '{}'",
                multiplier, vehicleClass.getDisplayName());

        return multiplier;
    }

    /**
     * Получает множитель типа поездки
     */
    public BigDecimal getTripTypeMultiplier(TripType tripType) {
        if (tripType == null) {
            return BigDecimal.ONE;
        }

        switch (tripType) {
            case ONE_WAY:
                return new BigDecimal("1.0");
            case ROUND_TRIP:
                return new BigDecimal("1.8"); // Скидка 10% за обратный билет
            default:
                log.warn("Неизвестный тип поездки: {}, используется множитель 1.0", tripType);
                return BigDecimal.ONE;
        }
    }

    /**
     * Получает множитель времени (ночная/праздничная доплата)
     */
    public BigDecimal getTimeMultiplier(LocalDateTime dateTime) {
        if (dateTime == null) {
            return BigDecimal.ONE;
        }

        BigDecimal multiplier = BigDecimal.ONE;
        int hour = dateTime.getHour();

        // Ночная доплата
        if (hour < workingHoursStart || hour > workingHoursEnd) {
            multiplier = multiplier.multiply(nightSurcharge);
            log.debug("Применена ночная доплата x{} для времени {}", nightSurcharge, hour);
        }

        // Можно добавить проверку на праздники
        // if (isHoliday(dateTime.toLocalDate())) {
        //     multiplier = multiplier.multiply(holidaySurcharge);
        // }

        return multiplier;
    }

    /**
     * Расчет стоимости дополнительных услуг
     */
    public BigDecimal calculateServicesFee(boolean needsChildSeat, boolean needsNameGreeting) {
        BigDecimal servicesFee = BigDecimal.ZERO;

        if (needsChildSeat) {
            servicesFee = servicesFee.add(childSeatFee);
            log.debug("Добавлена услуга 'Детское кресло': +{} EUR", childSeatFee);
        }

        if (needsNameGreeting) {
            servicesFee = servicesFee.add(nameGreetingFee);
            log.debug("Добавлена услуга 'Встреча с табличкой': +{} EUR", nameGreetingFee);
        }

        return servicesFee;
    }

    /**
     * Полный расчет стоимости поездки с учетом времени
     */
    public BigDecimal calculateTotalPrice(BigDecimal basePrice,
                                          VehicleClass vehicleClass,
                                          TripType tripType,
                                          LocalDateTime departureDateTime,
                                          boolean needsChildSeat,
                                          boolean needsNameGreeting) {

        if (basePrice == null) {
            basePrice = defaultBasePrice;
        }

        // Получаем множители
        BigDecimal vehicleMultiplier = getVehicleClassMultiplier(vehicleClass);
        BigDecimal tripMultiplier = getTripTypeMultiplier(tripType);
        BigDecimal timeMultiplier = getTimeMultiplier(departureDateTime);

        // Основная стоимость
        BigDecimal subtotal = basePrice
                .multiply(vehicleMultiplier)
                .multiply(tripMultiplier)
                .multiply(timeMultiplier);

        // Дополнительные услуги
        BigDecimal servicesFee = calculateServicesFee(needsChildSeat, needsNameGreeting);

        // Итоговая стоимость
        BigDecimal totalPrice = subtotal.add(servicesFee)
                .setScale(2, RoundingMode.HALF_UP);

        log.info("Расчет стоимости: базовая={} EUR, класс={} (x{}), тип={} (x{}), время={} (x{}), услуги=+{} EUR, итого={} EUR",
                basePrice, vehicleClass != null ? vehicleClass.getDisplayName() : "не указан", vehicleMultiplier,
                tripType, tripMultiplier, departureDateTime != null ? departureDateTime.getHour() + ":00" : "не указано",
                timeMultiplier, servicesFee, totalPrice);

        return totalPrice;
    }

    /**
     * Упрощенный расчет без учета времени (для обратной совместимости)
     */
    public BigDecimal calculateTotalPrice(BigDecimal basePrice,
                                          VehicleClass vehicleClass,
                                          TripType tripType,
                                          boolean needsChildSeat,
                                          boolean needsNameGreeting) {
        return calculateTotalPrice(basePrice, vehicleClass, tripType, null, needsChildSeat, needsNameGreeting);
    }

    /**
     * Форматирует цену с валютой
     */
    public String formatPriceWithCurrency(BigDecimal price, Currency currency) {
        if (price == null) {
            return "Цена не указана";
        }

        if (currency == null) {
            currency = Currency.valueOf(defaultCurrency);
        }

        return String.format("%.2f %s", price, currency.toString());
    }

    /**
     * Получить детализацию цены
     */
    public String getPriceBreakdown(BigDecimal basePrice,
                                    VehicleClass vehicleClass,
                                    TripType tripType,
                                    LocalDateTime departureDateTime,
                                    boolean needsChildSeat,
                                    boolean needsNameGreeting) {

        StringBuilder breakdown = new StringBuilder();

        breakdown.append("Детализация стоимости:\n");
        breakdown.append(String.format("• Базовая цена: %.2f EUR\n", basePrice));

        if (vehicleClass != null) {
            BigDecimal multiplier = vehicleClass.getPriceMultiplier();
            breakdown.append(String.format("• Класс '%s': x%.1f\n",
                    vehicleClass.getDisplayName(), multiplier));
        }

        if (tripType != null) {
            BigDecimal multiplier = getTripTypeMultiplier(tripType);
            breakdown.append(String.format("• Тип поездки: x%.1f", multiplier));
            if (tripType == TripType.ROUND_TRIP) {
                breakdown.append(" (скидка 10%)");
            }
            breakdown.append("\n");
        }

        if (departureDateTime != null) {
            BigDecimal timeMultiplier = getTimeMultiplier(departureDateTime);
            if (timeMultiplier.compareTo(BigDecimal.ONE) != 0) {
                breakdown.append(String.format("• Доплата за время: x%.1f\n", timeMultiplier));
            }
        }

        if (needsChildSeat) {
            breakdown.append(String.format("• Детское кресло: +%.2f EUR\n", childSeatFee));
        }

        if (needsNameGreeting) {
            breakdown.append(String.format("• Встреча с табличкой: +%.2f EUR\n", nameGreetingFee));
        }

        BigDecimal total = calculateTotalPrice(basePrice, vehicleClass, tripType, departureDateTime,
                needsChildSeat, needsNameGreeting);
        breakdown.append(String.format("• ИТОГО: %.2f EUR", total));

        return breakdown.toString();
    }

    /**
     * Проверить, применяется ли скидка
     */
    public boolean hasDiscount(TripType tripType) {
        if (tripType == TripType.ROUND_TRIP) {
            BigDecimal multiplier = getTripTypeMultiplier(tripType);
            return multiplier.compareTo(new BigDecimal("2.0")) < 0;
        }
        return false;
    }

    /**
     * Получить размер скидки в процентах для обратного билета
     */
    public int getDiscountPercentage(TripType tripType) {
        if (!hasDiscount(tripType)) {
            return 0;
        }

        // Для ROUND_TRIP: 1.8 вместо 2.0 = скидка 10%
        return 10;
    }
}