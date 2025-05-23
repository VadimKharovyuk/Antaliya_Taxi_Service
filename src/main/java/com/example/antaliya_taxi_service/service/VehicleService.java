package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.vehicle.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {

    VehicleListDto getVehicles(Pageable pageable);


    VehicleResponseDTO create(VehicleCreateDTO createDTO);


    VehicleResponseDTO update(VehicleUpdateDTO updateDTO);


    VehicleResponseDTO getById(Long id);

    void delete(Long id);


    List<VehicleCardDto> getActiveVehicles();


    VehicleUpdateDTO getForEdit(Long id);
}