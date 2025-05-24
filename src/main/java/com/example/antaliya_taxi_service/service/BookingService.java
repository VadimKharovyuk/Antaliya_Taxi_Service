package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.Booking.BookingCreateDTO;
import com.example.antaliya_taxi_service.dto.Booking.BookingResponseDTO;

public interface BookingService {


    BookingResponseDTO create(BookingCreateDTO createDTO);
}
