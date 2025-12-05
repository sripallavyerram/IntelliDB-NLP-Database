package com.intellidb.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingController {

    @GetMapping("/")
    public String landingPage() {
        // This will look for a landing.html file
        return "landing";
    }
}