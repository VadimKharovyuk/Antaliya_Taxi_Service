package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.service.CurrencyService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@Primary
public class CurrencyServiceImpl implements CurrencyService {

    private Map<String, BigDecimal> exchangeRates = new HashMap<>();

    @PostConstruct
    public void init() {
        // Инициализация базовых курсов обмена
        // В реальном приложении эти данные могут загружаться из внешнего API или базы данных
        updateExchangeRates();
    }

    @Override
    public BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency == toCurrency) {
            return amount;
        }

        String rateKey = fromCurrency.name() + "_" + toCurrency.name();
        if (exchangeRates.containsKey(rateKey)) {
            return amount.multiply(exchangeRates.get(rateKey))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Если прямого курса нет, пробуем найти обратный и инвертировать его
        String inverseRateKey = toCurrency.name() + "_" + fromCurrency.name();
        if (exchangeRates.containsKey(inverseRateKey)) {
            return amount.divide(exchangeRates.get(inverseRateKey), 2, RoundingMode.HALF_UP);
        }

        // Если нет прямого или обратного курса, пытаемся конвертировать через USD
        if (fromCurrency != Currency.USD && toCurrency != Currency.USD) {
            BigDecimal amountInUSD = convert(amount, fromCurrency, Currency.USD);
            return convert(amountInUSD, Currency.USD, toCurrency);
        }

        throw new IllegalArgumentException(
                "Не удалось конвертировать из " + fromCurrency + " в " + toCurrency);
    }

    @Override
    public BigDecimal getExchangeRate(Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency == toCurrency) {
            return BigDecimal.ONE;
        }

        String rateKey = fromCurrency.name() + "_" + toCurrency.name();
        if (exchangeRates.containsKey(rateKey)) {
            return exchangeRates.get(rateKey);
        }

        // Если прямого курса нет, пробуем найти обратный и инвертировать его
        String inverseRateKey = toCurrency.name() + "_" + fromCurrency.name();
        if (exchangeRates.containsKey(inverseRateKey)) {
            return BigDecimal.ONE.divide(exchangeRates.get(inverseRateKey), 6, RoundingMode.HALF_UP);
        }

        // Если нет прямого или обратного курса, пытаемся найти курс через USD
        if (fromCurrency != Currency.USD && toCurrency != Currency.USD) {
            BigDecimal rateFromToUSD = getExchangeRate(fromCurrency, Currency.USD);
            BigDecimal rateFromUSDToTarget = getExchangeRate(Currency.USD, toCurrency);
            return rateFromToUSD.multiply(rateFromUSDToTarget).setScale(6, RoundingMode.HALF_UP);
        }

        throw new IllegalArgumentException(
                "Курс обмена не найден для пары " + fromCurrency + " -> " + toCurrency);
    }

    @Override
    public Map<String, BigDecimal> getAllExchangeRates() {
        return new HashMap<>(exchangeRates);
    }

    @Override
    public void updateExchangeRates() {
        // В реальном приложении здесь был бы вызов внешнего API или загрузка из базы данных
        // Примерные курсы для демонстрации
        exchangeRates.clear();

        // Курсы по отношению к TRY (Турецкая лира)
        exchangeRates.put("TRY_USD", new BigDecimal("0.034")); // 1 TRY = 0.034 USD
        exchangeRates.put("TRY_EUR", new BigDecimal("0.031")); // 1 TRY = 0.031 EUR
        exchangeRates.put("TRY_RUB", new BigDecimal("3.15"));  // 1 TRY = 3.15 RUB

        // Курсы по отношению к USD (Доллар США)
        exchangeRates.put("USD_TRY", new BigDecimal("29.41")); // 1 USD = 29.41 TRY
        exchangeRates.put("USD_EUR", new BigDecimal("0.92"));  // 1 USD = 0.92 EUR
        exchangeRates.put("USD_RUB", new BigDecimal("92.50")); // 1 USD = 92.50 RUB

        // Курсы по отношению к EUR (Евро)
        exchangeRates.put("EUR_TRY", new BigDecimal("32.26")); // 1 EUR = 32.26 TRY
        exchangeRates.put("EUR_USD", new BigDecimal("1.09"));  // 1 EUR = 1.09 USD
        exchangeRates.put("EUR_RUB", new BigDecimal("100.75")); // 1 EUR = 100.75 RUB

        // Курсы по отношению к RUB (Российский рубль)
        exchangeRates.put("RUB_TRY", new BigDecimal("0.32")); // 1 RUB = 0.32 TRY
        exchangeRates.put("RUB_USD", new BigDecimal("0.011")); // 1 RUB = 0.011 USD
        exchangeRates.put("RUB_EUR", new BigDecimal("0.0099")); // 1 RUB = 0.0099 EUR
    }
}