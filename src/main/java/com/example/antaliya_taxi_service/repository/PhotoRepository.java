package com.example.antaliya_taxi_service.repository;


import com.example.antaliya_taxi_service.dto.AlbumDto;
import com.example.antaliya_taxi_service.dto.AlbumPhotoCount;
import com.example.antaliya_taxi_service.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    // Находит все фотографии из активных альбомов
    @Query("SELECT p FROM Photo p JOIN p.album a WHERE a.active = true")
    Page<Photo> findAllFromActiveAlbums(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Photo p WHERE p.album.id = :albumId")
    Long countPhotosByAlbumId(@Param("albumId") Long albumId);

    @Query("SELECT p.album.id as albumId, COUNT(p) as count FROM Photo p WHERE p.album.id IN :albumIds GROUP BY p.album.id")
    List<AlbumPhotoCount> countPhotosByAlbumIds(@Param("albumIds") List<Long> albumIds);
}