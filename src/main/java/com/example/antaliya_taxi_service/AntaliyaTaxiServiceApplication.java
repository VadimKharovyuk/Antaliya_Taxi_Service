package com.example.antaliya_taxi_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AntaliyaTaxiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AntaliyaTaxiServiceApplication.class, args);
    }




    ///add dto  tranclate fot rout /tour

    ///add валидацию на созданию блога + туров привязка к переводу api

    /// сделать dto для бронирования такси

  /// public TourDto updateTour(TourUpdateDto tourUpdateDto) {
  ///         return null;
  ///     }



}
