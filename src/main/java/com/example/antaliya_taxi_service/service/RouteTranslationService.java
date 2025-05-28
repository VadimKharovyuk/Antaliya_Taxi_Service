package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.maper.RouteMapper;
import com.example.antaliya_taxi_service.maper.TourTranslationMapper;
import com.example.antaliya_taxi_service.util.CachedTranslationService;
import com.example.antaliya_taxi_service.util.TranslationUsageMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service

@Slf4j
public class RouteTranslationService extends BaseTranslationService<RouteDto.Response, RouteDto.Response>{


    private final TranslationUsageMonitor usageMonitor;
    private final CacheManager cacheManager;
    private final CachedTranslationService cachedTranslationService;
    private final RouteMapper routeMapper;

    public RouteTranslationService(TranslationService translationService, TranslationUsageMonitor usageMonitor, CacheManager cacheManager, CachedTranslationService cachedTranslationService, RouteMapper routeMapper) {
        super(translationService);
        this.usageMonitor = usageMonitor;
        this.cacheManager = cacheManager;
        this.cachedTranslationService = cachedTranslationService;
        this.routeMapper = routeMapper;
    }

    @Override
    public RouteDto.Response translate(RouteDto.Response route, String targetLanguage, String sourceLanguage) {
        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            route.setTargetLanguage(targetLanguage);
            return route;
        }

        log.debug("Перевод маршрута ID={} на язык {}", route.getId(), targetLanguage);

        String translatedPickup = translateAndRecord(route.getPickupLocation(), targetLanguage, sourceLanguage);
        String translatedDropoff = translateAndRecord(route.getDropoffLocation(), targetLanguage, sourceLanguage);

        RouteDto.Response translated = routeMapper.toTranslatedResponse(
                route,
                translatedPickup,
                translatedDropoff,
                targetLanguage
        );

        logTranslationComplete(route.getId(), targetLanguage);
        logStatisticsIfNeeded();
        return translated;
    }

    private String translateAndRecord(String text, String targetLanguage, String sourceLanguage) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

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

    public void logCacheStatistics() {
        try {
            var cache = cacheManager.getCache("translations");
            if (cache != null) {
                var nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
                var stats = nativeCache.stats();

                log.info("=== Статистика кэша переводов (маршруты) ===");
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
