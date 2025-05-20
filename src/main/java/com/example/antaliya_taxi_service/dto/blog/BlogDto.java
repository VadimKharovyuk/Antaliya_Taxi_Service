package com.example.antaliya_taxi_service.dto.blog;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogDto {
    private Long id;
    private String title;
    private String description;
    private String shotDescription;
    private String url;
    private String imageId;
    private Integer views;
    private LocalDateTime uploadDate;
    private LocalDateTime updateDate;
    private Boolean isPublished;
}