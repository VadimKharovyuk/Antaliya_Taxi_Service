package com.example.antaliya_taxi_service.service;

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
public class PriceService {

    // Базовые цены можно вынести в конфигурацию
    @Value("${app.pricing.base-price:50.0}")
    private BigDecimal defaultBasePrice;

    @Value("${app.pricing.currency:EUR}")
    private String defaultCurrency;

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
     * Форматирует цену по классу автомобиля (с использованием множителей из enum)
     */
    public String formatPriceByClass(VehicleClass vehicleClass) {
        if (vehicleClass == null) {
            return "Цена по запросу";
        }

        // Используем базовую цену и множитель из enum
        BigDecimal basePrice = new BigDecimal("30.0"); // Базовая цена ECONOMY
        BigDecimal finalPrice = basePrice.multiply(vehicleClass.getPriceMultiplier());

        return String.format("от %.0f€/час", finalPrice);
    }

    /**
     * Получает базовую цену в числовом виде для расчетов
     */
    public BigDecimal getBasePriceByClass(VehicleClass vehicleClass) {
        if (vehicleClass == null) {
            return BigDecimal.ZERO;
        }

        // Базовая цена для ECONOMY класса
        BigDecimal economyBasePrice = new BigDecimal("30.0");

        // Умножаем на множитель класса
        return economyBasePrice.multiply(vehicleClass.getPriceMultiplier())
                .setScale(2, RoundingMode.HALF_UP);
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
     * Расчет стоимости дополнительных услуг
     */
    public BigDecimal calculateServicesFee(boolean needsChildSeat, boolean needsNameGreeting) {
        BigDecimal servicesFee = BigDecimal.ZERO;

        if (needsChildSeat) {
            BigDecimal childSeatFee = new BigDecimal("10.00");
            servicesFee = servicesFee.add(childSeatFee);
            log.debug("Добавлена услуга 'Детское кресло': +{} EUR", childSeatFee);
        }

        if (needsNameGreeting) {
            BigDecimal greetingFee = new BigDecimal("5.00");
            servicesFee = servicesFee.add(greetingFee);
            log.debug("Добавлена услуга 'Встреча с табличкой': +{} EUR", greetingFee);
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
            basePrice = new BigDecimal("50.0"); // значение по умолчанию
        }

        // Получаем множители
        BigDecimal vehicleMultiplier = getVehicleClassMultiplier(vehicleClass);
        BigDecimal tripMultiplier = getTripTypeMultiplier(tripType);
        BigDecimal timeMultiplier = getTimeMultiplier(departureDateTime);

        // Основная стоимость с учетом всех множителей
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
                basePrice,
                vehicleClass != null ? vehicleClass.getDisplayName() : "не указан", vehicleMultiplier,
                tripType, tripMultiplier,
                departureDateTime != null ? departureDateTime.getHour() + ":00" : "не указано",
                timeMultiplier, servicesFee, totalPrice);

        return totalPrice;
    }

    /**
     * Получает множитель времени (ночная доплата)
     */
    public BigDecimal getTimeMultiplier(LocalDateTime dateTime) {
        if (dateTime == null) {
            return BigDecimal.ONE;
        }

        int hour = dateTime.getHour();

        // Ночная доплата с 22:00 до 6:00
        if (hour >= 22 || hour < 6) {
            BigDecimal nightSurcharge = new BigDecimal("1.2");
            log.debug("Применена ночная доплата x{} для времени {}:00", nightSurcharge, hour);
            return nightSurcharge;
        }

        return BigDecimal.ONE;
    }




}