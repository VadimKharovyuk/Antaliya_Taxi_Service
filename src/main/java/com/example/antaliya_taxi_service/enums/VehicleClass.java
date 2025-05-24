package com.example.antaliya_taxi_service.enums;

import lombok.Getter;

import java.math.BigDecimal;


@Getter
public enum VehicleClass {
    ECONOMY(new BigDecimal("1.0"), "Эконом"),      // без наценки
    COMFORT(new BigDecimal("1.3"), "Комфорт"),     // +30%
    BUSINESS(new BigDecimal("1.6"), "Бизнес"),     // +60%
    VIP(new BigDecimal("2.0"), "VIP");             // +100%

    /**
     * -- GETTER --
     *  Получить множитель цены для данного класса
     */
    private final BigDecimal priceMultiplier;
    /**
     * -- GETTER --
     *  Получить отображаемое название
     */
    private final String displayName;

    VehicleClass(BigDecimal priceMultiplier, String displayName) {
        this.priceMultiplier = priceMultiplier;
        this.displayName = displayName;
    }

    /**
     * Получить множитель по умолчанию, если класс не указан
     */
    public static BigDecimal getDefaultMultiplier() {
        return BigDecimal.ONE;
    }
}