package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.vehicle.*;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import com.example.antaliya_taxi_service.model.Vehicle;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;


public interface VehicleService {

    VehicleListDto getVehicles(Pageable pageable);


    VehicleResponseDTO create(VehicleCreateDTO createDTO);


    VehicleResponseDTO update(VehicleUpdateDTO updateDTO);


    VehicleResponseDTO getById(Long id);

    void delete(Long id);


    List<VehicleCardDto> getActiveVehicles();


    VehicleUpdateDTO getForEdit(Long id);


    List<VehicleCardDto> getAvailableVehicles(LocalDateTime selectedDate, Integer passengers);


    boolean isVehicleAvailable(Long vehicleId, LocalDateTime selectedDate);


    VehicleCardDto getVehicleById(Long vehicleId);

    Long getActiveVehiclesCount();
}