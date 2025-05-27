package com.example.antaliya_taxi_service.service;//package com.example.antaliya_taxi_service.service;
//
//import com.example.antaliya_taxi_service.dto.tour.TourCardDto;
//import com.example.antaliya_taxi_service.dto.tour.TourCardTranslationDto;
//import com.example.antaliya_taxi_service.dto.tour.TourTranslationDto;
//import com.example.antaliya_taxi_service.maper.TourMapper;
//import com.example.antaliya_taxi_service.model.Tour;
//import com.example.antaliya_taxi_service.util.CachedTranslationService;
//import com.example.antaliya_taxi_service.util.TranslationUsageMonitor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class TourTranslationService extends BaseTranslationService<Tour, TourTranslationDto> {
//
//    private final TranslationUsageMonitor usageMonitor;
//    private final CacheManager cacheManager;
//    private final CachedTranslationService cachedTranslationService;
//    private final TourMapper tourMapper;
//
//    public TourTranslationService(TranslationService translationService,
//                                  TranslationUsageMonitor usageMonitor,
//                                  CacheManager cacheManager,
//                                  CachedTranslationService cachedTranslationService,
//                                  TourMapper tourMapper) {
//        super(translationService);
//        this.usageMonitor = usageMonitor;
//        this.cacheManager = cacheManager;
//        this.cachedTranslationService = cachedTranslationService;
//        this.tourMapper = tourMapper;
//    }
//
//    @Override
//    public TourTranslationDto translate(Tour tour, String targetLanguage, String sourceLanguage) {
//        if (!isLanguageSupported(targetLanguage)) {
//            log.warn("Неподдерживаемый язык: {}", targetLanguage);
//            return tourMapper.toUntranslatedTranslationDto(tour, targetLanguage);
//        }
//
//        logTranslationStart(tour.getId(), targetLanguage);
//
//        String translatedTitle = translateAndRecord(tour.getTitle(), targetLanguage, sourceLanguage);
//        String translatedDesc = translateAndRecord(tour.getDescription(), targetLanguage, sourceLanguage);
//        String translatedShortDesc = translateAndRecord(tour.getShortDescription(), targetLanguage, sourceLanguage);
//
//        TourTranslationDto result = tourMapper.toTranslationDto(
//                tour, translatedTitle, translatedDesc, translatedShortDesc, targetLanguage
//        );
//
//        logTranslationComplete(tour.getId(), targetLanguage);
//        logStatisticsIfNeeded();
//
//        return result;
//    }
//
//    public TourCardTranslationDto translateTourCard(TourCardDto card, String targetLanguage, String sourceLanguage) {
//        if (!isLanguageSupported(targetLanguage)) {
//            log.warn("Неподдерживаемый язык: {}", targetLanguage);
//            return tourMapper.toUntranslatedCardTranslationDto(card, targetLanguage);
//        }
//
//        log.debug("Переводим карточку тура {} на язык {}", card.getId(), targetLanguage);
//
//        String translatedTitle = translateAndRecord(card.getTitle(), targetLanguage, sourceLanguage);
//        String translatedShortDesc = translateAndRecord(card.getShortDescription(), targetLanguage, sourceLanguage);
//
//        return tourMapper.toCardTranslationDto(card, translatedTitle, translatedShortDesc, targetLanguage);
//    }
//
//    private String translateAndRecord(String text, String targetLanguage, String sourceLanguage) {
//        if (text == null || text.trim().isEmpty()) return text;
//
//        String translation = cachedTranslationService.translateText(text, targetLanguage, sourceLanguage);
//
//        if (!translation.equals(text)) {
//            usageMonitor.recordTranslation(text);
//        }
//
//        return translation;
//    }
//
//    private void logStatisticsIfNeeded() {
//        if (usageMonitor.getRequestsCount() % 10 == 0) {
//            usageMonitor.logCurrentUsage();
//            logCacheStatistics();
//        }
//    }
//
//    @CacheEvict(value = "translations", allEntries = true)
//    public void clearTranslationCache() {
//        log.info("Кэш переводов для туров очищен");
//    }
//
//    public void logCacheStatistics() {
//        try {
//            var cache = cacheManager.getCache("translations");
//            if (cache != null) {
//                var nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
//                var stats = nativeCache.stats();
//
//                log.info("=== Статистика кэша переводов (туры) ===");
//                log.info("Размер кэша: {}", nativeCache.estimatedSize());
//                log.info("Попаданий: {}, Промахов: {}", stats.hitCount(), stats.missCount());
//                log.info("Hit Rate: {:.2f}%", stats.hitRate() * 100);
//                log.info("Удалений: {}", stats.evictionCount());
//            }
//        } catch (Exception e) {
//            log.warn("Ошибка получения статистики кэша: {}", e.getMessage());
//        }
//    }
//}



