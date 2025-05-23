package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.blog.BlogCardDto;
import com.example.antaliya_taxi_service.dto.blog.BlogCardTranslationDto;
import com.example.antaliya_taxi_service.dto.blog.BlogDetailDto;
import com.example.antaliya_taxi_service.dto.blog.BlogTranslationDto;
import com.example.antaliya_taxi_service.maper.BlogMapper;
import com.example.antaliya_taxi_service.model.Blog;
import com.example.antaliya_taxi_service.util.CachedTranslationService;
import com.example.antaliya_taxi_service.util.TranslationUsageMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BlogTranslationService extends BaseTranslationService<Blog, BlogTranslationDto> {

    private final TranslationUsageMonitor usageMonitor;
    private final CacheManager cacheManager;
    private final CachedTranslationService cachedTranslationService;
    private final BlogMapper blogMapper;

    public BlogTranslationService(TranslationService translationService,
                                  TranslationUsageMonitor usageMonitor,
                                  CacheManager cacheManager,
                                  CachedTranslationService cachedTranslationService,
                                  BlogMapper blogMapper) {
        super(translationService);
        this.usageMonitor = usageMonitor;
        this.cacheManager = cacheManager;
        this.cachedTranslationService = cachedTranslationService;
        this.blogMapper = blogMapper;
    }

    @Override
    public BlogTranslationDto translate(Blog blog, String targetLanguage, String sourceLanguage) {
        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            return blogMapper.toUntranslatedBlogTranslationDto(blog, targetLanguage);
        }

        logTranslationStart(blog.getId(), targetLanguage);

        // Переводим текстовые поля
        String translatedTitle = translateAndRecord(blog.getTitle(), targetLanguage, sourceLanguage);
        String translatedDescription = translateAndRecord(blog.getDescription(), targetLanguage, sourceLanguage);
        String translatedShortDesc = translateAndRecord(blog.getShotDescription(), targetLanguage, sourceLanguage);

        // Используем mapper для создания результата
        BlogTranslationDto result = blogMapper.toBlogTranslationDto(
                blog,
                translatedTitle,
                translatedDescription,
                translatedShortDesc,
                targetLanguage
        );

        logTranslationComplete(blog.getId(), targetLanguage);
        logStatisticsIfNeeded();

        return result;
    }

    /**
     * Переводит BlogDetailDto на указанный язык
     */
    public BlogTranslationDto translateBlogDetail(BlogDetailDto blogDetail, String targetLanguage) {
        return translateBlogDetail(blogDetail, targetLanguage, null);
    }

    /**
     * Переводит BlogDetailDto с указанием исходного языка
     */
    public BlogTranslationDto translateBlogDetail(BlogDetailDto blogDetail, String targetLanguage, String sourceLanguage) {
        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            return blogMapper.toUntranslatedBlogTranslationDto(blogDetail, targetLanguage);
        }

        logTranslationStart(blogDetail.getId(), targetLanguage);

        // Переводим текстовые поля
        String translatedTitle = translateAndRecord(blogDetail.getTitle(), targetLanguage, sourceLanguage);
        String translatedDescription = translateAndRecord(blogDetail.getDescription(), targetLanguage, sourceLanguage);
        String translatedShortDesc = translateAndRecord(blogDetail.getShotDescription(), targetLanguage, sourceLanguage);

        // Используем mapper для создания результата
        BlogTranslationDto result = blogMapper.toBlogTranslationDto(
                blogDetail,
                translatedTitle,
                translatedDescription,
                translatedShortDesc,
                targetLanguage
        );

        logTranslationComplete(blogDetail.getId(), targetLanguage);
        return result;
    }

    /**
     * Переводит карточку блога на указанный язык
     */
    public BlogCardTranslationDto translateBlogCard(BlogCardDto blogCard, String targetLanguage) {
        return translateBlogCard(blogCard, targetLanguage, null);
    }

    /**
     * Переводит карточку блога с указанием исходного языка
     */
    public BlogCardTranslationDto translateBlogCard(BlogCardDto blogCard, String targetLanguage, String sourceLanguage) {
        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            return blogMapper.toUntranslatedBlogCardTranslationDto(blogCard, targetLanguage);
        }

        log.debug("Переводим карточку блога {} на язык {}", blogCard.getId(), targetLanguage);

        // Переводим текстовые поля
        String translatedTitle = translateAndRecord(blogCard.getTitle(), targetLanguage, sourceLanguage);
        String translatedShortDesc = translateAndRecord(blogCard.getShotDescription(), targetLanguage, sourceLanguage);

        // Используем mapper для создания результата
        return blogMapper.toBlogCardTranslationDto(
                blogCard,
                translatedTitle,
                translatedShortDesc,
                targetLanguage
        );
    }

    /**
     * Переводит текст и записывает статистику использования
     */
    private String translateAndRecord(String text, String targetLanguage, String sourceLanguage) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        // Используем кэширующий сервис для перевода
        String translation = cachedTranslationService.translateText(text, targetLanguage, sourceLanguage);

        // Записываем статистику только если текст был переведен
        if (!translation.equals(text)) {
            usageMonitor.recordTranslation(text);
        }

        return translation;
    }

    /**
     * Логирует статистику каждые 10 запросов
     */
    private void logStatisticsIfNeeded() {
        if (usageMonitor.getRequestsCount() % 10 == 0) {
            usageMonitor.logCurrentUsage();
            logCacheStatistics();
        }
    }

    @CacheEvict(value = "translations", allEntries = true)
    public void clearTranslationCache() {
        log.info("Кэш переводов очищен");
    }

    public void logCacheStatistics() {
        try {
            var cache = cacheManager.getCache("translations");
            if (cache != null) {
                var nativeCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) cache.getNativeCache();
                var stats = nativeCache.stats();

                log.info("=== Статистика кэша переводов ===");
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