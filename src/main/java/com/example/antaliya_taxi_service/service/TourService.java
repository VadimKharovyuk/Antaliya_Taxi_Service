package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.tour.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface TourService {

    TourDto createTour(TourCreateDto tourCreateDto) throws IOException;
    TourDto findTourById(Long id);
    TourListDto getAll (Pageable pageable);
    TourListDto getTop6ToursForMainPage();
    void deleteTour(Long id);
    TourDto updateTour(@Valid TourUpdateDto tourUpdateDto);

    List<TourCardDto> getTop6Tours(); // для главной страницы
    List<TourCardDto> getBestsellerTours();
}
