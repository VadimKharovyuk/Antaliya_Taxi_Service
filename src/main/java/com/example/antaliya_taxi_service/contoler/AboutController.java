package com.example.antaliya_taxi_service.contoler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/about")
@Controller
public class AboutController {


    @GetMapping
    public String contact(Model model) {
        return "contact/index";
    }
}
