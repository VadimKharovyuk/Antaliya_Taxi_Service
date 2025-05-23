package com.example.antaliya_taxi_service.enums;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum VehicleClass {
    STANDARD(new BigDecimal("1.0")),   // без наценки
    LUXURY(new BigDecimal("1.5")),     // +50%
    PREMIUM(new BigDecimal("2.0"));    // +100%

    private final BigDecimal priceMultiplier;

    VehicleClass(BigDecimal priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }
}