package com.example.antaliya_taxi_service.maper;

import com.example.antaliya_taxi_service.dto.PhotoDto;
import com.example.antaliya_taxi_service.model.Album;
import com.example.antaliya_taxi_service.model.Photo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PhotoMapper {

    /**
     * Преобразует Photo в PhotoDto.Response
     */
    public PhotoDto.Response toResponse(Photo photo) {
        if (photo == null) {
            return null;
        }

        return PhotoDto.Response.builder()
                .id(photo.getId())
                .title(photo.getTitle())
                .description(photo.getDescription())
                .url(photo.getUrl())
                .imageId(photo.getImageId())
                .uploadedAt(photo.getCreatedAt())
                .albumId(photo.getAlbum() != null ? photo.getAlbum().getId() : null)
                .build();
    }

    /**
     * Преобразует PhotoDto.Request в Photo для создания новой фотографии
     * Примечание: не устанавливает url и imageId, так как они должны быть
     * установлены после загрузки изображения
     */
    public Photo toEntity(PhotoDto.Request request, Album album) {
        if (request == null) {
            return null;
        }

        return Photo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .album(album)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Обновляет существующий Photo данными из PhotoDto.UpdateRequest
     * Примечание: не обновляет url и imageId, так как они должны быть
     * обновлены отдельно после загрузки изображения
     */
    public void updatePhotoFromDto(PhotoDto.UpdateRequest request, Photo photo) {
        if (request == null || photo == null) {
            return;
        }

        if (request.getTitle() != null) {
            photo.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            photo.setDescription(request.getDescription());
        }
    }

}