package com.example.antaliya_taxi_service.dto.tour;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDto {

    private Long id;
    private String title;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private Integer duration;
    private Boolean isBestseller;
    private Integer maxParticipants;
    private String language;
    private String url;
    private String imageId;
    private Integer views;
    private LocalDateTime uploadDate;
    private LocalDateTime updateDate;
}
