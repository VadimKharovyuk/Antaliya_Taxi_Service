package com.example.antaliya_taxi_service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Кэш для конвертаций валют (наиболее часто используемый)
        Caffeine<Object, Object> currencyConversionsCache = Caffeine.newBuilder()
                .expireAfterWrite(6, TimeUnit.HOURS) // Соответствует периоду обновления курсов
                .expireAfterAccess(30, TimeUnit.MINUTES) // Очистка неиспользуемых конвертаций
                .initialCapacity(200)
                .maximumSize(1000)
                .evictionListener((key, value, cause) ->
                        log.debug("CurrencyConversions cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("currencyConversions", currencyConversionsCache.build());

        // Кэш для отдельных обменных курсов
        Caffeine<Object, Object> exchangeRatesCache = Caffeine.newBuilder()
                .expireAfterWrite(6, TimeUnit.HOURS)
                .initialCapacity(50)
                .maximumSize(100) // Ограниченное количество валютных пар
                .evictionListener((key, value, cause) ->
                        log.debug("ExchangeRates cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("exchangeRates", exchangeRatesCache.build());

        // Кэш для полного списка обменных курсов (редко запрашивается)
        Caffeine<Object, Object> allExchangeRatesCache = Caffeine.newBuilder()
                .expireAfterWrite(6, TimeUnit.HOURS)
                .initialCapacity(1)
                .maximumSize(10) // Только несколько записей, обычно одна
                .evictionListener((key, value, cause) ->
                        log.debug("AllExchangeRates cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("allExchangeRates", allExchangeRatesCache.build());

        return cacheManager;
    }

    /**
     * Бин для мониторинга статистики кэша
     */
    @Bean
    public CacheMonitor cacheMonitor(CacheManager cacheManager) {
        return new CacheMonitor(cacheManager);
    }

    /**
     * Вспомогательный класс для мониторинга статистики кэша
     */
    public static class CacheMonitor {
        private final CacheManager cacheManager;
        private static final Logger monitorLog = LoggerFactory.getLogger("CacheMonitor");

        public CacheMonitor(CacheManager cacheManager) {
            this.cacheManager = cacheManager;

            // Пример периодического логирования статистики кэша
            // В реальном приложении можно настроить через Spring Scheduled
            // или подключить к системе мониторинга
            logCacheStatistics();
        }

        public void logCacheStatistics() {
            // Этот метод можно вызывать периодически для логирования статистики
            if (cacheManager instanceof CaffeineCacheManager) {
                CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;

                caffeineCacheManager.getCacheNames().forEach(cacheName -> {
                    try {
                        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                                (com.github.benmanes.caffeine.cache.Cache<Object, Object>)
                                        caffeineCacheManager.getCache(cacheName).getNativeCache();

                        com.github.benmanes.caffeine.cache.stats.CacheStats stats = nativeCache.stats();

                        monitorLog.info("Cache '{}' statistics: hitCount={}, missCount={}, hitRate={}, " +
                                        "evictionCount={}, estimatedSize={}",
                                cacheName, stats.hitCount(), stats.missCount(), stats.hitRate(),
                                stats.evictionCount(), nativeCache.estimatedSize());

                    } catch (Exception e) {
                        monitorLog.warn("Failed to get statistics for cache '{}': {}", cacheName, e.getMessage());
                    }
                });
            }
        }
    }
}