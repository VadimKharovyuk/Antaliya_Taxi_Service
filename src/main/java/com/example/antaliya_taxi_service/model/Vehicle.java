package com.example.antaliya_taxi_service.model;

import com.example.antaliya_taxi_service.enums.FuelType;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // === ОСНОВНАЯ ИНФОРМАЦИЯ ===

    @Column(nullable = false, length = 50)
    private String brand; // Mercedes, Renault, BMW

    @Column(nullable = false, length = 100)
    private String model; // Benz Vito, Traffic, Sprinter

    @Column(length = 4)
    private String year; // 2023, 2024

    @Column(name = "license_plate", length = 20)
    private String licensePlate; // Номер автомобиля для идентификации

    // === КЛАСС И ХАРАКТЕРИСТИКИ ===

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_class", nullable = false)
    private VehicleClass vehicleClass;

    @Column(name = "passenger_capacity", nullable = false)
    private Integer passengerCapacity; // 1-6, 1-13, 1-15

    @Column(name = "luggage_capacity", nullable = false)
    private Integer luggageCapacity; // количество чемоданов

    // === ДОПОЛНИТЕЛЬНЫЕ УДОБСТВА ===

    @Builder.Default
    @Column(name = "has_wifi")
    private Boolean hasWifi = true; // Wi-Fi

    @Builder.Default
    @Column(name = "has_air_conditioning")
    private Boolean hasAirConditioning = true; // Кондиционер

    @Builder.Default
    @Column(name = "has_leather_seats")
    private Boolean hasLeatherSeats = false; // Кожаные сиденья

    @Builder.Default
    @Column(name = "has_child_seat")
    private Boolean hasChildSeat = false; // Детские кресла

    @Column(name = "additional_features", length = 500)
    private String additionalFeatures; // Дополнительные особенности

    // === СТАТУС И СТАТИСТИКА ===

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true; // Доступен для бронирования

    @Builder.Default
    @Column(name = "completed_trips")
    private Integer completedTrips = 0; // Количество выполненных поездок


    // === МЕДИА И ОПИСАНИЕ ===

    @Column(name = "image_url", length = 500)
    private String imageUrl; // URL основного изображения

    @Column(name = "image_id", length = 100)
    private String imageId; // ID изображения в файловой системе

    @Column(columnDefinition = "TEXT")
    private String description; // Подробное описание

    // === ТЕХНИЧЕСКИЕ ХАРАКТЕРИСТИКИ ===

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type")
    private FuelType fuelType;


    @Column(name = "engine_capacity")
    private String engineCapacity; // 2.0L, 3.0L


    // === АУДИТ ===

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // === ПРЕДНАСТРОЙКА ДАННЫХ ===

    @PrePersist
    @PreUpdate
    public void normalizeData() {
        if (brand != null) brand = brand.trim();
        if (model != null) model = model.trim();
        if (licensePlate != null) licensePlate = licensePlate.toUpperCase().trim();
    }
}