import com.example.antaliya_taxi_service.dto.tour.TourCardDto;
import com.example.antaliya_taxi_service.dto.tour.TourCardTranslationDto;
import com.example.antaliya_taxi_service.dto.tour.TourDetailsDto;
import com.example.antaliya_taxi_service.dto.tour.TourTranslationDto;

import com.example.antaliya_taxi_service.maper.TourTranslationMapper;
import com.example.antaliya_taxi_service.model.Tour;
import com.example.antaliya_taxi_service.util.CachedTranslationService;
import com.example.antaliya_taxi_service.util.TranslationUsageMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TourTranslationService extends BaseTranslationService<Tour, TourTranslationDto> {

    private final TranslationUsageMonitor usageMonitor;
    private final CacheManager cacheManager;
    private final CachedTranslationService cachedTranslationService;
    private final TourTranslationMapper tourTranslationMapper;

    public TourTranslationService(TranslationService translationService,
                                  TranslationUsageMonitor usageMonitor,
                                  CacheManager cacheManager,
                                  CachedTranslationService cachedTranslationService,
                                  TourTranslationMapper tourTranslationMapper) {
        super(translationService);
        this.usageMonitor = usageMonitor;
        this.cacheManager = cacheManager;
        this.cachedTranslationService = cachedTranslationService;
        this.tourTranslationMapper = tourTranslationMapper;
    }


    @Override
    public TourTranslationDto translate(Tour tour, String targetLanguage, String sourceLanguage) {
        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            return tourTranslationMapper.toUntranslatedTourTranslationDto(tour, targetLanguage);
        }

        logTranslationStart(tour.getId(), targetLanguage);

        String translatedTitle = translateAndRecord(tour.getTitle(), targetLanguage, sourceLanguage);
        String translatedDesc = translateAndRecord(tour.getDescription(), targetLanguage, sourceLanguage);
        String translatedShortDesc = translateAndRecord(tour.getShortDescription(), targetLanguage, sourceLanguage);

        TourTranslationDto result = tourTranslationMapper.toTourTranslationDto(
                tour, translatedTitle, translatedDesc, translatedShortDesc, targetLanguage
        );

        logTranslationComplete(tour.getId(), targetLanguage);
        logStatisticsIfNeeded();

        return result;
    }


    /**
     * Переводит карточку тура из TourCardDto
     */
    public TourCardTranslationDto translateTourCard(TourCardDto card, String targetLanguage, String sourceLanguage) {
        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            return tourTranslationMapper.toUntranslatedTourCardTranslationDto(card, targetLanguage);
        }

        log.debug("Переводим карточку тура {} на язык {}", card.getId(), targetLanguage);

        String translatedTitle = translateAndRecord(card.getTitle(), targetLanguage, sourceLanguage);
        String translatedShortDesc = translateAndRecord(card.getShortDescription(), targetLanguage, sourceLanguage);

        return tourTranslationMapper.toTourCardTranslationDto(
                card, translatedTitle, translatedShortDesc, targetLanguage
        );
    }

    private String translateAndRecord(String text, String targetLanguage, String sourceLanguage) {
        if (text == null || text.trim().isEmpty()) return text;

        String translation = cachedTranslationService.translateText(text, targetLanguage, sourceLanguage);

        if (!translation.equals(text)) {
            usageMonitor.recordTranslation(text);
        }

        return translation;
    }

    private void logStatisticsIfNeeded() {
        if (usageMonitor.getRequestsCount() % 10 == 0) {
            usageMonitor.logCurrentUsage();
            logCacheStatistics();
        }
    }

    @CacheEvict(value = "translations", allEntries = true)
    public void clearTranslationCache() {
        log.info("Кэш переводов для туров очищен");
    }

    public void logCacheStatistics() {
        try {
            var cache = cacheManager.getCache("translations");
            if (cache != null) {
                var nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
                var stats = nativeCache.stats();

                log.info("=== Статистика кэша переводов (туры) ===");
                log.info("Размер кэша: {}", nativeCache.estimatedSize());
                log.info("Попаданий: {}, Промахов: {}", stats.hitCount(), stats.missCount());
                log.info("Hit Rate: {:.2f}%", stats.hitRate() * 100);
                log.info("Удалений: {}", stats.evictionCount());
            }
        } catch (Exception e) {
            log.warn("Ошибка получения статистики кэша: {}", e.getMessage());
        }
    }
}