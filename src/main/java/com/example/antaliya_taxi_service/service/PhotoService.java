package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.PhotoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PhotoService {
    /**
     * Получить фотографию по идентификатору
     */
    PhotoDto.Response getPhotoById(Long id);

    /**
     * Обновить данные фотографии
     */
    PhotoDto.Response updatePhoto(Long id, PhotoDto.UpdateRequest request) throws IOException;

    /**
     * Удалить фотографию
     */
    boolean deletePhoto(Long id);
}