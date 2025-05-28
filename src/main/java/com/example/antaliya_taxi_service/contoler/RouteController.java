package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.model.Route;
import com.example.antaliya_taxi_service.service.CurrencyService;
import com.example.antaliya_taxi_service.service.RouteService;
import com.example.antaliya_taxi_service.service.RouteTranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Slf4j
@Controller
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;
    private final CurrencyService currencyService;
    private final RouteTranslationService routeTranslationService ;

    /**
     * Просмотр всех маршрутов с пагинацией
     */

    @GetMapping()
    public String getAllRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Currency displayCurrency,
            @RequestParam(required = false) String lang,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RouteDto.Response> routePage = routeService.getAllRoutesWithPagination(pageable, displayCurrency);
        List<RouteDto.Response> translatedRoutes = routePage.getContent();

        // Определяем текущий язык (по умолчанию 'ru' если не указан)
        String currentLang = (lang != null && isValidLanguage(lang)) ? lang : "ru";
        boolean isTranslated = false;

        // Применяем перевод, только если язык валидный и не оригинал
        if (isValidLanguage(lang) && !"ru".equals(lang)) {
            translatedRoutes = translatedRoutes.stream()
                    .map(route -> routeTranslationService.translate(route, lang, "ru"))
                    .toList();
            isTranslated = true;
        }

        model.addAttribute("routes", translatedRoutes);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", routePage.getTotalPages());
        model.addAttribute("totalItems", routePage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        if (displayCurrency != null) {
            model.addAttribute("displayCurrency", displayCurrency);
        }

        model.addAttribute("currencies", Currency.values());
        model.addAttribute("currentLang", currentLang); // всегда передаем язык
        model.addAttribute("isTranslated", isTranslated); // флаг перевода

        return "routes/list";
    }



    @GetMapping("/details/{id}")
    public String viewRoute(
            @PathVariable Long id,
            @RequestParam(required = false) Currency displayCurrency,
            @RequestParam(required = false) String lang,
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

        // Определяем текущий язык (по умолчанию 'ru' если не указан)
        String currentLang = (lang != null && isValidLanguage(lang)) ? lang : "ru";
        boolean isTranslated = false;

        // Применяем перевод, только если язык валидный и не оригинал
        if (isValidLanguage(lang) && !"ru".equals(lang)) {
            route = routeTranslationService.translate(route, lang, "ru");
            isTranslated = true;
        }

        model.addAttribute("route", route);
        model.addAttribute("currencies", Currency.values());
        model.addAttribute("currentLang", currentLang);
        model.addAttribute("isTranslated", isTranslated);

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

    private boolean isValidLanguage(String lang) {
        return lang != null && (lang.equals("ru") || lang.equals("tr") || lang.equals("en"));
    }



    /**
     * Отображение формы поиска с выпадающими списками локаций
     */
    @GetMapping("/search")
    public String showSearchForm(Model model) {
        // Получаем список всех доступных мест отправления
        List<String> pickupLocations = routeService.getAllActivePickupLocations();

        // Получаем список всех мест назначения (первоначально все доступные)
        List<String> dropoffLocations = routeService.getAllActiveDropoffLocations();

        // Добавляем данные в модель
        model.addAttribute("pickupLocations", pickupLocations);
        model.addAttribute("dropoffLocations", dropoffLocations);
        model.addAttribute("searchDto", new RouteDto.Search());

        return "routes/search";
    }

    /**
     * AJAX метод для получения мест назначения в зависимости от выбранного места отправления
     */
    @GetMapping("/dropoff-locations")
    @ResponseBody
    public List<String> getDropoffLocationsForPickup(@RequestParam String pickupLocation) {
        return routeService.getDropoffLocationsForPickupLocation(pickupLocation);
    }

    /**
     * Обработка формы поиска маршрута
     */
    @PostMapping("/find")
    public String findRoute(
            @RequestParam String pickupLocation,
            @RequestParam String dropoffLocation,
            @RequestParam(defaultValue = "1") Integer passengers,
            @RequestParam(required = false) Currency displayCurrency,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Проверяем, что параметры не пустые
        if (pickupLocation.isEmpty() || dropoffLocation.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Пожалуйста, выберите место отправления и назначения");
            return "redirect:/routes/search";
        }

        // Ищем маршрут
        Optional<Route> routeOpt = routeService.findByPickupAndDropoffLocations(pickupLocation, dropoffLocation);

        if (routeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Маршрут " + pickupLocation + " → " + dropoffLocation + " не найден");
            return "redirect:/routes/search";
        }

        Route route = routeOpt.get();

        // Проверяем, активен ли маршрут
        if (!route.isActive()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Маршрут " + pickupLocation + " → " + dropoffLocation + " временно недоступен");
            return "redirect:/routes/search";
        }

        // Перенаправляем на страницу с результатами
        return "redirect:/routes/result?id=" + route.getId()
                + "&passengers=" + passengers
                + (displayCurrency != null ? "&displayCurrency=" + displayCurrency : "");
    }



    /**
     * Отображение результата поиска маршрута
     */
    @GetMapping("/result")
    public String showRouteResult(
            @RequestParam Long id,
            @RequestParam(defaultValue = "1") Integer passengers,
            @RequestParam(required = false) Currency displayCurrency,
            Model model) {

        // Получаем маршрут
        RouteDto.Response route;
        if (displayCurrency != null) {
            route = routeService.getRouteById(id, displayCurrency);
        } else {
            route = routeService.getRouteById(id);
        }

        // Добавляем данные в модель
        model.addAttribute("route", route);
        model.addAttribute("passengers", passengers);
        model.addAttribute("displayCurrency", displayCurrency);
        model.addAttribute("currencies", Currency.values());

        // Вычисляем общую стоимость в зависимости от количества пассажиров
        // Здесь можно добавить вашу логику ценообразования
        BigDecimal totalPrice;
        if (route.getConvertedPrice() != null) {
            totalPrice = route.getConvertedPrice();
        } else {
            totalPrice = route.getBasePrice();
        }

        // Например, базовая цена за 1-2 пассажиров, +30% за 3-4, +50% за 5+
        if (passengers > 4) {
            totalPrice = totalPrice.multiply(new BigDecimal("1.5"));
        } else if (passengers > 2) {
            totalPrice = totalPrice.multiply(new BigDecimal("1.3"));
        }

        model.addAttribute("totalPrice", totalPrice);

        return "routes/result";
    }

}