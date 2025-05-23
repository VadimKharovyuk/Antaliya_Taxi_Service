package com.example.antaliya_taxi_service.service.impl;
import com.example.antaliya_taxi_service.service.TranslationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class GoogleTranslateService implements TranslationService {

    @Value("${google.translate.api.key}")
    private String apiKey;

    private static final String TRANSLATE_URL = "https://translation.googleapis.com/language/translate/v2";
    private static final int MAX_TEXT_LENGTH = 25000; // Google позволяет до 30k символов

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        try {
            String testText = "Hello world";
            String testResult = performTestTranslation(testText, "ru");

            if (testResult != null && !testResult.equalsIgnoreCase(testText)) {
                log.info("Google Translate API успешно инициализирован. Тест: '{}' -> '{}'",
                        testText, testResult);
            } else {
                log.warn("Google Translate API инициализирован, но тестовый перевод не сработал");
            }

        } catch (Exception e) {
            log.error("Ошибка инициализации Google Translate API: {}", e.getMessage());
        }
    }

    @Override
    public String translateText(String text, String targetLanguage) {
        return translateText(text, targetLanguage, null);
    }

    @Override
    public String translateText(String text, String targetLanguage, String sourceLanguage) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        if (!isLanguageSupported(targetLanguage)) {
            log.warn("Неподдерживаемый язык: {}", targetLanguage);
            return text;
        }

        // Если текст слишком длинный, разбиваем на части
        if (text.length() > MAX_TEXT_LENGTH) {
            return translateLongText(text, targetLanguage, sourceLanguage);
        }

        return performTranslation(text, targetLanguage, sourceLanguage);
    }

    @Override
    public boolean isLanguageSupported(String language) {
        return language != null && (
                language.equals("ru") ||  // русский
                        language.equals("tr") ||  // турецкий
                        language.equals("en")     // английский
        );
    }

    /**
     * Переводит длинный текст по частям
     */
    private String translateLongText(String text, String targetLanguage, String sourceLanguage) {
        log.debug("Переводим длинный текст по частям. Длина: {} символов", text.length());

        StringBuilder result = new StringBuilder();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + MAX_TEXT_LENGTH, text.length());

            // Пытаемся найти ближайший разрыв предложения
            if (end < text.length()) {
                int lastSentenceEnd = findLastSentenceEnd(text, start, end);
                if (lastSentenceEnd > start) {
                    end = lastSentenceEnd;
                }
            }

            String chunk = text.substring(start, end);
            String translatedChunk = performTranslation(chunk, targetLanguage, sourceLanguage);

            result.append(translatedChunk);
            if (end < text.length()) {
                result.append(" ");
            }

            start = end;
        }

        return result.toString().trim();
    }

    /**
     * Находит последний конец предложения в диапазоне
     */
    private int findLastSentenceEnd(String text, int start, int end) {
        for (int i = end - 1; i > start; i--) {
            char c = text.charAt(i);
            if (c == '.' || c == '!' || c == '?' || c == '\n') {
                return i + 1;
            }
        }
        return end;
    }

    /**
     * Выполняет перевод одного фрагмента текста
     */
    private String performTranslation(String text, String targetLanguage, String sourceLanguage) {
        try {
            // Используем POST запрос для корректной обработки кириллицы
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Формируем тело запроса с правильным кодированием
            String requestBody = "key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8) +
                    "&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                    "&target=" + URLEncoder.encode(targetLanguage, StandardCharsets.UTF_8);

            // Добавляем исходный язык, если указан
            if (sourceLanguage != null && !sourceLanguage.isEmpty()) {
                requestBody += "&source=" + URLEncoder.encode(sourceLanguage, StandardCharsets.UTF_8);
            }

            log.debug("Отправляем запрос на перевод: {} -> {}, длина текста: {}",
                    sourceLanguage != null ? sourceLanguage : "auto", targetLanguage, text.length());

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(TRANSLATE_URL, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                if (jsonNode.has("data") && jsonNode.get("data").has("translations")) {
                    JsonNode translations = jsonNode.get("data").get("translations");
                    if (translations.isArray() && translations.size() > 0) {
                        JsonNode translation = translations.get(0);
                        String translatedText = translation.get("translatedText").asText();

                        // Логируем обнаруженный исходный язык
                        if (translation.has("detectedSourceLanguage")) {
                            String detectedLang = translation.get("detectedSourceLanguage").asText();
                            log.debug("Обнаружен исходный язык: {}", detectedLang);
                        }

                        log.debug("Успешно переведено: '{}' -> '{}'",
                                text.substring(0, Math.min(50, text.length())),
                                translatedText.substring(0, Math.min(50, translatedText.length())));

                        return translatedText;
                    }
                }

                log.error("Неожиданный формат ответа Google Translate API: {}", response.getBody());
            } else {
                log.error("Ошибка Google Translate API. Статус: {}, Ответ: {}",
                        response.getStatusCode(), response.getBody());
            }

            return text;

        } catch (Exception e) {
            log.error("Ошибка при переводе текста '{}': {}",
                    text.substring(0, Math.min(50, text.length())), e.getMessage());
            return text;
        }
    }

    /**
     * Тестовый перевод для проверки работоспособности API
     */
    private String performTestTranslation(String text, String targetLang) {
        try {
            // Используем POST запрос для тестирования
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String requestBody = "key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8) +
                    "&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                    "&target=" + URLEncoder.encode(targetLang, StandardCharsets.UTF_8);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(TRANSLATE_URL, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.has("data") && jsonNode.get("data").has("translations")) {
                    JsonNode translations = jsonNode.get("data").get("translations");
                    if (translations.isArray() && translations.size() > 0) {
                        return translations.get(0).get("translatedText").asText();
                    }
                }
            }

            log.debug("Тестовый ответ API: {}", response.getBody());
            return null;
        } catch (Exception e) {
            log.error("Ошибка тестового перевода: {}", e.getMessage());
            return null;
        }
    }
}
