package com.example.antaliya_taxi_service.enums;



import lombok.Getter;

//@Getter
//public enum BookingStatus {
//    PENDING("Ожидает подтверждения"),
//    CONFIRMED("Подтверждено"),
//    IN_PROGRESS("В процессе"),
//    COMPLETED("Завершено"),
//    CANCELLED("Отменено"),
//    REJECTED("Отклонено");
//
//    private final String displayName;
//
//    BookingStatus(String displayName) {
//        this.displayName = displayName;
//    }
//}


import lombok.Getter;
import java.util.Arrays;
import java.util.List;

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

    /**
     * Получить список активных статусов (которые блокируют автомобиль)
     */
    public static List<BookingStatus> getActiveStatuses() {
        return Arrays.asList(PENDING, CONFIRMED, IN_PROGRESS);
    }

    /**
     * Получить список статусов, которые можно отменить
     */
    public static List<BookingStatus> getCancellableStatuses() {
        return Arrays.asList(PENDING, CONFIRMED);
    }

    /**
     * Получить список статусов, которые можно редактировать
     */
    public static List<BookingStatus> getEditableStatuses() {
        return Arrays.asList(PENDING, CONFIRMED);
    }

    /**
     * Получить список завершенных статусов
     */
    public static List<BookingStatus> getCompletedStatuses() {
        return Arrays.asList(COMPLETED, CANCELLED, REJECTED);
    }

    /**
     * Проверить, является ли статус активным
     */
    public boolean isActive() {
        return getActiveStatuses().contains(this);
    }

    /**
     * Проверить, можно ли отменить бронирование с данным статусом
     */
    public boolean isCancellable() {
        return getCancellableStatuses().contains(this);
    }

    /**
     * Проверить, можно ли редактировать бронирование с данным статусом
     */
    public boolean isEditable() {
        return getEditableStatuses().contains(this);
    }

    /**
     * Проверить, является ли статус завершенным
     */
    public boolean isCompleted() {
        return getCompletedStatuses().contains(this);
    }
}