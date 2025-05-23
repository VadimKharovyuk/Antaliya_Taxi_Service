package com.example.antaliya_taxi_service.util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class TranslationUsageMonitor {

    private final AtomicLong charactersTranslated = new AtomicLong(0);
    private final AtomicLong requestsCount = new AtomicLong(0);

    public void recordTranslation(String text) {
        if (text != null) {
            long chars = text.length();
            charactersTranslated.addAndGet(chars);
            requestsCount.incrementAndGet();

            // Логируем каждые 100 запросов
            if (requestsCount.get() % 100 == 0) {
                log.info("Статистика переводов: {} запросов, {} символов",
                        requestsCount.get(), charactersTranslated.get());
            }
        }
    }

    public long getCharactersTranslated() {
        return charactersTranslated.get();
    }

    public long getRequestsCount() {
        return requestsCount.get();
    }

    public void logCurrentUsage() {
        long chars = charactersTranslated.get();
        long requests = requestsCount.get();

        log.info("=== Статистика использования переводов ===");
        log.info("Общее количество запросов: {}", requests);
        log.info("Общее количество символов: {}", chars);
        log.info("Оставшийся бесплатный лимит: {} символов", Math.max(0, 500000 - chars));

        if (chars > 400000) { // 80% от лимита
            log.warn("⚠️ Использовано {}% от бесплатного лимита Google Translate!",
                    (chars * 100) / 500000);
        }
    }
}