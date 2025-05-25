package com.example.antaliya_taxi_service.service;

import com.example.antaliya_taxi_service.dto.Booking.BookingCreateDTO;
import com.example.antaliya_taxi_service.dto.Booking.BookingResponseDTO;
import com.example.antaliya_taxi_service.dto.Booking.TourBookingCreateDTO;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService {

    BookingResponseDTO createTourBooking(TourBookingCreateDTO tourBookingDTO);


    BookingResponseDTO findByReference(String bookingReference);


    BigDecimal calculateTourPrice(TourBookingCreateDTO tourBookingDTO);


    BookingResponseDTO findByReferenceAndEmail(String bookingReference, String customerEmail);

}
