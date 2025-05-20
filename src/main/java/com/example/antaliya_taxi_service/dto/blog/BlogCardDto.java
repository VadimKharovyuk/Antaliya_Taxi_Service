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
public class BlogCardDto {
    private Long id;
    private String title;
    private String shotDescription; // Короткое описание для превью
    private String url; // URL изображения
    private Integer views;
    private String formattedDate; // Отформатированная дата для отображения
}