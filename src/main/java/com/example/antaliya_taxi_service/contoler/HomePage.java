package com.example.antaliya_taxi_service.contoler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePage {

@GetMapping
    public String  HomePage() {
//        return "home";
        return "homeV1";
    }
}
