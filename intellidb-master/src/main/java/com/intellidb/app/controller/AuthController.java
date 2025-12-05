package com.intellidb.app.controller;

import com.intellidb.app.model.User;
import com.intellidb.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Renders login.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Renders register.html
    }



    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/login?success"; // Redirect to login page with a success message
    }
}