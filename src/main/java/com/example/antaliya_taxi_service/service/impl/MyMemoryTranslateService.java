package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.service.TranslationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyMemoryTranslateService implements TranslationService {

    private static final String TRANSLATE_URL = "https://api.mymemory.translated.net/get";
    private static final int MAX_TEXT_LENGTH = 500; // Оставляем запас для других параметров

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

//    @PostConstruct
//    public void init() {
//        try {
//            String testText = "Hello";
//            String testResult = performTestTranslation(testText, "ru", "en");
//
//            if (testResult != null && !testResult.equalsIgnoreCase(testText)) {
//                log.info("MyMemory Translation API успешно инициализирован. Тест: '{}' -> '{}'",
//                        testText, testResult);
//            } else {
//                log.warn("MyMemory Translation API инициализирован, но тестовый перевод не сработал");
//            }
//
//        } catch (Exception e) {
//            log.error("Ошибка инициализации MyMemory Translation API: {}", e.getMessage());
//        }
//    }

    @Override
    public String translateText(String text, String targetLanguage) {
        // Автоопределение исходного языка - используем английский как базовый
        String sourceLanguage = detectSourceLanguage(text, targetLanguage);
        return translateText(text, targetLanguage, sourceLanguage);
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

        // Если исходный и целевой язык одинаковые, возвращаем оригинал
        if (sourceLanguage != null && sourceLanguage.equals(targetLanguage)) {
            log.debug("Исходный и целевой язык одинаковые ({}), возвращаем оригинал", targetLanguage);
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
     * Определяет исходный язык на основе целевого
     */
    private String detectSourceLanguage(String text, String targetLanguage) {
        // Простая эвристика для определения языка
        if (containsCyrillic(text)) {
            return "ru";
        } else if (containsTurkish(text)) {
            return "tr";
        } else {
            // По умолчанию считаем английским, если целевой не английский
            return targetLanguage.equals("en") ? "ru" : "en";
        }
    }

    /**
     * Проверяет наличие кириллических символов
     */
    private boolean containsCyrillic(String text) {
        return text.matches(".*[а-яё].*");
    }

    /**
     * Проверяет наличие турецких символов
     */
    private boolean containsTurkish(String text) {
        return text.matches(".*[çğıöşüÇĞIİÖŞÜ].*");
    }

    /**
     * Переводит длинный текст по частям
     */
    private String translateLongText(String text, String targetLanguage, String sourceLanguage) {
        log.debug("Переводим длинный текст по частям. Длина: {} символов", text.length());

        List<String> sentences = splitIntoSentences(text);
        StringBuilder result = new StringBuilder();

        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            // Если добавление предложения превысит лимит, переводим текущий chunk
            if (currentChunk.length() + sentence.length() > MAX_TEXT_LENGTH && currentChunk.length() > 0) {
                String translatedChunk = performTranslation(currentChunk.toString().trim(), targetLanguage, sourceLanguage);
                result.append(translatedChunk).append(" ");
                currentChunk.setLength(0);
            }

            currentChunk.append(sentence).append(" ");
        }

        // Переводим последний chunk
        if (currentChunk.length() > 0) {
            String translatedChunk = performTranslation(currentChunk.toString().trim(), targetLanguage, sourceLanguage);
            result.append(translatedChunk);
        }

        return result.toString().trim();
    }

    /**
     * Разбивает текст на предложения
     */
    private List<String> splitIntoSentences(String text) {
        // Простое разбиение по точкам, восклицательным и вопросительным знакам
        String[] sentences = text.split("(?<=[.!?])\\s+");
        return Arrays.asList(sentences);
    }

    /**
     * Выполняет перевод одного фрагмента текста
     */
    private String performTranslation(String text, String targetLanguage, String sourceLanguage) {
        try {
            // Определяем исходный язык, если не указан
            if (sourceLanguage == null) {
                sourceLanguage = detectSourceLanguage(text, targetLanguage);
            }

            // Если языки одинаковые, возвращаем оригинал
            if (sourceLanguage.equals(targetLanguage)) {
                return text;
            }

            String langPair = sourceLanguage + "|" + targetLanguage;

            String url = UriComponentsBuilder.fromHttpUrl(TRANSLATE_URL)
                    .queryParam("q", text)
                    .queryParam("langpair", langPair)
                    .build()
                    .toUriString();

            log.debug("Отправляем запрос на перевод: {} -> {}, длина текста: {}",
                    sourceLanguage, targetLanguage, text.length());

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                if (jsonNode.has("responseData")) {
                    JsonNode responseData = jsonNode.get("responseData");
                    if (responseData.has("translatedText")) {
                        String translatedText = responseData.get("translatedText").asText();
                        log.debug("Успешно переведено: '{}' -> '{}'",
                                text.substring(0, Math.min(50, text.length())),
                                translatedText.substring(0, Math.min(50, translatedText.length())));
                        return translatedText;
                    }
                }

                // Проверяем наличие ошибок
                if (jsonNode.has("responseStatus") && jsonNode.get("responseStatus").asInt() != 200) {
                    String errorDetails = jsonNode.has("responseDetails") ?
                            jsonNode.get("responseDetails").asText() : "Unknown error";
                    log.error("Ошибка MyMemory API: {}", errorDetails);
                }
            }

            log.error("Ошибка перевода, ответ API: {}", response.getBody());
            return text;

        } catch (Exception e) {
            log.error("Ошибка при переводе текста '{}': {}",
                    text.substring(0, Math.min(50, text.length())), e.getMessage());
            return text;
        }
    }

    private String performTestTranslation(String text, String targetLang, String sourceLang) {
        try {
            String langPair = sourceLang + "|" + targetLang;

            String url = UriComponentsBuilder.fromHttpUrl(TRANSLATE_URL)
                    .queryParam("q", text)
                    .queryParam("langpair", langPair)
                    .build()
                    .toUriString();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                if (jsonNode.has("responseData")) {
                    JsonNode responseData = jsonNode.get("responseData");
                    if (responseData.has("translatedText")) {
                        return responseData.get("translatedText").asText();
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