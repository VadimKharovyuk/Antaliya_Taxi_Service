package com.example.antaliya_taxi_service.controller;

import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomePageController {

    private final RouteService routeService;

    @GetMapping
    public String homePage(Model model) {
        List<String> pickupLocations = routeService.getAllActivePickupLocations();

        model.addAttribute("pickupLocations", pickupLocations);
        model.addAttribute("defaultCurrency", Currency.TRY);

        return "homeV1";  // Возвращаем имя вашего шаблона
    }
}