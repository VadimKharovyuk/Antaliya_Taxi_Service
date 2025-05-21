package com.example.antaliya_taxi_service.repository;

import com.example.antaliya_taxi_service.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
}
