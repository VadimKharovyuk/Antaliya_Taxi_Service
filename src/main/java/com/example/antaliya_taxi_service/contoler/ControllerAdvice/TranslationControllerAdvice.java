package com.example.antaliya_taxi_service.contoler.ControllerAdvice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ControllerAdvice
public class TranslationControllerAdvice {

    /**
     * Добавляет информацию о текущем языке на все страницы
     */
    @ModelAttribute("currentLang")
    public String getCurrentLanguage(HttpServletRequest request) {
        String lang = request.getParameter("lang");
        return lang != null && isValidLanguage(lang) ? lang : "original";
    }

    /**
     * Добавляет флаг наличия перевода на все страницы
     */
    @ModelAttribute("isTranslated")
    public boolean getIsTranslated(HttpServletRequest request) {
        String lang = request.getParameter("lang");
        return lang != null && isValidLanguage(lang);
    }

    /**
     * Добавляет список поддерживаемых языков на все страницы
     */
    @ModelAttribute("supportedLanguages")
    public List<Language> getSupportedLanguages() {
        return Arrays.asList(
                new Language("original", "Оригинал", "bi-globe", false),
                new Language("ru", "Русский", "bi-flag", true),
                new Language("tr", "Türkçe", "bi-flag", true),
                new Language("en", "English", "bi-flag", true)
        );
    }

    /**
     * Добавляет базовый URL без параметра lang
     */
    @ModelAttribute("baseUrl")
    public String getBaseUrl(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString != null) {
            // Удаляем параметр lang из query string
            String cleanQuery = Arrays.stream(queryString.split("&"))
                    .filter(param -> !param.startsWith("lang="))
                    .reduce((p1, p2) -> p1 + "&" + p2)
                    .orElse("");

            return requestURI + (cleanQuery.isEmpty() ? "" : "?" + cleanQuery);
        }

        return requestURI;
    }

    /**
     * Добавляет утилитарные методы для переводов
     */
    @ModelAttribute("translationUtils")
    public TranslationUtils getTranslationUtils() {
        return new TranslationUtils();
    }

    /**
     * Проверяет валидность языка
     */
    private boolean isValidLanguage(String lang) {
        return lang != null && (lang.equals("ru") || lang.equals("tr") || lang.equals("en"));
    }

    /**
     * Класс для представления языка
     */
    @Getter
    @AllArgsConstructor
    public static class Language {
        private final String code;
        private final String name;
        private final String icon;
        private final boolean requiresTranslation;

    }

    /**
     * Утилитарный класс для работы с переводами в шаблонах
     */
    public static class TranslationUtils {

        /**
         * Строит URL с добавлением/изменением параметра lang
         */
        public String buildUrlWithLang(String baseUrl, String lang) {
            if ("original".equals(lang)) {
                return baseUrl;
            }

            String separator = baseUrl.contains("?") ? "&" : "?";
            return baseUrl + separator + "lang=" + lang;
        }

        /**
         * Проверяет, является ли язык активным
         */
        public boolean isActiveLanguage(String currentLang, String checkLang) {
            return currentLang.equals(checkLang);
        }

        /**
         * Возвращает CSS класс для кнопки языка
         */
        public String getLanguageButtonClass(String currentLang, String buttonLang) {
            boolean isActive = isActiveLanguage(currentLang, buttonLang);
            return isActive ? "lang-btn active" : "lang-btn";
        }
    }
}