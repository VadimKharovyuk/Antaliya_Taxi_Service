package com.example.antaliya_taxi_service.util;// 1. СОЗДАЙТЕ ЭТОТ НОВЫЙ ФАЙЛ: CachedTranslationService.java


import com.example.antaliya_taxi_service.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CachedTranslationService {

    private final TranslationService translationService;

    /**
     * КЭШИРОВАННЫЙ перевод текста - ОТДЕЛЬНЫЙ СЕРВИС для правильной работы AOP
     */
    @Cacheable(value = "translations",
            key = "#text.hashCode() + '_' + #targetLanguage + '_' + (#sourceLanguage ?: 'auto')")
    public String translateText(String text, String targetLanguage, String sourceLanguage) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        log.info("🔥 CACHE MISS: Выполняем новый перевод для '{}' -> {} (ключ: {})",
                text.length() > 50 ? text.substring(0, 50) + "..." : text,
                targetLanguage,
                text.hashCode() + "_" + targetLanguage + "_" + (sourceLanguage != null ? sourceLanguage : "auto"));

        String result = translationService.translateText(text, targetLanguage, sourceLanguage);

        log.info("✅ Перевод выполнен: '{}', сохранен в кэш",
                result.length() > 50 ? result.substring(0, 50) + "..." : result);

        return result;
    }
}