package com.example.antaliya_taxi_service.dto.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourCardDto {

    private Long id;
    private String title;
    private String shortDescription;
    private BigDecimal price;
    private Integer duration;
    private Boolean isBestseller;
    private Integer maxParticipants;
    private String language;
    private String url;
    private String imageId;
    private Integer views;

    // Дополнительные поля для отображения в списке
    private String formattedPrice; // "от 80 €"
    private String formattedDuration; // "7 часов"
    private Boolean hasVipTransfer; // можно добавить если нужно
}
