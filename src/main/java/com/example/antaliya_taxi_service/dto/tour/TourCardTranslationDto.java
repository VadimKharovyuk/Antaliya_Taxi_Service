package com.example.antaliya_taxi_service.dto.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourCardTranslationDto {

    private Long id;
    private String title;
    private String shortDescription;

    private String url;
    private Integer views;
    private String formattedDate;

    private String language;


    private String formattedPrice;
    private String formattedDuration;
    private boolean isBestseller;
    private int maxParticipants;
    private boolean hasVipTransfer;

}
