package com.example.antaliya_taxi_service.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

//
//@Getter
//public enum TripType {
//    ONE_WAY("One Way"),
//    ROUND_TRIP("Round Trip");
//
//    private final String displayName;
//
//    TripType(String displayName) {
//        this.displayName = displayName;
//    }
//
//}
@Getter
public enum TripType {
    ONE_WAY("Одна сторона", "Поездка в одну сторону"),
    ROUND_TRIP("Туда-обратно", "Поездка туда и обратно"),
    TOUR("Тур", "Экскурсионный тур"),
    TRANSFER("Трансфер", "Трансфер до места назначения"),
    AIRPORT_PICKUP("Встреча в аэропорту", "Встреча в аэропорту"),
    AIRPORT_DROPOFF("Доставка в аэропорт", "Доставка в аэропорт"),
    HOURLY("Почасовая аренда", "Аренда автомобиля с водителем по часам");

    private final String displayName;
    private final String description;

    TripType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Требует ли данный тип поездки обратный трансфер по умолчанию
     */
    public boolean requiresReturnByDefault() {
        return this == ROUND_TRIP;
    }

    /**
     * Является ли поездка туром
     */
    public boolean isTour() {
        return this == TOUR;
    }

    /**
     * Является ли поездка трансфером
     */
    public boolean isTransfer() {
        return this == TRANSFER || this == AIRPORT_PICKUP || this == AIRPORT_DROPOFF;
    }

    /**
     * Получить подходящие типы для туров
     */
    public static List<TripType> getTourTypes() {
        return Arrays.asList(TOUR, ONE_WAY, ROUND_TRIP);
    }

    /**
     * Получить подходящие типы для трансферов
     */
    public static List<TripType> getTransferTypes() {
        return Arrays.asList(TRANSFER, AIRPORT_PICKUP, AIRPORT_DROPOFF, ONE_WAY, ROUND_TRIP, HOURLY);
    }
}