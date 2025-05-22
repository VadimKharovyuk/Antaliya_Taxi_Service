package com.example.antaliya_taxi_service.contoler.admin;
import com.example.antaliya_taxi_service.dto.tour.TourCreateDto;
import com.example.antaliya_taxi_service.dto.tour.TourDto;
import com.example.antaliya_taxi_service.dto.tour.TourListDto;
import com.example.antaliya_taxi_service.dto.tour.TourUpdateDto;
import com.example.antaliya_taxi_service.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin/tours")
@RequiredArgsConstructor
public class AdminTourController {

    private final TourService tourService;

    @GetMapping
    public String getAllTours(@PageableDefault(size = 10) Pageable pageable, Model model) {
        TourListDto tours = tourService.getAll(pageable);
        model.addAttribute("tours", tours);
        return "admin/tours/list";
    }

    @GetMapping("/create")
    public String createTourForm(Model model) {
        model.addAttribute("tourCreateDto", new TourCreateDto());
        return "admin/tours/create";
    }

    @PostMapping("/create")
    public String createTour(@Valid @ModelAttribute TourCreateDto tourCreateDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/tours/create";
        }

        try {
            TourDto createdTour = tourService.createTour(tourCreateDto);
            redirectAttributes.addFlashAttribute("successMessage", "Тур успешно создан!");
            return "redirect:/admin/tours";
        } catch (IOException e) {
            bindingResult.rejectValue("image", "error.image", "Ошибка загрузки изображения: " + e.getMessage());
            return "admin/tours/create";
        } catch (Exception e) {
            bindingResult.reject("error.general", "Ошибка создания тура: " + e.getMessage());
            return "admin/tours/create";
        }
    }


    @GetMapping("/{id}")
    public String viewTour(@PathVariable Long id, Model model) {
        try {
            TourDto tour = tourService.findTourById(id);
            model.addAttribute("tour", tour);
            return "admin/tours/view";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Тур не найден");
            return "redirect:/admin/tours";
        }
    }


    @GetMapping("/{id}/edit")
    public String editTourForm(@PathVariable Long id, Model model) {
        try {
            TourDto tour = tourService.findTourById(id);
            TourUpdateDto tourUpdateDto = TourUpdateDto.builder()
                    .id(tour.getId())
                    .title(tour.getTitle())
                    .description(tour.getDescription())
                    .shortDescription(tour.getShortDescription())
                    .price(tour.getPrice())
                    .duration(tour.getDuration())
                    .isBestseller(tour.getIsBestseller())
                    .maxParticipants(tour.getMaxParticipants())
                    .language(tour.getLanguage())
                    .build();

            model.addAttribute("tourUpdateDto", tourUpdateDto);
            model.addAttribute("tour", tour);
            return "admin/tours/edit";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Тур не найден");
            return "redirect:/admin/tours";
        }
    }

    // Обновление тура
    @PostMapping("/{id}/edit")
    public String updateTour(@PathVariable Long id,
                             @Valid @ModelAttribute TourUpdateDto tourUpdateDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        tourUpdateDto.setId(id);

        if (bindingResult.hasErrors()) {
            TourDto tour = tourService.findTourById(id);
            model.addAttribute("tour", tour);
            return "admin/tours/edit";
        }

        try {
            TourDto updatedTour = tourService.updateTour(tourUpdateDto);
            redirectAttributes.addFlashAttribute("successMessage", "Тур успешно обновлен!");
            return "redirect:/admin/tours/" + id;
        } catch (Exception e) {
            bindingResult.reject("error.general", "Ошибка обновления тура: " + e.getMessage());
            TourDto tour = tourService.findTourById(id);
            model.addAttribute("tour", tour);
            return "admin/tours/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteTour(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tourService.deleteTour(id);
            redirectAttributes.addFlashAttribute("successMessage", "Тур успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка удаления тура: " + e.getMessage());
        }
        return "redirect:/admin/tours";
    }
}
