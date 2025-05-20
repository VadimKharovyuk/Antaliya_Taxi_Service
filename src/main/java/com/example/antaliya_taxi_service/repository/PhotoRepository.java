package com.example.antaliya_taxi_service.repository;


import com.example.antaliya_taxi_service.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}