package com.example.antaliya_taxi_service.service.impl;

import com.example.antaliya_taxi_service.model.Booking;
import com.example.antaliya_taxi_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {



    private String generateBookingReference() {
        String datePrefix = LocalDate.now().format(
                DateTimeFormatter.ofPattern("yyMMdd"));
        String randomSuffix = String.format("%04d",
                (int)(Math.random() * 10000));
        return "AT" + datePrefix + randomSuffix;
    }

}
