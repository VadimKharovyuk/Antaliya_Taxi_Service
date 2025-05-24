package com.example.antaliya_taxi_service.exception;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class PassengerCapacityExceededException extends RuntimeException {

    private final Long vehicleId;
    private final Integer vehicleCapacity;
    private final Integer requestedPassengers;

    public PassengerCapacityExceededException(Long vehicleId, Integer vehicleCapacity, Integer requestedPassengers) {
        super("Превышена вместимость автомобиля ID: " + vehicleId +
                ". Максимум: " + vehicleCapacity + ", запрошено: " + requestedPassengers);
        this.vehicleId = vehicleId;
        this.vehicleCapacity = vehicleCapacity;
        this.requestedPassengers = requestedPassengers;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public Integer getVehicleCapacity() {
        return vehicleCapacity;
    }

    public Integer getRequestedPassengers() {
        return requestedPassengers;
    }

}
