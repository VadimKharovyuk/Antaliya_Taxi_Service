package com.example.antaliya_taxi_service.maper;

import com.example.antaliya_taxi_service.dto.tour.*;
import com.example.antaliya_taxi_service.model.Tour;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class TourMapper {

    public TourDto toDto(Tour tour) {
        if (tour == null) {
            return null;
        }

        return TourDto.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .description(tour.getDescription())
                .shortDescription(tour.getShortDescription())
                .price(tour.getPrice())
                .duration(tour.getDuration())
                .isBestseller(tour.getIsBestseller())
                .maxParticipants(tour.getMaxParticipants())
                .language(tour.getLanguage())
                .url(tour.getUrl())
                .views(tour.getViews())
                .uploadDate(tour.getUploadDate())
                .updateDate(tour.getUpdateDate())
                .build();
    }

    public Tour toEntity(TourDto dto) {
        if (dto == null) {
            return null;
        }

        return Tour.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .shortDescription(dto.getShortDescription())
                .price(dto.getPrice())
                .duration(dto.getDuration())
                .isBestseller(dto.getIsBestseller())
                .maxParticipants(dto.getMaxParticipants())
                .language(dto.getLanguage())
                .url(dto.getUrl())
                .views(dto.getViews())
                .uploadDate(dto.getUploadDate())
                .updateDate(dto.getUpdateDate())
                .build();
    }

    public TourDetailsDto toDetailsDto(Tour tour) {
        if (tour == null) {
            return null;
        }

        return TourDetailsDto.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .description(tour.getDescription())
                .shortDescription(tour.getShortDescription())
                .price(tour.getPrice())
                .duration(tour.getDuration())
                .isBestseller(tour.getIsBestseller())
                .maxParticipants(tour.getMaxParticipants())
                .language(tour.getLanguage())
                .url(tour.getUrl())
                .views(tour.getViews())
                .uploadDate(tour.getUploadDate())
                .updateDate(tour.getUpdateDate())
                .formattedPrice("от " + tour.getPrice() + " €")
                .formattedDuration(formatDuration(tour.getDuration()))
                .build();
    }

    // Преобразование в карточку для списка
    public TourCardDto toCardDto(Tour tour) {
        if (tour == null) {
            return null;
        }

        return TourCardDto.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .shortDescription(tour.getShortDescription())
                .price(tour.getPrice())
                .duration(tour.getDuration())
                .isBestseller(tour.getIsBestseller())
                .maxParticipants(tour.getMaxParticipants())
                .language(tour.getLanguage())
                .url(tour.getUrl())
                .imageId(tour.getImageId())
                .views(tour.getViews())
                .formattedPrice("от " + tour.getPrice() + " €")
                .formattedDuration(formatDuration(tour.getDuration()))
                .hasVipTransfer(true)
                .build();
    }

    public Tour createTourFromDto(TourCreateDto dto) {
        if (dto == null) {
            return null;
        }

        return Tour.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .shortDescription(dto.getShortDescription())
                .price(dto.getPrice())
                .duration(dto.getDuration())
                .isBestseller(dto.getIsBestseller())
                .maxParticipants(dto.getMaxParticipants())
                .language(dto.getLanguage())
                .views(0)
                .build();
    }

    public void updateTourFromDto(Tour tour, TourUpdateDto dto) {
        if (tour == null || dto == null) {
            return;
        }

        if (dto.getTitle() != null) tour.setTitle(dto.getTitle());
        if (dto.getDescription() != null) tour.setDescription(dto.getDescription());
        if (dto.getShortDescription() != null) tour.setShortDescription(dto.getShortDescription());
        if (dto.getPrice() != null) tour.setPrice(dto.getPrice());
        if (dto.getDuration() != null) tour.setDuration(dto.getDuration());
        if (dto.getIsBestseller() != null) tour.setIsBestseller(dto.getIsBestseller());
        if (dto.getMaxParticipants() != null) tour.setMaxParticipants(dto.getMaxParticipants());
        if (dto.getLanguage() != null) tour.setLanguage(dto.getLanguage());
    }

    private String formatDuration(Integer duration) {
        if (duration == null) return "";
        if (duration == 1) return "1 час";
        if (duration >= 2 && duration <= 4) return duration + " часа";
        return duration + " часов";
    }
}