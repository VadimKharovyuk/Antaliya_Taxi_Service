package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.dto.AlbumDto;
import com.example.antaliya_taxi_service.dto.PhotoDto;
import com.example.antaliya_taxi_service.exception.ResourceNotFoundException;
import com.example.antaliya_taxi_service.maper.AlbumMapper;
import com.example.antaliya_taxi_service.maper.PhotoMapper;
import com.example.antaliya_taxi_service.model.Album;
import com.example.antaliya_taxi_service.model.Photo;

import com.example.antaliya_taxi_service.repository.AlbumRepository;
import com.example.antaliya_taxi_service.repository.PhotoRepository;
import com.example.antaliya_taxi_service.service.AlbumService;
import com.example.antaliya_taxi_service.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final AlbumMapper albumMapper;
    private final PhotoMapper photoMapper;
    private final StorageService storageService;

    @Override
    @Transactional
    public AlbumDto.Response createAlbum(AlbumDto.Request request) throws IOException {
        // Создаем альбом
        Album album = albumMapper.toEntity(request);

        // Загружаем обложку, если она есть
        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            StorageService.StorageResult result = storageService.uploadImage(request.getCoverImage());
            album.setUrl(result.getUrl());
            album.setImageId(result.getImageId());
        }

        // Сохраняем альбом в БД
        album = albumRepository.save(album);
        log.info("Создан новый альбом: {}", album.getTitle());

        return albumMapper.toResponse(album);
    }

    @Override
    @Transactional
    public AlbumDto.Response updateAlbum(Long id, AlbumDto.UpdateRequest request) throws IOException {
        // Получаем альбом из БД
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Альбом не найден с id: " + id));

        // Обновляем данные альбома
        albumMapper.updateAlbumFromDto(request, album);

        // Если загружена новая обложка, обновляем изображение
        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            // Удаляем старое изображение, если оно есть
            if (album.getImageId() != null) {
                storageService.deleteImage(album.getImageId());
            }

            // Загружаем новое изображение
            StorageService.StorageResult result = storageService.uploadImage(request.getCoverImage());
            album.setUrl(result.getUrl());
            album.setImageId(result.getImageId());
        }

        // Сохраняем изменения
        album = albumRepository.save(album);
        log.info("Обновлен альбом с id: {}", id);

        return albumMapper.toResponse(album);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDto.Response getAlbumById(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Альбом не найден с id: " + id));

        return albumMapper.toResponse(album);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDto.Item> getAllActiveAlbums() {
        List<Album> albums = albumRepository.findByActiveTrue();
        return albumMapper.toItemList(albums);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumDto.Item> getAllAlbums() {
        List<Album> albums = albumRepository.findAll();
        return albumMapper.toItemList(albums);
    }

    @Override
    @Transactional
    public boolean deleteAlbum(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Альбом не найден с id: " + id));

        // Удаляем все связанные фотографии
        if (album.getPhotos() != null && !album.getPhotos().isEmpty()) {
            for (Photo photo : album.getPhotos()) {
                if (photo.getImageId() != null) {
                    storageService.deleteImage(photo.getImageId());
                }
            }
        }

        // Удаляем обложку альбома
        if (album.getImageId() != null) {
            storageService.deleteImage(album.getImageId());
        }

        // Удаляем альбом из БД
        albumRepository.delete(album);
        log.info("Удален альбом с id: {}", id);

        return true;
    }

    @Override
    @Transactional
    public AlbumDto.Response addPhotoToAlbum(Long albumId, MultipartFile image, String title, String description) throws IOException {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Альбом не найден с id: " + albumId));

        // Создаем новую фотографию
        Photo photo = Photo.builder()
                .title(title)
                .description(description)
                .album(album)
                .createdAt(LocalDateTime.now())
                .build();

        // Загружаем изображение
        if (image != null && !image.isEmpty()) {
            StorageService.StorageResult result = storageService.uploadImage(image);
            photo.setUrl(result.getUrl());
            photo.setImageId(result.getImageId());
        }

        // Сохраняем фотографию
        photo = photoRepository.save(photo);
        log.info("Добавлена новая фотография '{}' в альбом с id: {}", title, albumId);

        // Возвращаем обновленный альбом
        return getAlbumById(albumId);
    }

    @Override
    @Transactional
    public boolean removePhotoFromAlbum(Long albumId, Long photoId) {
        // Проверяем существование альбома
        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException("Альбом не найден с id: " + albumId);
        }

        // Находим фотографию
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Фотография не найдена с id: " + photoId));

        // Проверяем, принадлежит ли фотография указанному альбому
        if (!photo.getAlbum().getId().equals(albumId)) {
            throw new IllegalArgumentException("Фотография с id " + photoId + " не принадлежит альбому с id " + albumId);
        }

        // Удаляем изображение из хранилища
        if (photo.getImageId() != null) {
            storageService.deleteImage(photo.getImageId());
        }

        // Удаляем фотографию из БД
        photoRepository.delete(photo);
        log.info("Удалена фотография с id: {} из альбома с id: {}", photoId, albumId);

        return true;
    }

    @Override
    @Transactional
    public AlbumDto.Response updateAlbumActivity(Long id, boolean active) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Альбом не найден с id: " + id));

        album.setActive(active);
        album = albumRepository.save(album);
        log.info("Обновлен статус активности альбома с id: {} на: {}", id, active);

        return albumMapper.toResponse(album);
    }
}