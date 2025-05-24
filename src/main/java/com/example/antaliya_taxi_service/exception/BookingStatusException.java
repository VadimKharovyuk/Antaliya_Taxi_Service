package com.example.antaliya_taxi_service.exception;

import com.example.antaliya_taxi_service.enums.BookingStatus;
import lombok.Getter;

@Getter
public class BookingStatusException extends RuntimeException {


        private final Long bookingId;
        private final BookingStatus currentStatus;
        private final BookingStatus requestedStatus;
        private final String operation;

        public BookingStatusException(Long bookingId, BookingStatus currentStatus, String operation) {
            super("Нельзя выполнить операцию '" + operation + "' для бронирования ID: " + bookingId +
                    " в статусе: " + currentStatus);
            this.bookingId = bookingId;
            this.currentStatus = currentStatus;
            this.requestedStatus = null;
            this.operation = operation;
        }

        public BookingStatusException(Long bookingId, BookingStatus currentStatus,
                                      BookingStatus requestedStatus, String operation) {
            super("Нельзя изменить статус бронирования ID: " + bookingId +
                    " с '" + currentStatus + "' на '" + requestedStatus + "' для операции: " + operation);
            this.bookingId = bookingId;
            this.currentStatus = currentStatus;
            this.requestedStatus = requestedStatus;
            this.operation = operation;
        }

        public Long getBookingId() {
            return bookingId;
        }

        public BookingStatus getCurrentStatus() {
            return currentStatus;
        }

        public BookingStatus getRequestedStatus() {
            return requestedStatus;
        }

        public String getOperation() {
            return operation;
        }
}

