package com.example.antaliya_taxi_service.contoler.admin;
import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.service.CurrencyService;
import com.example.antaliya_taxi_service.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/routes")
@RequiredArgsConstructor
public class AdminRouteController {

    private final RouteService routeService;
    private final CurrencyService currencyService;

    /**
     * Отображение списка всех маршрутов
     */
    @GetMapping
    public String showAllRoutes(
            @RequestParam(required = false) Currency displayCurrency,
            Model model) {

        List<RouteDto.Response> routes;
        if (displayCurrency != null) {
            routes = routeService.getAllRoutes(displayCurrency);
            model.addAttribute("displayCurrency", displayCurrency);
        } else {
            routes = routeService.getAllRoutes();
        }

        model.addAttribute("routes", routes);
        model.addAttribute("currencies", Currency.values());

        return "admin/routes/list";
    }

    /**
     * Отображение формы создания маршрута
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("routeDto", new RouteDto.Create());
        model.addAttribute("currencies", Currency.values());
        return "admin/routes/create";
    }

    /**
     * Обработка создания нового маршрута
     */
    @PostMapping("/create")
    public String createRoute(
            @Valid @ModelAttribute("routeDto") RouteDto.Create routeDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("currencies", Currency.values());
            return "admin/routes/create";
        }

        RouteDto.Response createdRoute = routeService.createRoute(routeDto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Маршрут успешно создан: " + createdRoute.getPickupLocation() + " → " + createdRoute.getDropoffLocation());

        return "redirect:/admin/routes";
    }

    /**
     * Отображение формы редактирования маршрута
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        RouteDto.Response route = routeService.getRouteById(id);

        RouteDto.Update updateDto = new RouteDto.Update();
        updateDto.setId(route.getId());
        updateDto.setPickupLocation(route.getPickupLocation());
        updateDto.setDropoffLocation(route.getDropoffLocation());
        updateDto.setDistance(route.getDistance());
        updateDto.setEstimatedTime(route.getEstimatedTime());
        updateDto.setBasePrice(route.getBasePrice());
        updateDto.setCurrency(route.getCurrency());
        updateDto.setActive(route.isActive());

        model.addAttribute("routeDto", updateDto);
        model.addAttribute("currencies", Currency.values());

        return "admin/routes/edit";
    }

    /**
     * Обработка обновления маршрута
     */
    @PostMapping("/edit/{id}")
    public String updateRoute(
            @PathVariable Long id,
            @Valid @ModelAttribute("routeDto") RouteDto.Update routeDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!id.equals(routeDto.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Недопустимый ID маршрута");
            return "redirect:/admin/routes";
        }

        if (result.hasErrors()) {
            model.addAttribute("currencies", Currency.values());
            return "admin/routes/edit";
        }

        RouteDto.Response updatedRoute = routeService.updateRoute(routeDto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Маршрут успешно обновлен: " + updatedRoute.getPickupLocation() + " → " + updatedRoute.getDropoffLocation());

        return "redirect:/admin/routes";
    }

    /**
     * Отображение детальной информации о маршруте
     */
    @GetMapping("/view/{id}")
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

        model.addAttribute("route", route);
        model.addAttribute("currencies", Currency.values());

        return "admin/routes/view";
    }

    /**
     * Изменение статуса маршрута (активация/деактивация)
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleRouteStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            RedirectAttributes redirectAttributes) {

        RouteDto.Response route = routeService.toggleRouteStatus(id, active);
        String status = active ? "активирован" : "деактивирован";

        redirectAttributes.addFlashAttribute("successMessage",
                "Маршрут " + route.getPickupLocation() + " → " + route.getDropoffLocation() + " " + status);

        return "redirect:/admin/routes";
    }

    /**
     * Удаление маршрута
     */
    @PostMapping("/delete/{id}")
    public String deleteRoute(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        RouteDto.Response route = routeService.getRouteById(id);
        String routeInfo = route.getPickupLocation() + " → " + route.getDropoffLocation();

        routeService.deleteRoute(id);

        redirectAttributes.addFlashAttribute("successMessage",
                "Маршрут " + routeInfo + " удален");

        return "redirect:/admin/routes";
    }

    /**
     * Поиск маршрутов
     */
    @GetMapping("/search")
    public String searchRoutes(
            @ModelAttribute("searchDto") RouteDto.Search searchDto,
            @RequestParam(required = false) Currency displayCurrency,
            Model model) {

        List<RouteDto.SearchResult> results;
        if (displayCurrency != null) {
            results = routeService.searchRoutes(searchDto, displayCurrency);
            model.addAttribute("displayCurrency", displayCurrency);
        } else {
            results = routeService.searchRoutes(searchDto);
        }

        model.addAttribute("searchResults", results);
        model.addAttribute("currencies", Currency.values());

        return "admin/routes/search-results";
    }
}