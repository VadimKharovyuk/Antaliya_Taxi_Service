package com.example.antaliya_taxi_service.maper;

import com.example.antaliya_taxi_service.dto.tour.*;
import com.example.antaliya_taxi_service.model.Tour;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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






    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Создает TourTranslationDto из Tour с переведенными полями
     */
    public TourTranslationDto toTourTranslationDto(Tour tour,
                                                   String translatedTitle,
                                                   String translatedDescription,
                                                   String translatedShortDescription,
                                                   String targetLanguage) {
        if (tour == null) {
            return null;
        }

        return TourTranslationDto.builder()
                .id(tour.getId())
                .title(translatedTitle != null ? translatedTitle : tour.getTitle())
                .description(translatedDescription != null ? translatedDescription : tour.getDescription())
                .shortDescription(translatedShortDescription != null ? translatedShortDescription : tour.getShortDescription())
                .price(tour.getPrice())
                .duration(tour.getDuration())
                .isBestseller(tour.getIsBestseller())
                .maxParticipants(tour.getMaxParticipants())
                .url(tour.getUrl())
                .views(tour.getViews())
                .uploadDate(tour.getUploadDate())
                .updateDate(tour.getUpdateDate())
                .language(targetLanguage)
                .formattedDate(tour.getUploadDate() != null ?
                        tour.getUploadDate().format(DATE_FORMATTER) : null)
                .readingTimeMinutes(calculateReadingTime(tour.getDescription()))
                .build();
    }

    /**
     * Создает TourTranslationDto из TourDetailDto с переведенными полями
     */
    public TourTranslationDto toTourTranslationDto(TourDetailsDto  tourDetail,
                                                   String translatedTitle,
                                                   String translatedDescription,
                                                   String translatedShortDescription,
                                                   String targetLanguage) {
        if (tourDetail == null) {
            return null;
        }

        return TourTranslationDto.builder()
                .id(tourDetail.getId())
                .title(translatedTitle != null ? translatedTitle : tourDetail.getTitle())
                .description(translatedDescription != null ? translatedDescription : tourDetail.getDescription())
                .shortDescription(translatedShortDescription != null ? translatedShortDescription : tourDetail.getShortDescription())
                .price(tourDetail.getPrice())
                .duration(tourDetail.getDuration())
                .isBestseller(tourDetail.getIsBestseller())
                .maxParticipants(tourDetail.getMaxParticipants())
                .url(tourDetail.getUrl())
                .views(tourDetail.getViews())
                .uploadDate(tourDetail.getUploadDate())
                .updateDate(tourDetail.getUpdateDate())
                .language(targetLanguage)
//                .formattedDate(tourDetail.getFormattedDate())
//                .readingTimeMinutes(tourDetail.getReadingTimeMinutes())
                .build();
    }

    /**
     * Создает TourCardTranslationDto из Tour с переведенными полями
     */
    public TourCardTranslationDto toTourCardTranslationDto(Tour tour,
                                                           String translatedTitle,
                                                           String translatedShortDescription,
                                                           String targetLanguage) {
        if (tour == null) {
            return null;
        }

        return TourCardTranslationDto.builder()
                .id(tour.getId())
                .title(translatedTitle != null ? translatedTitle : tour.getTitle())
                .shortDescription(translatedShortDescription != null ? translatedShortDescription : tour.getShortDescription())
                .url(tour.getUrl())
                .views(tour.getViews())
                .formattedDate(tour.getUploadDate() != null ?
                        tour.getUploadDate().format(DATE_FORMATTER) : null)
                .language(targetLanguage)
                .formattedPrice(formatPrice(tour.getPrice()))
                .formattedDuration(formatDuration(tour.getDuration()))
                .isBestseller(tour.getIsBestseller() != null ? tour.getIsBestseller() : false)
                .maxParticipants(tour.getMaxParticipants() != null ? tour.getMaxParticipants() : 0)
                .hasVipTransfer(tour.getIsBestseller() != null ? tour.getIsBestseller() : false)
                .build();
    }

    /**
     * Создает TourCardTranslationDto из TourCardDto с переведенными полями
     */
    public TourCardTranslationDto toTourCardTranslationDto(TourCardDto tourCard,
                                                           String translatedTitle,
                                                           String translatedShortDescription,
                                                           String targetLanguage) {
        if (tourCard == null) {
            return null;
        }

        return TourCardTranslationDto.builder()
                .id(tourCard.getId())
                .title(translatedTitle != null ? translatedTitle : tourCard.getTitle())
                .shortDescription(translatedShortDescription != null ? translatedShortDescription : tourCard.getShortDescription())
                .url(tourCard.getUrl())
                .views(tourCard.getViews())
//                .formattedDate(tourCard.getFormattedDate())
                .language(targetLanguage)
                .formattedPrice(tourCard.getFormattedPrice())
                .formattedDuration(tourCard.getFormattedDuration())
//                .isBestseller(tourCard.isBestseller())
                .maxParticipants(tourCard.getMaxParticipants())
//                .hasVipTransfer(tourCard.isHasVipTransfer())
                .build();
    }
    /**
     * Создает непереведенную версию TourTranslationDto из Tour
     */
    public TourTranslationDto toUntranslatedTourTranslationDto(Tour tour, String targetLanguage) {
        if (tour == null) {
            return null;
        }

        return TourTranslationDto.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .description(tour.getDescription())
                .shortDescription(tour.getShortDescription())
                .price(tour.getPrice())
                .duration(tour.getDuration())
                .isBestseller(tour.getIsBestseller())
                .maxParticipants(tour.getMaxParticipants())
                .url(tour.getUrl())
                .views(tour.getViews())
                .uploadDate(tour.getUploadDate())
                .updateDate(tour.getUpdateDate())
                .language(targetLanguage)
                .formattedDate(tour.getUploadDate() != null ?
                        tour.getUploadDate().format(DATE_FORMATTER) : null)
                .readingTimeMinutes(calculateReadingTime(tour.getDescription()))
                .build();
    }
    /**
     * Создает непереведенную версию TourTranslationDto из TourDetailDto
     */
    public TourTranslationDto toUntranslatedTourTranslationDto(TourDetailsDto tourDetail, String targetLanguage) {
        if (tourDetail == null) {
            return null;
        }

        return TourTranslationDto.builder()
                .id(tourDetail.getId())
                .title(tourDetail.getTitle())
                .description(tourDetail.getDescription())
                .shortDescription(tourDetail.getShortDescription())
                .price(tourDetail.getPrice())
                .duration(tourDetail.getDuration())
                .isBestseller(tourDetail.getIsBestseller())
                .maxParticipants(tourDetail.getMaxParticipants())
                .url(tourDetail.getUrl())
                .views(tourDetail.getViews())
                .uploadDate(tourDetail.getUploadDate())
                .updateDate(tourDetail.getUpdateDate())
                .language(targetLanguage)
//                .formattedDate(tourDetail.getFormattedDate())
//                .readingTimeMinutes(tourDetail.getReadingTimeMinutes())
                .build();
    }
    /**
     * Создает непереведенную версию TourCardTranslationDto из Tour
     */
    public TourCardTranslationDto toUntranslatedTourCardTranslationDto(Tour tour, String targetLanguage) {
        if (tour == null) {
            return null;
        }

        return TourCardTranslationDto.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .shortDescription(tour.getShortDescription())
                .url(tour.getUrl())
                .views(tour.getViews())
                .formattedDate(tour.getUploadDate() != null ?
                        tour.getUploadDate().format(DATE_FORMATTER) : null)
                .language(targetLanguage)
                .formattedPrice(formatPrice(tour.getPrice()))
                .formattedDuration(formatDuration(tour.getDuration()))
                .isBestseller(tour.getIsBestseller() != null ? tour.getIsBestseller() : false)
                .maxParticipants(tour.getMaxParticipants() != null ? tour.getMaxParticipants() : 0)
                .hasVipTransfer(tour.getIsBestseller() != null ? tour.getIsBestseller() : false)
                .build();
    }
    /**
     * Создает непереведенную версию TourCardTranslationDto из TourCardDto
     */
    public TourCardTranslationDto toUntranslatedTourCardTranslationDto(TourCardDto tourCard, String targetLanguage) {
        if (tourCard == null) {
            return null;
        }

        return TourCardTranslationDto.builder()
                .id(tourCard.getId())
                .title(tourCard.getTitle())
                .shortDescription(tourCard.getShortDescription())
                .url(tourCard.getUrl())
                .views(tourCard.getViews())
//                .formattedDate(tourCard.getFormattedDate())
                .language(targetLanguage)
                .formattedPrice(tourCard.getFormattedPrice())
                .formattedDuration(tourCard.getFormattedDuration())
//                .isBestseller(tourCard.isBestseller())
                .maxParticipants(tourCard.getMaxParticipants())
//                .hasVipTransfer(tourCard.isHasVipTransfer())
                .build();
    }
    /**
     * Вспомогательный метод для расчета времени чтения
     */
    private Integer calculateReadingTime(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 1;
        }
        int wordCount = text.trim().split("\\s+").length;
        return Math.max(1, (int) Math.ceil(wordCount / 200.0));
    }

    /**
     * Вспомогательный метод для форматирования цены
     */
    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "Цена по запросу";
        }
        return String.format("$%.2f", price);
    }



}
