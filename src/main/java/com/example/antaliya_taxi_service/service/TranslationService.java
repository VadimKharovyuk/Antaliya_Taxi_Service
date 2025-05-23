package com.example.antaliya_taxi_service.service;

public interface TranslationService {
    String translateText(String text, String targetLanguage);
    String translateText(String text, String targetLanguage, String sourceLanguage);
    boolean isLanguageSupported(String language);
}
