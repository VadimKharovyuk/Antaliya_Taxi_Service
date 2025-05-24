package com.example.antaliya_taxi_service.exception;

import lombok.Getter;

import java.util.List;
@Getter
public class BookingValidationException extends RuntimeException {

    private final List<String> validationErrors;
    private final String field;

    public BookingValidationException(String message) {
        super(message);
        this.validationErrors = List.of(message);
        this.field = null;
    }

    public BookingValidationException(String field, String message) {
        super("Ошибка в поле '" + field + "': " + message);
        this.validationErrors = List.of(message);
        this.field = field;
    }

    public BookingValidationException(List<String> validationErrors) {
        super("Ошибки валидации: " + String.join("; ", validationErrors));
        this.validationErrors = validationErrors;
        this.field = null;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public String getField() {
        return field;
    }
}
