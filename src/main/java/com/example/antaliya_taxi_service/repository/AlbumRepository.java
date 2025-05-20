package com.example.antaliya_taxi_service.repository;

import com.example.antaliya_taxi_service.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByActiveTrue();
}
