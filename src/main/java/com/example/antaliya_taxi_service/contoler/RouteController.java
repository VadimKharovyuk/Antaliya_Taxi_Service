package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.service.CurrencyService;
import com.example.antaliya_taxi_service.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;
    private final CurrencyService currencyService;

    /**
     * Главная страница с формой поиска
     */
    @GetMapping
    public String showSearchForm(Model model) {
        model.addAttribute("searchDto", new RouteDto.Search());
        model.addAttribute("currencies", Currency.values());
        return "routes/search";
    }

    /**
     * Поиск маршрутов по критериям
     */
    @PostMapping("/search")
    public String searchRoutes(
            @ModelAttribute("searchDto") RouteDto.Search searchDto,
            @RequestParam(required = false) Currency displayCurrency,
            Model model) {

        List<RouteDto.SearchResult> results;
        if (displayCurrency != null) {
            results = routeService.searchActiveRoutes(searchDto, displayCurrency);
            model.addAttribute("displayCurrency", displayCurrency);
        } else {
            results = routeService.searchActiveRoutes(searchDto);
        }

        model.addAttribute("searchResults", results);
        model.addAttribute("currencies", Currency.values());
        model.addAttribute("searchDto", searchDto);

        return "routes/search-results";
    }

    /**
     * Просмотр деталей маршрута
     */
    @GetMapping("/{id}")
    public String viewRoute(
            @PathVariable Long id,
            @RequestParam(required = false) Currency displayCurrency,
            Model model) {

        RouteDto.Response route;
        if (displayCurrency != null) {
            route = routeService.getRouteById(id, displayCurrency);
            model.addAttribute("displayCurrency", displayCurrency);
        } else {
            route = routeService.getRouteById(id);
        }

        // Проверка, что маршрут активен
        if (!route.isActive()) {
            return "redirect:/routes?error=routeNotActive";
        }

        model.addAttribute("route", route);
        model.addAttribute("currencies", Currency.values());

        // Добавляем конвертацию цен в разные валюты для отображения
        if (displayCurrency == null) {
            // Если валюта не выбрана, подготовим цены во всех валютах
            model.addAttribute("priceUSD",
                    currencyService.convert(route.getBasePrice(), route.getCurrency(), Currency.USD));
            model.addAttribute("priceEUR",
                    currencyService.convert(route.getBasePrice(), route.getCurrency(), Currency.EUR));
            model.addAttribute("priceTRY",
                    currencyService.convert(route.getBasePrice(), route.getCurrency(), Currency.TRY));
            model.addAttribute("priceRUB",
                    currencyService.convert(route.getBasePrice(), route.getCurrency(), Currency.RUB));
        }

        return "routes/view";
    }

    /**
     * Страница для сравнения маршрутов
     */
    @GetMapping("/compare")
    public String compareRoutes(
            @RequestParam List<Long> ids,
            @RequestParam(required = false) Currency displayCurrency,
            Model model) {

        List<RouteDto.Response> routes = ids.stream()
                .map(id -> {
                    try {
                        RouteDto.Response route;
                        if (displayCurrency != null) {
                            route = routeService.getRouteById(id, displayCurrency);
                        } else {
                            route = routeService.getRouteById(id);
                        }
                        // Проверка, что маршрут активен
                        if (route.isActive()) {
                            return route;
                        }
                        return null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(route -> route != null)
                .toList();

        model.addAttribute("routes", routes);
        model.addAttribute("currencies", Currency.values());
        if (displayCurrency != null) {
            model.addAttribute("displayCurrency", displayCurrency);
        }

        return "routes/compare";
    }

    /**
     * Получение всех доступных мест отправления для автозаполнения
     */
    @GetMapping("/pickup-locations")
    public String getPickupLocations(Model model) {
        List<String> pickupLocations = routeService.getAllActiveRoutes().stream()
                .map(RouteDto.Response::getPickupLocation)
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("locations", pickupLocations);
        return "routes/pickup-locations";
    }

    /**
     * Получение всех доступных мест назначения для автозаполнения
     */
    @GetMapping("/dropoff-locations")
    public String getDropoffLocations(
            @RequestParam(required = false) String pickupLocation,
            Model model) {

        List<String> dropoffLocations;
        if (pickupLocation != null && !pickupLocation.isEmpty()) {
            // Если указано место отправления, показываем только подходящие места назначения
            dropoffLocations = routeService.getAllActiveRoutes().stream()
                    .filter(route -> route.getPickupLocation().equals(pickupLocation))
                    .map(RouteDto.Response::getDropoffLocation)
                    .distinct()
                    .sorted()
                    .toList();
        } else {
            // Иначе показываем все доступные места назначения
            dropoffLocations = routeService.getAllActiveRoutes().stream()
                    .map(RouteDto.Response::getDropoffLocation)
                    .distinct()
                    .sorted()
                    .toList();
        }

        model.addAttribute("locations", dropoffLocations);
        return "routes/dropoff-locations";
    }
}