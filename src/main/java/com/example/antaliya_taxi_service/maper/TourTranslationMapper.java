package com.example.antaliya_taxi_service.maper;//package com.example.antaliya_taxi_service.maper;
//
//
//import com.example.antaliya_taxi_service.dto.tour.TourCardDto;
//import com.example.antaliya_taxi_service.dto.tour.TourCardTranslationDto;
//import com.example.antaliya_taxi_service.dto.tour.TourDetailsDto;
//import com.example.antaliya_taxi_service.dto.tour.TourTranslationDto;
//import com.example.antaliya_taxi_service.model.Tour;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.format.DateTimeFormatter;
//
//
//@Component
//public class TourTranslationMapper {
//
//    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
//
//    /**
//     * Создает TourTranslationDto из Tour с переведенными полями
//     */
//    public TourTranslationDto toTourTranslationDto(Tour tour,
//                                                   String translatedTitle,
//                                                   String translatedDescription,
//                                                   String translatedShortDescription,
//                                                   String targetLanguage) {
//        if (tour == null) {
//            return null;
//        }
//
//        return TourTranslationDto.builder()
//                .id(tour.getId())
//                .title(translatedTitle != null ? translatedTitle : tour.getTitle())
//                .description(translatedDescription != null ? translatedDescription : tour.getDescription())
//                .shortDescription(translatedShortDescription != null ? translatedShortDescription : tour.getShortDescription())
//                .price(tour.getPrice())
//                .duration(tour.getDuration())
//                .isBestseller(tour.getIsBestseller())
//                .maxParticipants(tour.getMaxParticipants())
//                .url(tour.getUrl())
//                .views(tour.getViews())
//                .uploadDate(tour.getUploadDate())
//                .updateDate(tour.getUpdateDate())
//                .language(targetLanguage)
//                .formattedDate(tour.getUploadDate() != null ?
//                        tour.getUploadDate().format(DATE_FORMATTER) : null)
//                .readingTimeMinutes(calculateReadingTime(tour.getDescription()))
//                .build();
//    }
//
//    /**
//     * Создает TourCardTranslationDto из TourCardDto с переведенными полями
//     */
//    public TourCardTranslationDto toTourCardTranslationDto(TourCardDto tourCard,
//                                                           String translatedTitle,
//                                                           String translatedShortDescription,
//                                                           String targetLanguage) {
//        if (tourCard == null) {
//            return null;
//        }
//
//        return TourCardTranslationDto.builder()
//                .id(tourCard.getId())
//                .title(translatedTitle != null ? translatedTitle : tourCard.getTitle())
//                .shortDescription(translatedShortDescription != null ? translatedShortDescription : tourCard.getShortDescription())
//                .url(tourCard.getUrl())
//                .views(tourCard.getViews())
////                .formattedDate(tourCard.getFormattedDate())
//                .language(targetLanguage)
//                .formattedPrice(tourCard.getFormattedPrice())
//                .formattedDuration(tourCard.getFormattedDuration())
////                .isBestseller(tourCard.isBestseller())
//                .maxParticipants(tourCard.getMaxParticipants())
////                .hasVipTransfer(tourCard.isHasVipTransfer())
//                .build();
//    }
//
//    /**
//     * Создает непереведенную версию TourTranslationDto из Tour
//     */
//    public TourTranslationDto toUntranslatedTourTranslationDto(Tour tour, String targetLanguage) {
//        if (tour == null) {
//            return null;
//        }
//
//        return TourTranslationDto.builder()
//                .id(tour.getId())
//                .title(tour.getTitle())
//                .description(tour.getDescription())
//                .shortDescription(tour.getShortDescription())
//                .price(tour.getPrice())
//                .duration(tour.getDuration())
//                .isBestseller(tour.getIsBestseller())
//                .maxParticipants(tour.getMaxParticipants())
//                .url(tour.getUrl())
//                .views(tour.getViews())
//                .uploadDate(tour.getUploadDate())
//                .updateDate(tour.getUpdateDate())
//                .language(targetLanguage)
//                .formattedDate(tour.getUploadDate() != null ?
//                        tour.getUploadDate().format(DATE_FORMATTER) : null)
//                .readingTimeMinutes(calculateReadingTime(tour.getDescription()))
//                .build();
//    }
//
//    /**
//     * Создает непереведенную версию TourCardTranslationDto из TourCardDto
//     */
//    public TourCardTranslationDto toUntranslatedTourCardTranslationDto(TourCardDto tourCard, String targetLanguage) {
//        if (tourCard == null) {
//            return null;
//        }
//
//        return TourCardTranslationDto.builder()
//                .id(tourCard.getId())
//                .title(tourCard.getTitle())
//                .shortDescription(tourCard.getShortDescription())
//                .url(tourCard.getUrl())
//                .views(tourCard.getViews())
////                .formattedDate(tourCard.getFormattedDate())
//                .language(targetLanguage)
//                .formattedPrice(tourCard.getFormattedPrice())
//                .formattedDuration(tourCard.getFormattedDuration())
////                .isBestseller(tourCard.isBestseller())
//                .maxParticipants(tourCard.getMaxParticipants())
////                .hasVipTransfer(tourCard.isHasVipTransfer())
//                .build();
//    }
//
//    /**
//     * Вспомогательный метод для расчета времени чтения
//     */
//    private Integer calculateReadingTime(String text) {
//        if (text == null || text.trim().isEmpty()) {
//            return 1;
//        }
//        int wordCount = text.trim().split("\\s+").length;
//        return Math.max(1, (int) Math.ceil(wordCount / 200.0));
//    }
//
//
//    public TourTranslationDto mapToTranslatedDto(Tour tour, String targetLanguage) {
//        return TourTranslationDto.builder()
//                .id(tour.getId())
//                .title(tour.getTitle())
//                .description(tour.getDescription())
//                .shortDescription(tour.getShortDescription())
//                .language(targetLanguage) // сохраняем язык, даже если перевод не делался
//                .build();
//    }
//}



import com.example.antaliya_taxi_service.dto.tour.TourTranslationDto;
import com.example.antaliya_taxi_service.dto.tour.TourCardTranslationDto;
import com.example.antaliya_taxi_service.dto.tour.TourCardDto;

import com.example.antaliya_taxi_service.model.Tour;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Component
public class TourTranslationMapper {

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


    public TourTranslationDto toUntranslatedTourTranslationDto(Tour tour, String lang) {
        return TourTranslationDto.builder()
                .id(tour.getId())
                .title(tour.getTitle())
                .description(tour.getDescription())
                .shortDescription(tour.getShortDescription())
                .price(tour.getPrice())
                .duration(tour.getDuration())
//                .isBestseller(tour.isBestseller())
                .maxParticipants(tour.getMaxParticipants())
                .url(tour.getUrl())
                .views(tour.getViews())
                .uploadDate(tour.getUploadDate())
                .updateDate(tour.getUpdateDate())
                .language(lang)
                .formattedDate(null) // если нет форматирования — можно оставить null
                .readingTimeMinutes(null) // аналогично
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


}