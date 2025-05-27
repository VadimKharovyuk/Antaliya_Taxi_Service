package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.tour.TourCardDto;
import com.example.antaliya_taxi_service.dto.tour.TourDto;
import com.example.antaliya_taxi_service.dto.tour.TourListDto;
import com.example.antaliya_taxi_service.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tours")
public class TourController {

    private final TourService tourService;


    @GetMapping
    public String getAllTours(@PageableDefault(size = 10) Pageable pageable, Model model) {
        TourListDto tourListDto = tourService.getAll(pageable);
        model.addAttribute("tours", tourListDto);
        return "tour/list";
    }


    @GetMapping("/{id}")
    public String getTourById(@PathVariable("id") Long id, Model model) {
        TourDto tour = tourService.findTourById(id);
        model.addAttribute("tour", tour);


        List<TourCardDto> relatedTours = tourService.findRelatedTours(id);
        model.addAttribute("relatedTours", relatedTours);
        return "tour/view";
    }

}
