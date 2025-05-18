package com.example.antaliya_taxi_service.contoler.admin;
import com.example.antaliya_taxi_service.dto.RouteDto;
import com.example.antaliya_taxi_service.enums.Currency;
import com.example.antaliya_taxi_service.service.CurrencyService;
import com.example.antaliya_taxi_service.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/routes")
@RequiredArgsConstructor
@Slf4j
public class AdminRouteController {

    private final RouteService routeService;
    private final CurrencyService currencyService;

    /**
     * Отображение списка всех маршрутов
     */
    @GetMapping
    public String showAllRoutes(
            @RequestParam(required = false) Currency displayCurrency,
            @RequestParam(required = false) String success,
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

        // Добавляем сообщения об успешных операциях в зависимости от параметра success
        if (success != null) {
            switch (success) {
                case "created":
                    model.addAttribute("successMessage", "Маршрут успешно создан!");
                    break;
                case "updated":
                    model.addAttribute("successMessage", "Маршрут успешно обновлен!");
                    break;
                case "deleted":
                    model.addAttribute("successMessage", "Маршрут успешно удален!");
                    break;
                case "image-uploaded":
                    model.addAttribute("successMessage", "Изображение успешно загружено!");
                    break;
                case "image-deleted":
                    model.addAttribute("successMessage", "Изображение успешно удалено!");
                    break;
            }
        }

        return "admin/routes/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("routeDto", new RouteDto.Create());
        model.addAttribute("currencies", Currency.values());
        return "admin/routes/create";
    }

    /**
     * Обработка создания нового маршрута с поддержкой загрузки изображения
     */
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createRoute(
            @Valid @ModelAttribute("routeDto") RouteDto.Create routeDto,
            BindingResult bindingResult,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Важно: проверяем наличие ошибок валидации
        if (bindingResult.hasErrors()) {
            model.addAttribute("currencies", Currency.values());
            return "admin/routes/create";
        }

        try {
            // Логирование для отладки
            log.info("Получены данные - Откуда: {}, Куда: {}, Расстояние: {}, Время: {}, Цена: {}, Валюта: {}",
                    routeDto.getPickupLocation(),
                    routeDto.getDropoffLocation(),
                    routeDto.getDistance(),
                    routeDto.getEstimatedTime(),
                    routeDto.getBasePrice(),
                    routeDto.getCurrency());

            RouteDto.Response createdRoute;

            if (imageFile != null && !imageFile.isEmpty()) {
                log.info("Создание маршрута с изображением: {} -> {}",
                        routeDto.getPickupLocation(), routeDto.getDropoffLocation());
                createdRoute = routeService.createRoute(routeDto, imageFile);
            } else {
                log.info("Создание маршрута без изображения: {} -> {}",
                        routeDto.getPickupLocation(), routeDto.getDropoffLocation());
                createdRoute = routeService.createRoute(routeDto);
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Маршрут успешно создан: " + createdRoute.getPickupLocation() + " → " + createdRoute.getDropoffLocation());

            return "redirect:/admin/routes";

        } catch (IOException e) {
            log.error("Ошибка при загрузке изображения: {}", e.getMessage());
            model.addAttribute("currencies", Currency.values());
            model.addAttribute("errorMessage", "Ошибка при загрузке изображения: " + e.getMessage());
            return "admin/routes/create";
        }
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
        updateDto.setUrl(route.getUrl());
        updateDto.setImageId(route.getImageId());

        model.addAttribute("routeDto", updateDto);
        model.addAttribute("route", route);
        model.addAttribute("currencies", Currency.values());

        return "admin/routes/edit";
    }

    /**
     * Обработка обновления маршрута с поддержкой обновления изображения
     */
    @PostMapping(value = "/edit/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateRoute(
            @PathVariable Long id,
            @ModelAttribute("routeDto") RouteDto.Update routeDto,
            BindingResult result,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!id.equals(routeDto.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Недопустимый ID маршрута");
            return "redirect:/admin/routes";
        }

        if (result.hasErrors()) {
            model.addAttribute("currencies", Currency.values());
            // Добавляем обратно исходный маршрут для отображения изображения
            model.addAttribute("route", routeService.getRouteById(id));
            return "admin/routes/edit";
        }

        try {
            RouteDto.Response updatedRoute;

            if (imageFile != null && !imageFile.isEmpty()) {
                log.info("Обновление маршрута ID {} с новым изображением", id);
                updatedRoute = routeService.updateRoute(routeDto, imageFile);
            } else {
                log.info("Обновление маршрута ID {} без изменения изображения", id);
                updatedRoute = routeService.updateRoute(routeDto);
            }

            redirectAttributes.addFlashAttribute("successMessage",
                    "Маршрут успешно обновлен: " + updatedRoute.getPickupLocation() + " → " + updatedRoute.getDropoffLocation());

            return "redirect:/admin/routes";

        } catch (IOException e) {
            log.error("Ошибка при обновлении изображения: {}", e.getMessage());
            model.addAttribute("currencies", Currency.values());
            model.addAttribute("errorMessage", "Ошибка при обновлении изображения: " + e.getMessage());
            // Добавляем обратно исходный маршрут для отображения изображения
            model.addAttribute("route", routeService.getRouteById(id));
            return "admin/routes/edit";
        }
    }

    /**
     * Загрузка изображения для существующего маршрута
     */
    @PostMapping("/upload-image/{id}")
    public String uploadRouteImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        if (imageFile == null || imageFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Файл изображения не выбран");
            return "redirect:/admin/routes/edit/" + id;
        }

        try {
            RouteDto.Response updatedRoute = routeService.uploadRouteImage(id, imageFile);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Изображение для маршрута " + updatedRoute.getPickupLocation() + " → " + updatedRoute.getDropoffLocation() + " успешно загружено");
            return "redirect:/admin/routes/edit/" + id;
        } catch (IOException e) {
            log.error("Ошибка при загрузке изображения для маршрута ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при загрузке изображения: " + e.getMessage());
            return "redirect:/admin/routes/edit/" + id;
        }
    }

