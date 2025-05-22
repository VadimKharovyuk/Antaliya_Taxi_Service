package com.example.antaliya_taxi_service.service.impl;
import com.example.antaliya_taxi_service.dto.tour.*;
import com.example.antaliya_taxi_service.maper.TourMapper;
import com.example.antaliya_taxi_service.model.Blog;
import com.example.antaliya_taxi_service.model.Tour;
import com.example.antaliya_taxi_service.repository.TourRepository;
import com.example.antaliya_taxi_service.service.StorageService;
import com.example.antaliya_taxi_service.service.TourService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class TourServiceImpl implements TourService {
    private final TourMapper tourMapper;
    private final TourRepository tourRepository;
    private final StorageService storageService;

    @Override
    public TourDto createTour(TourCreateDto tourCreateDto) throws IOException {
        Tour tour = tourMapper.createTourFromDto(tourCreateDto);

        // Загружаем изображение, если оно есть
        if (tourCreateDto.getImage() != null && !tourCreateDto.getImage().isEmpty()) {
            StorageService.StorageResult result = storageService.uploadImage(tourCreateDto.getImage());
            tour.setUrl(result.getUrl());
            tour.setImageId(result.getImageId());
        }

        Tour savedTour = tourRepository.save(tour);
        return tourMapper.toDto(savedTour);
    }

    @Override
    @Transactional(readOnly = true)
    public TourDto findTourById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тур с id " + id + " не найден"));
        return tourMapper.toDto(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public TourListDto getAll(Pageable pageable) {
        Page<Tour> tourPage = tourRepository.findAll(pageable);
        return buildTourListDto(tourPage);
    }

    @Override
    @Transactional(readOnly = true)
    public TourListDto getTop6ToursForMainPage() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<Tour> tourPage = tourRepository.findAll(pageable);
        return buildTourListDto(tourPage);
    }

    @Override
    public void deleteTour(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тур с id " + id + " не найден"));

        if (tour.getImageId() != null) {
            storageService.deleteImage(tour.getImageId());
        }
        tourRepository.delete(tour);
    }

    @Override
    public TourDto updateTour(TourUpdateDto tourUpdateDto) {
        return null;
    }

    // Вынес общую логику в отдельный метод
    private TourListDto buildTourListDto(Page<Tour> tourPage) {
        List<TourCardDto> tourCardDtos = tourPage.getContent().stream()
                .map(tourMapper::toCardDto)
                .toList();

        return TourListDto.builder()
                .tourCardDtos(tourCardDtos)
                .totalPages(tourPage.getTotalPages())
                .currentPage(tourPage.getNumber())
                .totalItems(tourPage.getTotalElements())
                .itemsPerPage(tourPage.getSize())
                .hasNext(tourPage.hasNext())
                .hasPrevious(tourPage.hasPrevious())
                .isFirst(tourPage.isFirst())
                .isLast(tourPage.isLast())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public List<TourCardDto> getTop6Tours() {
        Pageable pageable = PageRequest.of(0, 6, Sort.by("views").descending()); // сортировка по просмотрам
        Page<Tour> tourPage = tourRepository.findAll(pageable);

        return tourPage.getContent()
                .stream()
                .map(tourMapper::toCardDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourCardDto> getBestsellerTours() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<Tour> tourPage = tourRepository.findByIsBestsellerTrueOrderByViewsDesc(pageable);

        return tourPage.getContent()
                .stream()
                .map(tourMapper::toCardDto)
                .toList();
    }
}
