package com.example.antaliya_taxi_service.repository;


import com.example.antaliya_taxi_service.enums.BookingStatus;
import com.example.antaliya_taxi_service.model.Booking;
import com.example.antaliya_taxi_service.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByVehicleAndStatus(Vehicle vehicle, BookingStatus status);

}
