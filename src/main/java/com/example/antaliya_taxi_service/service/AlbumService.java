package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.Album.AlbumDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AlbumService {
    /**
     * Создать новый альбом
     */
    AlbumDto.Response createAlbum(AlbumDto.Request request) throws IOException;

    /**
     * Обновить существующий альбом
     */
    AlbumDto.Response updateAlbum(Long id, AlbumDto.UpdateRequest request) throws IOException;

    /**
     * Получить альбом по идентификатору
     */
    AlbumDto.Response getAlbumById(Long id);

    /**
     * Получить список всех активных альбомов
     */
    List<AlbumDto.Item> getAllActiveAlbums();

    /**
     * Получить список всех альбомов (включая неактивные)
     */
    List<AlbumDto.Item> getAllAlbums();

    /**
     * Удалить альбом по идентификатору
     */
    boolean deleteAlbum(Long id);

    /**
     * Добавить фотографию в альбом
     */
    AlbumDto.Response addPhotoToAlbum(Long albumId, MultipartFile image, String title, String description) throws IOException;

    /**
     * Удалить фотографию из альбома
     */
    boolean removePhotoFromAlbum(Long albumId, Long photoId);

    /**
     * Обновить активность альбома
     */
    AlbumDto.Response updateAlbumActivity(Long id, boolean active);


    Map<Long, Long> getPhotoCountsForAlbums(List<AlbumDto.Item> albums);
}