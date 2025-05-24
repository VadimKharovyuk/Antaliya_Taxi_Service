package com.example.antaliya_taxi_service.exception;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class VehicleNotAvailableException extends RuntimeException {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final Long vehicleId;
    private final LocalDateTime requestedDateTime;
    private final String reason;
    private final String vehicleName;

    /**
     * Базовый конструктор с ID автомобиля и временем
     */
    public VehicleNotAvailableException(Long vehicleId, LocalDateTime requestedDateTime) {
        super(String.format("Автомобиль ID: %d недоступен на %s",
                vehicleId, requestedDateTime.format(FORMATTER)));
        this.vehicleId = vehicleId;
        this.requestedDateTime = requestedDateTime;
        this.reason = "UNAVAILABLE";
        this.vehicleName = null;
    }

    /**
     * Конструктор с указанием причины недоступности
     */
    public VehicleNotAvailableException(Long vehicleId, LocalDateTime requestedDateTime, String reason) {
        super(String.format("Автомобиль ID: %d недоступен на %s. Причина: %s",
                vehicleId, requestedDateTime.format(FORMATTER), reason));
        this.vehicleId = vehicleId;
        this.requestedDateTime = requestedDateTime;
        this.reason = reason;
        this.vehicleName = null;
    }

    /**
     * Конструктор с названием автомобиля
     */
    public VehicleNotAvailableException(Long vehicleId, String vehicleName, LocalDateTime requestedDateTime, String reason) {
        super(String.format("Автомобиль '%s' (ID: %d) недоступен на %s. Причина: %s",
                vehicleName, vehicleId, requestedDateTime.format(FORMATTER), reason));
        this.vehicleId = vehicleId;
        this.requestedDateTime = requestedDateTime;
        this.reason = reason;
        this.vehicleName = vehicleName;
    }

    /**
     * Конструктор для случая, когда автомобиль неактивен
     */
    public static VehicleNotAvailableException inactive(Long vehicleId, String vehicleName) {
        return new VehicleNotAvailableException(vehicleId, vehicleName, null, "Автомобиль неактивен");
    }

    /**
     * Конструктор для случая, когда автомобиль уже забронирован
     */
    public static VehicleNotAvailableException alreadyBooked(Long vehicleId, String vehicleName,
                                                             LocalDateTime requestedDateTime) {
        return new VehicleNotAvailableException(vehicleId, vehicleName, requestedDateTime,
                "Автомобиль уже забронирован на это время");
    }

    /**
     * Конструктор для случая технического обслуживания
     */
    public static VehicleNotAvailableException maintenance(Long vehicleId, String vehicleName,
                                                           LocalDateTime requestedDateTime) {
        return new VehicleNotAvailableException(vehicleId, vehicleName, requestedDateTime,
                "Автомобиль находится на техническом обслуживании");
    }

    /**
     * Конструктор для случая, когда водитель недоступен
     */
    public static VehicleNotAvailableException driverUnavailable(Long vehicleId, String vehicleName,
                                                                 LocalDateTime requestedDateTime) {
        return new VehicleNotAvailableException(vehicleId, vehicleName, requestedDateTime,
                "Водитель недоступен в указанное время");
    }

    /**
     * Получить отформатированную дату
     */
    public String getFormattedDateTime() {
        return requestedDateTime != null ? requestedDateTime.format(FORMATTER) : "Не указано";
    }

    /**
     * Получить детализированное сообщение для пользователя
     */
    public String getUserFriendlyMessage() {
        StringBuilder message = new StringBuilder();

        if (vehicleName != null) {
            message.append("Автомобиль '").append(vehicleName).append("'");
        } else {
            message.append("Выбранный автомобиль");
        }

        message.append(" недоступен");

        if (requestedDateTime != null) {
            message.append(" на ").append(getFormattedDateTime());
        }

        message.append(".");

        // Добавляем причину в удобочитаемом виде
        switch (reason) {
            case "UNAVAILABLE":
                message.append(" Попробуйте выбрать другое время или автомобиль.");
                break;
            case "Автомобиль неактивен":
                message.append(" Автомобиль временно недоступен для бронирования.");
                break;
            case "Автомобиль уже забронирован на это время":
                message.append(" Выберите другое время или другой автомобиль.");
                break;
            case "Автомобиль находится на техническом обслуживании":
                message.append(" Автомобиль проходит техническое обслуживание.");
                break;
            case "Водитель недоступен в указанное время":
                message.append(" Водитель занят в указанное время.");
                break;
            default:
                if (reason != null && !reason.isEmpty()) {
                    message.append(" ").append(reason);
                }
        }

        return message.toString();
    }

    /**
     * Проверить, является ли причина временной (можно попробовать позже)
     */
    public boolean isTemporary() {
        return "Автомобиль уже забронирован на это время".equals(reason) ||
                "Водитель недоступен в указанное время".equals(reason);
    }

    /**
     * Проверить, является ли причина постоянной (нужно выбрать другой автомобиль)
     */
    public boolean isPermanent() {
        return "Автомобиль неактивен".equals(reason) ||
                "Автомобиль находится на техническом обслуживании".equals(reason);
    }
}