package com.example.antaliya_taxi_service.enums;

import lombok.Getter;

@Getter
public enum Currency {
    TRY("Турецкая лира"),
    USD("Доллар США"),
    EUR("Евро"),
    RUB("Российский рубль");

    private final String description;

    Currency(String description) {
        this.description = description;
    }

}
