package com.example.antaliya_taxi_service.enums;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public enum VehicleClass {
    ECONOMY(new BigDecimal("1.0"), "Эконом"),        // без наценки
    COMFORT(new BigDecimal("1.3"), "Комфорт"),       // +30%
    BUSINESS(new BigDecimal("1.6"), "Бизнес"),       // +60%
    PREMIUM(new BigDecimal("1.8"), "Премиум"),       // +80% - ДОБАВЛЕНО
    VIP(new BigDecimal("2.0"), "VIP"),               // +100%
    LUXURY(new BigDecimal("2.5"), "Люкс");
    /**
     * Получить множитель цены для данного класса
     */
    private final BigDecimal priceMultiplier;

    /**
     * Получить отображаемое название
     */
    private final String displayName;

    VehicleClass(BigDecimal priceMultiplier, String displayName) {
        this.priceMultiplier = priceMultiplier;
        this.displayName = displayName;
    }

    /**
     * Получить множитель по умолчанию, если класс не указан
     */
    public static BigDecimal getDefaultMultiplier() {
        return BigDecimal.ONE;
    }

    /**
     * Получить класс автомобиля по умолчанию
     */
    public static VehicleClass getDefault() {
        return COMFORT;
    }

    /**
     * Проверить, является ли класс премиальным
     */
    public boolean isPremium() {
        return this == VIP || this == LUXURY || this == BUSINESS;
    }

    /**
     * Получить все доступные классы, отсортированные по цене
     */
    public static List<VehicleClass> getAllSortedByPrice() {
        return Arrays.stream(values())
                .sorted(Comparator.comparing(VehicleClass::getPriceMultiplier))
                .collect(Collectors.toList());
    }

    /**
     * Найти класс по названию (безопасно)
     */
    public static VehicleClass fromString(String className) {
        if (className == null || className.trim().isEmpty()) {
            return getDefault();
        }

        try {
            return VehicleClass.valueOf(className.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Логируем ошибку и возвращаем класс по умолчанию
            System.err.println("Неизвестный класс автомобиля: " + className + ". Используется " + getDefault());
            return getDefault();
        }
    }
}