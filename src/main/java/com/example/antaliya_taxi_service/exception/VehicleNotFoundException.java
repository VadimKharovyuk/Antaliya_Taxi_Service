package com.example.antaliya_taxi_service.exception;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(Long  id) {
        super(id.toString());
    }
}
