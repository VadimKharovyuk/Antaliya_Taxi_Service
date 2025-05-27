package com.example.antaliya_taxi_service.dto.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourTranslationDto {

    private Long id;
    private String title;
    private String description;
    private String shortDescription;

    // Атрибуты для отображения
    private BigDecimal price;
    private Integer duration;
    private Boolean isBestseller;
    private Integer maxParticipants;
    private String url;
    private Integer views;
    private LocalDateTime uploadDate;
    private LocalDateTime updateDate;


    private String language; // язык перевода, например: 'ru', 'en', 'tr'

    // Дополнительные поля для UI
    private String formattedDate;
    private Integer readingTimeMinutes;
}