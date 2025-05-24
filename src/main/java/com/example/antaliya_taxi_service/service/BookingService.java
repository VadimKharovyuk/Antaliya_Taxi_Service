package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.Booking.BookingCreateDTO;
import com.example.antaliya_taxi_service.dto.Booking.BookingResponseDTO;

import java.math.BigDecimal;

public interface BookingService {


    BookingResponseDTO create(BookingCreateDTO createDTO);


    BigDecimal calculateEstimatedPrice(BookingCreateDTO bookingDTO);
}
