package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.service.CurrencyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
@Primary
public class CurrencyServiceImplApi implements CurrencyService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyServiceImplApi.class);
    private static final String FALLBACK_MESSAGE = "Используем сохраненные обменные курсы из-за ошибки API";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private Map<String, BigDecimal> exchangeRates = new ConcurrentHashMap<>();
    @Getter
    private LocalDateTime lastUpdated;

    @Value("${currency.api.url:https://open.er-api.com/v6/latest/USD}")
    private String apiUrl;

    @Value("${currency.api.key:}")
    private String apiKey;

    @PostConstruct
    public void init() {
        log.info("Используемый URL API: {}", apiUrl);
        log.info("API ключ установлен: {}", !apiKey.isEmpty());
        updateExchangeRates();
    }

    @Override
    @Cacheable(value = "currencyConversions", key = "{#amount, #fromCurrency, #toCurrency}")
    public BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
        log.debug("Cache miss - выполняем конвертацию {} {} в {}", amount, fromCurrency, toCurrency);

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
            BigDecimal amountInUSD = convertWithoutCache(amount, fromCurrency, Currency.USD);
            return convertWithoutCache(amountInUSD, Currency.USD, toCurrency);
        }

        throw new IllegalArgumentException(
                "Не удалось конвертировать из " + fromCurrency + " в " + toCurrency);
    }

    // Вспомогательный метод для избежания кэширования промежуточных конвертаций
    private BigDecimal convertWithoutCache(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
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

        throw new IllegalArgumentException(
                "Не удалось конвертировать из " + fromCurrency + " в " + toCurrency);
    }

    @Override
    @Cacheable(value = "exchangeRates", key = "{#fromCurrency, #toCurrency}")
    public BigDecimal getExchangeRate(Currency fromCurrency, Currency toCurrency) {
        log.debug("Cache miss - получаем курс из {} в {}", fromCurrency, toCurrency);

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
            BigDecimal rateFromToUSD = getExchangeRateWithoutCache(fromCurrency, Currency.USD);
            BigDecimal rateFromUSDToTarget = getExchangeRateWithoutCache(Currency.USD, toCurrency);
            return rateFromToUSD.multiply(rateFromUSDToTarget).setScale(6, RoundingMode.HALF_UP);
        }

        throw new IllegalArgumentException(
                "Курс обмена не найден для пары " + fromCurrency + " -> " + toCurrency);
    }

    // Вспомогательный метод для избежания кэширования промежуточных результатов
    private BigDecimal getExchangeRateWithoutCache(Currency fromCurrency, Currency toCurrency) {
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

        throw new IllegalArgumentException(
                "Курс обмена не найден для пары " + fromCurrency + " -> " + toCurrency);
    }

    @Override
    @Cacheable(value = "allExchangeRates")
    public Map<String, BigDecimal> getAllExchangeRates() {
        log.debug("Cache miss - получаем все обменные курсы");
        return new HashMap<>(exchangeRates);
    }

    @Override
    @Scheduled(cron = "0 0 */6 * * *") // Обновление каждые 6 часов
    @CacheEvict(value = {"currencyConversions", "exchangeRates", "allExchangeRates"}, allEntries = true)
    public void updateExchangeRates() {
        try {
            log.info("Обновление обменных курсов...");

            // Используем Open Exchange Rates API (можно заменить на другой)
            String url = apiUrl;
            if (!apiKey.isEmpty()) {
                url += (url.contains("?") ? "&" : "?") + "app_id=" + apiKey;
            }

            log.debug("Запрос к API: {}", url.replaceAll("app_id=[^&]+", "app_id=***"));

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            // Проверка наличия необходимых данных
            if (root.has("rates")) {
                updateRatesFromApi(root);
                lastUpdated = LocalDateTime.now();
                log.info("Обменные курсы успешно обновлены в {}", lastUpdated);
                log.info("Кэши очищены");
            } else {
                log.error("Неверный формат ответа API: отсутствует ключ 'rates'");
                setFallbackRates();
            }
        } catch (RestClientException e) {
            log.error("Ошибка при обращении к API обменных курсов: {}", e.getMessage());
            setFallbackRates();
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обновлении обменных курсов: {}", e.getMessage());
            setFallbackRates();
        }
    }

    private void updateRatesFromApi(JsonNode root) {
        Map<String, BigDecimal> newRates = new ConcurrentHashMap<>();

        // Базовая валюта в ответе API (обычно USD)
        String baseCurrency = root.path("base_code").asText("USD");
        Currency baseCurrencyEnum;
        try {
            baseCurrencyEnum = Currency.valueOf(baseCurrency);
        } catch (IllegalArgumentException e) {
            log.warn("Базовая валюта '{}' не поддерживается, используем USD", baseCurrency);
            baseCurrencyEnum = Currency.USD;
        }

        JsonNode rates = root.path("rates");

        // Логируем полученные курсы для дебага
        log.debug("Полученные курсы: {}", rates);

        // Создаем карту курсов валют относительно базовой валюты
        Map<Currency, BigDecimal> ratesMap = new HashMap<>();

        for (Currency currency : Currency.values()) {
            if (rates.has(currency.name())) {
                BigDecimal rate = new BigDecimal(rates.get(currency.name()).asText());
                ratesMap.put(currency, rate);

                // Добавляем прямой курс от базовой валюты
                if (currency != baseCurrencyEnum) {
                    String key = baseCurrencyEnum.name() + "_" + currency.name();
                    newRates.put(key, rate);

                    // Добавляем обратный курс к базовой валюте
                    String inverseKey = currency.name() + "_" + baseCurrencyEnum.name();
                    newRates.put(inverseKey, BigDecimal.ONE.divide(rate, 6, RoundingMode.HALF_UP));
                }
            }
        }

        // Вычисляем кросс-курсы между всеми валютами
        for (Currency from : Currency.values()) {
            if (!ratesMap.containsKey(from)) continue;

            BigDecimal fromRate = ratesMap.get(from);

            for (Currency to : Currency.values()) {
                if (!ratesMap.containsKey(to) || from == to) continue;

                BigDecimal toRate = ratesMap.get(to);
                String key = from.name() + "_" + to.name();

                // Вычисляем кросс-курс
                BigDecimal crossRate = toRate.divide(fromRate, 6, RoundingMode.HALF_UP);
                newRates.put(key, crossRate);
            }
        }

        // Атомарно обновляем карту курсов
        this.exchangeRates = newRates;

        // Логируем количество загруженных курсов
        log.info("Загружено {} обменных курсов", newRates.size());
    }

    private void setFallbackRates() {
        log.warn(FALLBACK_MESSAGE);

        // Если карта пуста (первый запуск), установим базовые курсы
        if (exchangeRates.isEmpty()) {
            // Текущие примерные курсы (май 2025)
            Map<String, BigDecimal> fallbackRates = new ConcurrentHashMap<>();

            // Курсы по отношению к TRY (Турецкая лира)
            fallbackRates.put("TRY_USD", new BigDecimal("0.034")); // 1 TRY = 0.034 USD
            fallbackRates.put("TRY_EUR", new BigDecimal("0.031")); // 1 TRY = 0.031 EUR
            fallbackRates.put("TRY_RUB", new BigDecimal("3.15"));  // 1 TRY = 3.15 RUB

            // Курсы по отношению к USD (Доллар США)
            fallbackRates.put("USD_TRY", new BigDecimal("29.41")); // 1 USD = 29.41 TRY
            fallbackRates.put("USD_EUR", new BigDecimal("0.92"));  // 1 USD = 0.92 EUR
            fallbackRates.put("USD_RUB", new BigDecimal("92.50")); // 1 USD = 92.50 RUB

            // Курсы по отношению к EUR (Евро)
            fallbackRates.put("EUR_TRY", new BigDecimal("32.26")); // 1 EUR = 32.26 TRY
            fallbackRates.put("EUR_USD", new BigDecimal("1.09"));  // 1 EUR = 1.09 USD
            fallbackRates.put("EUR_RUB", new BigDecimal("100.75")); // 1 EUR = 100.75 RUB

            // Курсы по отношению к RUB (Российский рубль)
            fallbackRates.put("RUB_TRY", new BigDecimal("0.32")); // 1 RUB = 0.32 TRY
            fallbackRates.put("RUB_USD", new BigDecimal("0.011")); // 1 RUB = 0.011 USD
            fallbackRates.put("RUB_EUR", new BigDecimal("0.0099")); // 1 RUB = 0.0099 EUR

            // Атомарно устанавливаем запасные курсы
            this.exchangeRates = fallbackRates;

            log.info("Установлено {} запасных обменных курсов", fallbackRates.size());
        }
    }
}