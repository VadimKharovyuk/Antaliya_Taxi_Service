package com.example.antaliya_taxi_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;             // Название блога
    private String description;       // Описание блога
    private String shotDescription;       //  короткое Описание блога
    private String url;               // URL изображения обложки
    private String imageId;           // ID изображения в хранилище
    private Integer views;            // Количество просмотров
    private LocalDateTime uploadDate;  // Дата создания
    private LocalDateTime updateDate;  // Дата обновления
    private Boolean isPublished;
}
