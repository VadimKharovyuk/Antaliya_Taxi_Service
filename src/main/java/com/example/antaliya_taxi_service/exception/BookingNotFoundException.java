package com.example.antaliya_taxi_service.exception;

import lombok.Getter;

@Getter
public class BookingNotFoundException extends RuntimeException {
  private final Long bookingId;
  private final String bookingReference;

  public BookingNotFoundException(Long bookingId) {
    super("Бронирование не найдено с ID: " + bookingId);
    this.bookingId = bookingId;
    this.bookingReference = null;
  }

  public BookingNotFoundException(String bookingReference) {
    super("Бронирование не найдено с номером: " + bookingReference);
    this.bookingId = null;
    this.bookingReference = bookingReference;
  }

  public BookingNotFoundException(String message, Throwable cause) {
    super(message, cause);
    this.bookingId = null;
    this.bookingReference = null;
  }

  public Long getBookingId() {
    return bookingId;
  }

  public String getBookingReference() {
    return bookingReference;
  }
}
