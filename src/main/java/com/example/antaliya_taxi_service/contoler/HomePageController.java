package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomePageController {

    private final RouteService routeService;

    @GetMapping
    public String homePage(Model model ,
                           @RequestParam(required = false) Currency displayCurrency
                           ) {


        // Получаем маршруты для главной страницы
        List<RouteDto.DestinationCard> popularRoutes = routeService.getPopularRoutes(displayCurrency);
        model.addAttribute("popularRoutes", popularRoutes);

        // Получаем список всех доступных мест отправления
        List<String> pickupLocations = routeService.getAllActivePickupLocations();
        model.addAttribute("pickupLocations", pickupLocations);

        // Получаем список всех мест назначения (первоначально все доступные)
        List<String> dropoffLocations = routeService.getAllActiveDropoffLocations();

        // Добавляем данные в модель
        model.addAttribute("dropoffLocations", dropoffLocations);
        model.addAttribute("searchDto", new RouteDto.Search());

        return "homeV1";
    }
}