package com.example.antaliya_taxi_service.contoler.admin;

import com.example.antaliya_taxi_service.dto.vehicle.*;
import com.example.antaliya_taxi_service.enums.FuelType;
import com.example.antaliya_taxi_service.enums.VehicleClass;
import com.example.antaliya_taxi_service.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/vehicles")
@RequiredArgsConstructor
@Slf4j
public class AdminVehicleController {

    private final VehicleService vehicleService;

    /**
     * Список всех автомобилей
     */
    @GetMapping
    public String listVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        VehicleListDto vehicleList = vehicleService.getVehicles(pageable);

        model.addAttribute("vehicleList", vehicleList);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "admin/vehicles/list";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicle", new VehicleCreateDTO());
        model.addAttribute("vehicleClasses", VehicleClass.values());
        model.addAttribute("fuelTypes", FuelType.values());
        return "admin/vehicles/create";
    }


    @PostMapping("/create")
    public String createVehicle(
            @Valid @ModelAttribute("vehicle") VehicleCreateDTO createDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicleClasses", VehicleClass.values());
            model.addAttribute("fuelTypes", FuelType.values());
            return "admin/vehicles/create";
        }

        try {
            VehicleResponseDTO created = vehicleService.create(createDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Автомобиль успешно создан с ID: " + created.getId());
            return "redirect:/admin/vehicles";
        } catch (Exception e) {
            log.error("Ошибка при создании автомобиля: {}", e.getMessage());
            model.addAttribute("errorMessage", "Ошибка при создании автомобиля: " + e.getMessage());
            model.addAttribute("vehicleClasses", VehicleClass.values());
            model.addAttribute("fuelTypes", FuelType.values());
            return "admin/vehicles/create";
        }
    }


    @GetMapping("/{id}")
    public String viewVehicle(@PathVariable Long id, Model model) {
        try {
            VehicleResponseDTO vehicle = vehicleService.getById(id);
            model.addAttribute("vehicle", vehicle);
            return "admin/vehicles/view";
        } catch (Exception e) {
            log.error("Ошибка при получении автомобиля с ID {}: {}", id, e.getMessage());
            return "redirect:/admin/vehicles?error=Автомобиль не найден";
        }
    }


    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            // Один метод сервиса решает все!
            VehicleUpdateDTO updateDTO = vehicleService.getForEdit(id);
            VehicleResponseDTO vehicle = vehicleService.getById(id); // Для imageUrl

            model.addAttribute("vehicle", updateDTO);
            model.addAttribute("currentImageUrl", vehicle.getImageUrl());
            model.addAttribute("vehicleClasses", VehicleClass.values());
            model.addAttribute("fuelTypes", FuelType.values());
            return "admin/vehicles/edit";
        } catch (Exception e) {
            log.error("Ошибка при получении автомобиля для редактирования с ID {}: {}", id, e.getMessage());
            return "redirect:/admin/vehicles?error=Автомобиль не найден";
        }
    }

    /**
     * Обновление автомобиля
     */
    @PostMapping("/{id}/update")
    public String updateVehicle(
            @PathVariable Long id,
            @Valid @ModelAttribute("vehicle") VehicleUpdateDTO updateDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        updateDTO.setId(id); // Убеждаемся, что ID установлен

        if (bindingResult.hasErrors()) {
            try {
                VehicleResponseDTO currentVehicle = vehicleService.getById(id);
                model.addAttribute("currentImageUrl", currentVehicle.getImageUrl());
            } catch (Exception ignored) {}

            model.addAttribute("vehicleClasses", VehicleClass.values());
            model.addAttribute("fuelTypes", FuelType.values());
            return "admin/vehicles/edit";
        }

        try {
            VehicleResponseDTO updated = vehicleService.update(updateDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Автомобиль успешно обновлен");
            return "redirect:/admin/vehicles/" + updated.getId();
        } catch (Exception e) {
            log.error("Ошибка при обновлении автомобиля с ID {}: {}", id, e.getMessage());
            model.addAttribute("errorMessage", "Ошибка при обновлении автомобиля: " + e.getMessage());
            model.addAttribute("vehicleClasses", VehicleClass.values());
            model.addAttribute("fuelTypes", FuelType.values());
            return "admin/vehicles/edit";
        }
    }

    /**
     * Удаление автомобиля
     */
    @PostMapping("/{id}/delete")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vehicleService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Автомобиль успешно удален");
        } catch (Exception e) {
            log.error("Ошибка при удалении автомобиля с ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при удалении автомобиля: " + e.getMessage());
        }
        return "redirect:/admin/vehicles";
    }

    /**
     * Переключение статуса активности автомобиля
     */
    @PostMapping("/{id}/toggle-status")
    public String toggleVehicleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            VehicleResponseDTO vehicle = vehicleService.getById(id);

            VehicleUpdateDTO updateDTO = VehicleUpdateDTO.builder()
                    .id(id)
                    .active(!vehicle.getActive())
                    .build();

            vehicleService.update(updateDTO);

            String status = updateDTO.getActive() ? "активирован" : "деактивирован";
            redirectAttributes.addFlashAttribute("successMessage",
                    "Автомобиль успешно " + status);
        } catch (Exception e) {
            log.error("Ошибка при изменении статуса автомобиля с ID {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при изменении статуса автомобиля");
        }
        return "redirect:/admin/vehicles";
    }
}