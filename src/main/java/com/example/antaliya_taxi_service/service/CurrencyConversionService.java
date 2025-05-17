package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.model.ExchangeRate;
import com.example.antaliya_taxi_service.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {

    private final ExchangeRateRepository exchangeRateRepository;

    public BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency == toCurrency) {
            return amount;
        }

        // Поиск прямого курса
        Optional<ExchangeRate> directRate = exchangeRateRepository
                .findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);

        if (directRate.isPresent()) {
            return amount.multiply(directRate.get().getRate())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Поиск обратного курса
        Optional<ExchangeRate> inverseRate = exchangeRateRepository
                .findByFromCurrencyAndToCurrency(toCurrency, fromCurrency);

        if (inverseRate.isPresent()) {
            return amount.divide(inverseRate.get().getRate(), 2, RoundingMode.HALF_UP);
        }

        // Конвертация через USD как промежуточную валюту
        if (fromCurrency != Currency.USD && toCurrency != Currency.USD) {
            BigDecimal amountInUSD = convert(amount, fromCurrency, Currency.USD);
            return convert(amountInUSD, Currency.USD, toCurrency);
        }

        throw new IllegalArgumentException(
                "Не удалось конвертировать из " + fromCurrency + " в " + toCurrency);
    }

    // Дополнительные методы для обновления курсов и т.д.
}