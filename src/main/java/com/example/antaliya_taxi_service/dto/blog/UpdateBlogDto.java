package com.example.antaliya_taxi_service.dto.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlogDto {
    private Long id;
    private String title;
    private String description;
    private String shotDescription;
    private MultipartFile image; // Опционально - новое изображение
    private Boolean updateImage; // Флаг: нужно ли обновлять изображение
    private Boolean isPublished;
}