package com.example.antaliya_taxi_service.contoler;

import com.example.antaliya_taxi_service.dto.vehicle.VehicleCardDto;
import com.example.antaliya_taxi_service.dto.vehicle.VehicleListDto;
import com.example.antaliya_taxi_service.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/vehicle")
public class VehicleController {
    private final VehicleService vehicleService;


    @GetMapping
    public String listActiveCAr(Model model ,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir) {

            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            VehicleListDto vehicleList = vehicleService.getVehicles(pageable);

            model.addAttribute("vehicleList", vehicleList);
            model.addAttribute("currentPage", page);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);

            return "vehicle/list";
    }
}
