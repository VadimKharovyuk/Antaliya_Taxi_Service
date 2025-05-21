package com.example.antaliya_taxi_service.dto.tour;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourUpdateDto {

    @NotNull(message = "ID тура обязателен")
    private Long id;

    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String title;

    private String description;

    @Size(max = 500, message = "Краткое описание не должно превышать 500 символов")
    private String shortDescription;

    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    @Digits(integer = 8, fraction = 2, message = "Неверный формат цены")
    private BigDecimal price;

    @Min(value = 1, message = "Продолжительность должна быть минимум 1 час")
    @Max(value = 24, message = "Продолжительность не должна превышать 24 часа")
    private Integer duration;

    private Boolean isBestseller;

    @Min(value = 1, message = "Минимум 1 участник")
    @Max(value = 50, message = "Максимум 50 участников")
    private Integer maxParticipants;

    @Size(max = 50, message = "Язык не должен превышать 50 символов")
    private String language;

    private MultipartFile image;
}
