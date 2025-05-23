package com.example.antaliya_taxi_service.repository;

import com.example.antaliya_taxi_service.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByActiveTrue();

}
