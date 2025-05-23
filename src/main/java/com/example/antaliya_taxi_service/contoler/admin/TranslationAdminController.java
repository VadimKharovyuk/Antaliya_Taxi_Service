package com.example.antaliya_taxi_service.contoler.admin;


import com.example.antaliya_taxi_service.service.BlogTranslationService;

import com.example.antaliya_taxi_service.util.TranslationUsageMonitor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/translation")
@RequiredArgsConstructor
public class TranslationAdminController {

    private final BlogTranslationService blogTranslationService;
    private final TranslationUsageMonitor usageMonitor;
    private final CacheManager cacheManager;


    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTranslationStats() {
        long totalChars = usageMonitor.getCharactersTranslated();
        long remainingLimit = Math.max(0, 500000 - totalChars);
        double usagePercentage = (totalChars * 100.0) / 500000;

        // Статистика кэша
        Map<String, Object> cacheStats = new HashMap<>();
        try {
            var cache = cacheManager.getCache("translations");
            if (cache != null) {
                var nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
                var stats = nativeCache.stats();

                cacheStats.put("size", nativeCache.estimatedSize());
                cacheStats.put("hitCount", stats.hitCount());
                cacheStats.put("missCount", stats.missCount());
                cacheStats.put("hitRate", Math.round(stats.hitRate() * 10000.0) / 100.0); // в процентах
                cacheStats.put("evictionCount", stats.evictionCount());
            }
        } catch (Exception e) {
            cacheStats.put("error", "Не удалось получить статистику кэша");
        }

        Map<String, Object> result = Map.of(
                "totalRequests", usageMonitor.getRequestsCount(),
                "totalCharacters", totalChars,
                "remainingLimit", remainingLimit,
                "usagePercentage", Math.round(usagePercentage * 100.0) / 100.0,
                "cacheStats", cacheStats
        );

        return ResponseEntity.ok(result);
    }

    /**
     * Очистить кэш переводов
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<Map<String, String>> clearCache() {
        blogTranslationService.clearTranslationCache();
        return ResponseEntity.ok(Map.of("message", "Кэш переводов успешно очищен"));
    }
}
