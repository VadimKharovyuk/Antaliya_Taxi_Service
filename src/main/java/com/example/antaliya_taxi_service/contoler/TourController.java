package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.tour.*;
import com.example.antaliya_taxi_service.model.Tour;
import com.example.antaliya_taxi_service.service.TourService;
import com.example.antaliya_taxi_service.service.TourTranslationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tours")
public class TourController {

    private final TourService tourService;
    private final TourTranslationService tourTranslationService;


    @GetMapping
    public String getAllTours(@PageableDefault(size = 10) Pageable pageable,
                              @RequestParam(required = false) String lang,
                              Model model) {

        TourListDto tourListDto = tourService.getAll(pageable);

        // Если указан язык перевода - переводим туры
        if (lang != null && isValidLanguage(lang)) {
            List<TourCardTranslationDto> translatedTours = tourListDto.getTourCardDtos().stream()
                    .map(tour -> tourTranslationService.translateTourCard(tour, lang, "ru")) // предполагаем исходный язык русский
                    .collect(Collectors.toList());
            model.addAttribute("translatedTours", translatedTours);
        }

        model.addAttribute("tours", tourListDto);
        return "tour/list";
    }


    @GetMapping("/{id}")
    public String getTourById(@PathVariable("id") Long id,
                              @RequestParam(required = false) String lang,
                              Model model,
                              HttpServletRequest request) {

        TourDto tour = tourService.findTourById(id);
        model.addAttribute("tour", tour);

        // Добавляем перевод, если язык задан и допустим
        if (lang != null && isValidLanguage(lang)) {
            Tour entity = tourService.findTourEntityById(id); // метод, возвращающий саму сущность Tour
            TourTranslationDto translatedTour = tourTranslationService.translate(entity, lang);
            model.addAttribute("translatedTour", translatedTour);
        }

        List<TourCardDto> relatedTours = tourService.findRelatedTours(id);
        model.addAttribute("relatedTours", relatedTours);
        return "tour/view";
    }

    private boolean isValidLanguage(String lang) {
        return lang != null && (lang.equals("ru") || lang.equals("tr") || lang.equals("en"));
    }

}
