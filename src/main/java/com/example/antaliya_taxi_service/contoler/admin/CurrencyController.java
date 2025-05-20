package com.example.antaliya_taxi_service.contoler.admin;

import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.service.CurrencyService;
import com.example.antaliya_taxi_service.service.impl.CurrencyServiceImplApi;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final CacheManager cacheManager;


    @GetMapping("/rates")
    public Map<String, Object> getAllRates() {
        Map<String, Object> response = new HashMap<>();
        response.put("rates", currencyService.getAllExchangeRates());
        response.put("lastUpdated", ((CurrencyServiceImplApi) currencyService).getLastUpdated());
        return response;
    }

    @GetMapping("/convert")
    public Map<String, Object> convertCurrency(
            @RequestParam BigDecimal amount,
            @RequestParam Currency from,
            @RequestParam Currency to) {

        BigDecimal convertedAmount = currencyService.convert(amount, from, to);
        BigDecimal rate = currencyService.getExchangeRate(from, to);

        Map<String, Object> response = new HashMap<>();
        response.put("from", from);
        response.put("to", to);
        response.put("amount", amount);
        response.put("convertedAmount", convertedAmount);
        response.put("rate", rate);
        response.put("lastUpdated", ((CurrencyServiceImplApi) currencyService).getLastUpdated());

        return response;
    }

    @GetMapping("/update")
    public Map<String, Object> updateRates() {
        currencyService.updateExchangeRates();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("lastUpdated", ((CurrencyServiceImplApi) currencyService).getLastUpdated());
        return response;
    }

    /**
     * Эндпоинт для получения статистики кэша
     * Доступен только для администраторов или в dev/test окружении
     */
    @GetMapping("/cache-stats")
    public ResponseEntity<Map<String, Object>> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();

        cacheManager.getCacheNames().forEach(cacheName -> {
            Map<String, Object> cacheStats = new HashMap<>();
            org.springframework.cache.Cache springCache = cacheManager.getCache(cacheName);

            if (springCache instanceof CaffeineCache) {
                CaffeineCache caffeineCache = (CaffeineCache) springCache;
                Cache<Object, Object> nativeCache = (Cache<Object, Object>) caffeineCache.getNativeCache();

                com.github.benmanes.caffeine.cache.stats.CacheStats stats2 = nativeCache.stats();

                cacheStats.put("size", nativeCache.estimatedSize());
                cacheStats.put("hitCount", stats2.hitCount());
                cacheStats.put("missCount", stats2.missCount());
                cacheStats.put("hitRate", stats2.hitRate());
                cacheStats.put("evictionCount", stats2.evictionCount());
                cacheStats.put("evictionWeight", stats2.evictionWeight());

                stats.put(cacheName, cacheStats);
            } else {
                cacheStats.put("error", "Not a Caffeine cache");
                stats.put(cacheName, cacheStats);
            }
        });

        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    /**
     * Эндпоинт для очистки кэша
     * Доступен только для администраторов или в dev/test окружении
     */
    @DeleteMapping("/cache")
    public ResponseEntity<Map<String, Object>> clearCache(
            @RequestParam(required = false) String cacheName) {

        Map<String, Object> response = new HashMap<>();

        if (cacheName != null && !cacheName.isEmpty()) {
            // Очистка конкретного кэша
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                response.put("message", "Cache '" + cacheName + "' cleared successfully");
            } else {
                response.put("error", "Cache '" + cacheName + "' not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } else {
            // Очистка всех кэшей
            cacheManager.getCacheNames().forEach(name -> {
                org.springframework.cache.Cache cache = cacheManager.getCache(name);
                if (cache != null) {
                    cache.clear();
                }
            });
            response.put("message", "All caches cleared successfully");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}