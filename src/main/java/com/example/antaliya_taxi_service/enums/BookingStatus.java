package com.example.antaliya_taxi_service.enums;



import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDING("Ожидает подтверждения"),
    CONFIRMED("Подтверждено"),
    IN_PROGRESS("В процессе"),
    COMPLETED("Завершено"),
    CANCELLED("Отменено"),
    REJECTED("Отклонено");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }
}