package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.PhotoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    /**
     * Получить все активные фотографии с пагинацией
     */
    Page<PhotoDto.Response> getAllActivePhotos(Pageable pageable);

   }