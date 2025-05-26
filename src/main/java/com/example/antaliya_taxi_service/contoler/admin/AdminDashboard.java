package com.example.antaliya_taxi_service.contoler.admin;

import com.example.antaliya_taxi_service.service.BookingService;
import com.example.antaliya_taxi_service.service.PhotoService;
import com.example.antaliya_taxi_service.service.TourService;
import com.example.antaliya_taxi_service.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminDashboard {
    private final VehicleService vehicleService;
    private final TourService tourService;
    private final PhotoService photoService;
    private final BookingService bookingService;


    @GetMapping
    public String adminDashboard(Model model) {

        model.addAttribute("tourCount", tourService.getActiveToursCount());
        model.addAttribute("vehicleCount", vehicleService.getActiveVehiclesCount());
        model.addAttribute("photoCount", photoService.getTotalPhotosCount());
        model.addAttribute("newBookingCount", bookingService.getNewBookingsCount());
        return "admin/admin/dashboard";
    }
}
