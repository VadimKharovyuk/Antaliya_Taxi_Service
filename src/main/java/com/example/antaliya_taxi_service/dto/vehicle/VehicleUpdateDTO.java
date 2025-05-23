package com.example.antaliya_taxi_service.dto.vehicle;

import com.example.antaliya_taxi_service.enums.FuelType;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleUpdateDTO {

    @NotNull(message = "ID обязателен для обновления")
    private Long id;

    // === ОСНОВНАЯ ИНФОРМАЦИЯ ===

    @Size(max = 50, message = "Марка не должна превышать 50 символов")
    private String brand;

    @Size(max = 100, message = "Модель не должна превышать 100 символов")
    private String model;

    @Size(max = 4, message = "Год должен быть в формате YYYY")
    @Pattern(regexp = "\\d{4}", message = "Год должен содержать 4 цифры")
    private String year;

    @Size(max = 20, message = "Номер не должен превышать 20 символов")
    private String licensePlate;

    // === КЛАСС И ХАРАКТЕРИСТИКИ ===

    private VehicleClass vehicleClass;

    @Min(value = 1, message = "Минимум 1 пассажир")
    @Max(value = 20, message = "Максимум 20 пассажиров")
    private Integer passengerCapacity;

    @Min(value = 0, message = "Вместимость багажа не может быть отрицательной")
    private Integer luggageCapacity;

    // === ДОПОЛНИТЕЛЬНЫЕ УДОБСТВА ===

    private Boolean hasWifi;
    private Boolean hasAirConditioning;
    private Boolean hasLeatherSeats;
    private Boolean hasChildSeat;

    @Size(max = 500, message = "Дополнительные особенности не должны превышать 500 символов")
    private String additionalFeatures;

    // === МЕДИА И ОПИСАНИЕ ===

    private MultipartFile image; // Новое изображение (опционально)

    @Size(max = 1000, message = "Описание не должно превышать 1000 символов")
    private String description;

    // === ТЕХНИЧЕСКИЕ ХАРАКТЕРИСТИКИ ===

    private FuelType fuelType;

    @Size(max = 10, message = "Объем двигателя не должен превышать 10 символов")
    private String engineCapacity;

    // === СТАТУС ===

    private Boolean active;
}