    /**
     * Удаление изображения маршрута
     */
    @PostMapping("/delete-image/{id}")
    public String deleteRouteImage(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            RouteDto.Response updatedRoute = routeService.deleteRouteImage(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Изображение для маршрута " + updatedRoute.getPickupLocation() + " → " + updatedRoute.getDropoffLocation() + " успешно удалено");
            return "redirect:/admin/routes/edit/" + id;
        } catch (IOException e) {
            log.error("Ошибка при удалении изображения для маршрута ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении изображения: " + e.getMessage());
            return "redirect:/admin/routes/edit/" + id;
        }
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
     * Удаление маршрута вместе с изображением
     */
    @PostMapping("/delete/{id}")
    public String deleteRoute(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        RouteDto.Response route = routeService.getRouteById(id);
        String routeInfo = route.getPickupLocation() + " → " + route.getDropoffLocation();

        try {
            // Используем метод удаления с изображением
            routeService.deleteRouteWithImage(id);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Маршрут " + routeInfo + " удален");

        } catch (IOException e) {
            log.error("Ошибка при удалении изображения маршрута ID {}: {}", id, e.getMessage());

            // В случае ошибки удаления изображения, продолжаем удаление самого маршрута
            routeService.deleteRoute(id);

            redirectAttributes.addFlashAttribute("warningMessage",
                    "Маршрут " + routeInfo + " удален, но возникла ошибка при удалении изображения: " + e.getMessage());
        }

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

    /**
     * Обработка ошибок максимального размера файла
     */
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(
            org.springframework.web.multipart.MaxUploadSizeExceededException e,
            RedirectAttributes redirectAttributes) {

        log.error("Превышен максимально допустимый размер файла: {}", e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage",
                "Превышен максимально допустимый размер файла. Пожалуйста, выберите файл меньшего размера (макс. 10MB).");

        return "redirect:/admin/routes";
    }
}