package com.example.antaliya_taxi_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public abstract class BaseTranslationService<T, R> {

    protected final TranslationService translationService;



    /**
     * Абстрактный метод для перевода - каждый сервис реализует свою логику
     */
    public abstract R translate(T entity, String targetLanguage, String sourceLanguage);

    /**
     * Перевод с автоопределением исходного языка
     */
    public R translate(T entity, String targetLanguage) {
        return translate(entity, targetLanguage, null);
    }

    /**
     * Перевод списка сущностей
     */
    public List<R> translateList(List<T> entities, String targetLanguage) {
        return entities.stream()
                .map(entity -> translate(entity, targetLanguage))
                .collect(Collectors.toList());
    }

    /**
     * Перевод списка сущностей с указанием исходного языка
     */
    public List<R> translateList(List<T> entities, String targetLanguage, String sourceLanguage) {
        return entities.stream()
                .map(entity -> translate(entity, targetLanguage, sourceLanguage))
                .collect(Collectors.toList());
    }

    /**
     * Проверка поддерживаемых языков (общая логика)
     */
    protected boolean isLanguageSupported(String language) {
        return translationService.isLanguageSupported(language);
    }

    /**
     * Логирование начала перевода (общая логика)
     */
    protected void logTranslationStart(Object entityId, String targetLanguage) {
        log.info("Начинаем перевод {} на язык {}", entityId, targetLanguage);
    }

    /**
     * Логирование завершения перевода (общая логика)
     */
    protected void logTranslationComplete(Object entityId, String targetLanguage) {
        log.debug("Завершен перевод {} на язык {}", entityId, targetLanguage);
    }
}