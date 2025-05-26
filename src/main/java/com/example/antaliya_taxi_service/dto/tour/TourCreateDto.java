package com.example.antaliya_taxi_service.dto.tour;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourCreateDto {

    @NotBlank(message = "Название тура обязательно")
    @Size(max = 50, message = "Название не должно превышать 50 символов")
    private String title;

    @NotBlank(message = "Описание обязательно")
    @Size(max = 1300,message = "Максимальная длина описание 1300 символов")
    private String description;


    @Size(max = 50, message = "Краткое описание не должно превышать 50 символов")
    private String shortDescription;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    @Digits(integer = 8, fraction = 2, message = "Неверный формат цены")
    private BigDecimal price;

    @NotNull(message = "Продолжительность обязательна")
    @Min(value = 1, message = "Продолжительность должна быть минимум 1 час")
    @Max(value = 24, message = "Продолжительность не должна превышать 24 часа")
    private Integer duration;

    private Boolean isBestseller = false;

    @Min(value = 1, message = "Минимум 1 участник")
    @Max(value = 50, message = "Максимум 50 участников")
    private Integer maxParticipants;

    @Size(max = 50, message = "Язык не должен превышать 50 символов")
    private String language = "Русский";

    private MultipartFile image;


}