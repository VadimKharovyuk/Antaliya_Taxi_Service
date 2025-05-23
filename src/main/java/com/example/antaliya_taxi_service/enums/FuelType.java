package com.example.antaliya_taxi_service.enums;

import lombok.Getter;

@Getter
public enum FuelType {
    DIESEL("Diesel"),
    PETROL("Petrol"),
    ELECTRIC("Electric"),
    HYBRID("Hybrid");

    private final String displayName;

    FuelType(String displayName) {
        this.displayName = displayName;
    }

}
