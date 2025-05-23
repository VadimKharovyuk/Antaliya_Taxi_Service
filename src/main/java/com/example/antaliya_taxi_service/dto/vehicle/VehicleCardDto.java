package com.example.antaliya_taxi_service.dto.vehicle;

import com.example.antaliya_taxi_service.enums.VehicleClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleCardDto {
    private Long id;
    private String brand;
    private String model;
    private String year;
    private VehicleClass vehicleClass;
    private Integer passengerCapacity;
    private Integer luggageCapacity;
    private String imageUrl;
    private Boolean active;
    private Integer completedTrips;
    private String formattedPrice; // например "от 50€/час"
}
