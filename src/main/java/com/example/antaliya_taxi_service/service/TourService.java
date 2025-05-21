package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.tour.TourCreateDto;
import com.example.antaliya_taxi_service.dto.tour.TourDto;
import com.example.antaliya_taxi_service.dto.tour.TourListDto;
import com.example.antaliya_taxi_service.dto.tour.TourUpdateDto;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface TourService {

    TourDto createTour(TourCreateDto tourCreateDto) throws IOException;
    TourDto findTourById(Long id);
    TourListDto getAll (Pageable pageable);
    TourListDto getTop6ToursForMainPage();
    void deleteBlog(Long id);



}
