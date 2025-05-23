package com.example.antaliya_taxi_service.enums;

import lombok.Getter;

@Getter
public enum TripType {
    ONE_WAY("One Way"),
    ROUND_TRIP("Round Trip");

    private final String displayName;

    TripType(String displayName) {
        this.displayName = displayName;
    }

}
