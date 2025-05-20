package com.example.antaliya_taxi_service.repository;


import com.example.antaliya_taxi_service.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    // Находит все фотографии из активных альбомов
    @Query("SELECT p FROM Photo p JOIN p.album a WHERE a.active = true")
    Page<Photo> findAllFromActiveAlbums(Pageable pageable);
}