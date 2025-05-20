package com.example.antaliya_taxi_service.maper;

import com.example.antaliya_taxi_service.dto.AlbumDto;
import com.example.antaliya_taxi_service.dto.PhotoDto;
import com.example.antaliya_taxi_service.model.Album;
import com.example.antaliya_taxi_service.model.Photo;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlbumMapper {

    private final PhotoMapper photoMapper;

    public AlbumMapper(PhotoMapper photoMapper) {
        this.photoMapper = photoMapper;
    }

    /**
     * Преобразует Album в AlbumDto.Response
     */
    public AlbumDto.Response toResponse(Album album) {
        if (album == null) {
            return null;
        }

        List<PhotoDto.Response> photoResponses = null;
        if (album.getPhotos() != null && !album.getPhotos().isEmpty()) {
            photoResponses = album.getPhotos().stream()
                    .map(photoMapper::toResponse)
                    .collect(Collectors.toList());
        }

        return AlbumDto.Response.builder()
                .id(album.getId())
                .title(album.getTitle())
                .description(album.getDescription())
                .url(album.getUrl())
                .imageId(album.getImageId())
                .createdAt(LocalDateTime.now())
                .photos(photoResponses)
                .active(album.isActive())
                .build();
    }


    /**
     * Преобразует Album в AlbumDto.Item для отображения в списке
     */
    public AlbumDto.Item toItem(Album album) {
        if (album == null) {
            return null;
        }

        return AlbumDto.Item.builder()
                .id(album.getId())
                .title(album.getTitle())
                .description(album.getDescription())
                .url(album.getUrl())
                .active(album.isActive())
                .build();
    }

    /**
     * Преобразует список Album в список AlbumDto.Item
     */
    public List<AlbumDto.Item> toItemList(List<Album> albums) {
        if (albums == null) {
            return new ArrayList<>();
        }

        return albums.stream()
                .map(this::toItem)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует AlbumDto.Request в Album для создания нового альбома
     * Примечание: не устанавливает url и imageId, так как они должны быть
     * установлены после загрузки изображения
     */
    public Album toEntity(AlbumDto.Request request) {
        if (request == null) {
            return null;
        }

        return Album.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .active(request.isActive())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Обновляет существующий Album данными из AlbumDto.UpdateRequest
     * Примечание: не обновляет url и imageId, так как они должны быть
     * обновлены отдельно после загрузки изображения
     */
    public void updateAlbumFromDto(AlbumDto.UpdateRequest request, Album album) {
        if (request == null || album == null) {
            return;
        }

        if (request.getTitle() != null) {
            album.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            album.setDescription(request.getDescription());
        }

        album.setActive(request.isActive());
    }

    /**
     * Преобразует список Album в список AlbumDto.Response
     */
    public List<AlbumDto.Response> toResponseList(List<Album> albums) {
        if (albums == null) {
            return new ArrayList<>();
        }

        return albums.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}