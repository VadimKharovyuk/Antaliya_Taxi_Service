package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.dto.blog.BlogCardDto;
import com.example.antaliya_taxi_service.dto.blog.BlogDto;
import com.example.antaliya_taxi_service.dto.blog.BlogListDto;
import com.example.antaliya_taxi_service.dto.blog.CreateBlogDto;
import com.example.antaliya_taxi_service.dto.tour.TourCardDto;
import com.example.antaliya_taxi_service.dto.tour.TourCreateDto;
import com.example.antaliya_taxi_service.dto.tour.TourDto;
import com.example.antaliya_taxi_service.dto.tour.TourListDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private final TourMapper tourMapper;
    private final TourRepository tourRepository;
    private final StorageService storageService ;

    @Override
    public TourDto createTour(TourCreateDto tourCreateDto) throws IOException {
        Tour tour = tourMapper.createTourFromDto(tourCreateDto);

        if (tourCreateDto.getImage() != null && !tourCreateDto.getImage().isEmpty()) {
            StorageService.StorageResult storageResult = storageService.uploadImage(tourCreateDto.getImage());
            tour.setUrl(storageResult.getUrl());
            tour.setImageId(storageResult.getImageId());
        }
        tourRepository.save(tour);
        return tourMapper.toDto(tour);
    }

    @Override
    public TourDto findTourById(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тур с id " + id + " не найден"));
        return tourMapper.toDto(tour);
    }

    @Override
    public TourListDto getAll(Pageable pageable) {
        Page<Tour> tourPage = tourRepository.findAll(pageable);

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
    public TourListDto getTop6ToursForMainPage() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<Tour> tourPage = tourRepository.findAll(pageable);

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
    @Transactional
    public void deleteBlog(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тур с id " + id + " не найден"));
        storageService.deleteImage(tour.getImageId());
        tourRepository.delete(tour);
    }


}
