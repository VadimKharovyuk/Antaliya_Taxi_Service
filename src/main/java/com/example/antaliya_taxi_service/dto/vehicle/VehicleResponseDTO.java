package com.example.antaliya_taxi_service.dto.vehicle;

import com.example.antaliya_taxi_service.enums.FuelType;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDTO {

    private Long id;

    // === ОСНОВНАЯ ИНФОРМАЦИЯ ===

    private String brand;
    private String model;
    private String year;
    private String licensePlate;

    // === КЛАСС И ХАРАКТЕРИСТИКИ ===

    private VehicleClass vehicleClass;
    private Integer passengerCapacity;
    private Integer luggageCapacity;

    // === ДОПОЛНИТЕЛЬНЫЕ УДОБСТВА ===

    private Boolean hasWifi;
    private Boolean hasAirConditioning;
    private Boolean hasLeatherSeats;
    private Boolean hasChildSeat;
    private String additionalFeatures;

    // === СТАТУС И СТАТИСТИКА ===

    private Boolean active;
    private Integer completedTrips;

    // === МЕДИА И ОПИСАНИЕ ===

    private String imageUrl; // URL изображения
    private String imageId;  // ID изображения
    private String description;

    // === ТЕХНИЧЕСКИЕ ХАРАКТЕРИСТИКИ ===

    private FuelType fuelType;
    private String engineCapacity;

    // === АУДИТ ===

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
