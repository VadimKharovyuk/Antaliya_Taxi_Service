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


        // ДОБАВЛЯЕМ: Кэш для переводов
        Caffeine<Object, Object> translationsCache = Caffeine.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS) // Переводы актуальны долго
                .expireAfterAccess(24, TimeUnit.HOURS) // Если не используется 24ч - удаляем
                .initialCapacity(1000) // Много текстов для перевода
                .maximumSize(10000) // Большой размер для экономии API
                .evictionListener((key, value, cause) ->
                        log.debug("Translations cache eviction: key={}, cause={}",
                                key.toString().substring(0, Math.min(50, key.toString().length())), cause))
                .recordStats();
        cacheManager.registerCustomCache("translations", translationsCache.build());



        // Кэш для конвертаций валют (наиболее часто используемый)
        Caffeine<Object, Object> currencyConversionsCache = Caffeine.newBuilder()
                .expireAfterWrite(12, TimeUnit.HOURS) // Увеличено до 12 часов
                .expireAfterAccess(3, TimeUnit.HOURS) // Увеличено до 3 часов
                .initialCapacity(500) // Увеличена начальная ёмкость
                .maximumSize(5000) // Значительно увеличен максимальный размер
                .evictionListener((key, value, cause) ->
                        log.debug("CurrencyConversions cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("currencyConversions", currencyConversionsCache.build());

        // Кэш для отдельных обменных курсов
        Caffeine<Object, Object> exchangeRatesCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS) // Увеличено до 24 часов
                .initialCapacity(100) // Увеличена начальная ёмкость
                .maximumSize(500) // Увеличен максимальный размер
                .evictionListener((key, value, cause) ->
                        log.debug("ExchangeRates cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("exchangeRates", exchangeRatesCache.build());

        // Кэш для полного списка обменных курсов
        Caffeine<Object, Object> allExchangeRatesCache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS) // Увеличено до 24 часов
                .initialCapacity(5) // Увеличена начальная ёмкость
                .maximumSize(50) // Увеличен максимальный размер
                .evictionListener((key, value, cause) ->
                        log.debug("AllExchangeRates cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("allExchangeRates", allExchangeRatesCache.build());

        // Новый кэш для популярных валютных пар с более долгим хранением
        Caffeine<Object, Object> popularCurrencyPairsCache = Caffeine.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS) // Хранить до 7 дней
                .initialCapacity(20)
                .maximumSize(100)
                .evictionListener((key, value, cause) ->
                        log.debug("PopularCurrencyPairs cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("popularCurrencyPairs", popularCurrencyPairsCache.build());

        // Кэш для исторических курсов (если вы расширите функциональность)
        Caffeine<Object, Object> historicalRatesCache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.DAYS) // Хранить до 30 дней
                .initialCapacity(50)
                .maximumSize(1000)
                .evictionListener((key, value, cause) ->
                        log.debug("HistoricalRates cache eviction: key={}, cause={}", key, cause))
                .recordStats();
        cacheManager.registerCustomCache("historicalRates", historicalRatesCache.build());




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