package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.dto.AlbumDto;
import com.example.antaliya_taxi_service.dto.PhotoDto;
import com.example.antaliya_taxi_service.exception.ResourceNotFoundException;
import com.example.antaliya_taxi_service.maper.PhotoMapper;
import com.example.antaliya_taxi_service.model.Photo;
import com.example.antaliya_taxi_service.repository.PhotoRepository;
import com.example.antaliya_taxi_service.service.PhotoService;
import com.example.antaliya_taxi_service.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public PhotoDto.Response getPhotoById(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Фотография не найдена с id: " + id));

        return photoMapper.toResponse(photo);
    }

    @Override
    @Transactional
    public PhotoDto.Response updatePhoto(Long id, PhotoDto.UpdateRequest request) throws IOException {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Фотография не найдена с id: " + id));

        // Обновляем данные фотографии
        photoMapper.updatePhotoFromDto(request, photo);

        // Если загружено новое изображение, обновляем его
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            // Удаляем старое изображение, если оно есть
            if (photo.getImageId() != null) {
                storageService.deleteImage(photo.getImageId());
            }

            // Загружаем новое изображение
            StorageService.StorageResult result = storageService.uploadImage(request.getImage());
            photo.setUrl(result.getUrl());
            photo.setImageId(result.getImageId());
        }

        // Сохраняем изменения
        photo = photoRepository.save(photo);
        log.info("Обновлена фотография с id: {}", id);

        return photoMapper.toResponse(photo);
    }

    @Override
    @Transactional
    public boolean deletePhoto(Long id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Фотография не найдена с id: " + id));

        // Удаляем изображение из хранилища
        if (photo.getImageId() != null) {
            storageService.deleteImage(photo.getImageId());
        }

        // Удаляем фотографию из БД
        photoRepository.delete(photo);
        log.info("Удалена фотография с id: {}", id);

        return true;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PhotoDto.Response> getAllActivePhotos(Pageable pageable) {
        // Получаем фотографии только из активных альбомов
        Page<Photo> photosPage = photoRepository.findAllFromActiveAlbums(pageable);
        return photosPage.map(photoMapper::toResponse);
    }

    @Override
    public Long getTotalPhotosCount() {
        return photoRepository.count();
    }

}