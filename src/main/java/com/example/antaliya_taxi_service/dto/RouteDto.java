package com.example.antaliya_taxi_service.dto;

import com.example.antaliya_taxi_service.enums.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class RouteDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String pickupLocation;
        private String dropoffLocation;
        private Integer distance;
        private Integer estimatedTime;
        private BigDecimal basePrice;
        private Currency currency;
        private boolean active;
        private String url;
        private String imageId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Дополнительные поля для отображения цены в другой валюте
        private BigDecimal convertedPrice;
        private Currency displayCurrency;

        private String targetLanguage;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {
        @NotBlank(message = "Место отправления обязательно")
        private String pickupLocation;

        @NotBlank(message = "Место назначения обязательно")
        private String dropoffLocation;

        @PositiveOrZero(message = "Расстояние должно быть положительным числом или нулем")
        private Integer distance;

        @PositiveOrZero(message = "Время в пути должно быть положительным числом или нулем")
        private Integer estimatedTime;

        @NotNull(message = "Базовая цена обязательна")
        @Min(value = 0, message = "Базовая цена должна быть положительным числом")
        private BigDecimal basePrice;

        private Currency currency = Currency.TRY;

        private String url;
        private String imageId;

        private boolean active = true;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update {
        private Long id;

        @NotBlank(message = "Место отправления обязательно")
        private String pickupLocation;

        @NotBlank(message = "Место назначения обязательно")
        private String dropoffLocation;

        @PositiveOrZero(message = "Расстояние должно быть положительным числом или нулем")
        private Integer distance;

        @PositiveOrZero(message = "Время в пути должно быть положительным числом или нулем")
        private Integer estimatedTime;

        @Min(value = 0, message = "Базовая цена должна быть положительным числом")
        private BigDecimal basePrice;

        private Currency currency;

        private String url;
        private String imageId;

        private boolean active;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Search {
        @NotBlank(message = "Место отправления обязательно")
        private String pickupLocation;

        @NotBlank(message = "Место назначения обязательно")
        private String dropoffLocation;

        @Min(value = 1, message = "Количество пассажиров должно быть не менее 1")
        private Integer passengers;

        // Опциональное поле для указания валюты отображения цен
        private Currency displayCurrency;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchResult {
        private Long id;
        private String pickupLocation;
        private String dropoffLocation;
        private Integer distance;
        private Integer estimatedTime;
        private BigDecimal basePrice;
        private Currency currency;
        private String url;
        private String imageId;

        // Поля для отображения цены в запрошенной валюте
        private BigDecimal convertedPrice;
        private Currency displayCurrency;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceConversion {
        private Long routeId;
        private BigDecimal originalPrice;
        private Currency originalCurrency;
        private BigDecimal convertedPrice;
        private Currency targetCurrency;
        private BigDecimal exchangeRate;
        private LocalDateTime conversionTime;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DestinationCard {
        private Long id;
        private String pickupLocation;
        private String dropoffLocation;
        private Integer distance;
        private Integer estimatedTime;
        private BigDecimal price;
        private Currency currency;
        private String url;

        // Вспомогательные методы для улучшения отображения
        public String getFormattedTime() {
            if (estimatedTime == null) return "";

            int hours = estimatedTime / 60;
            int minutes = estimatedTime % 60;

            if (hours > 0) {
                return hours + " ч " + (minutes > 0 ? minutes + " мин" : "");
            } else {
                return minutes + " мин";
            }
        }

        public String getFormattedPrice() {
            return "от " + price + " " + currency;
        }

        // Метод для получения названия маршрута
        public String getRouteName() {
            return pickupLocation + " - " + dropoffLocation;
        }
    }
}