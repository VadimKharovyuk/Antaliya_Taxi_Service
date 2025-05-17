package com.example.antaliya_taxi_service.service;



import com.example.antaliya_taxi_service.enums.Currency;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyService {

    /**
     * Конвертирует сумму из одной валюты в другую
     */
    BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency);

    /**
     * Получает обменный курс между двумя валютами
     */
    BigDecimal getExchangeRate(Currency fromCurrency, Currency toCurrency);

    /**
     * Возвращает все доступные обменные курсы
     */
    Map<String, BigDecimal> getAllExchangeRates();

    /**
     * Обновляет обменные курсы (может быть вызвано по расписанию)
     */
    void updateExchangeRates();
}
