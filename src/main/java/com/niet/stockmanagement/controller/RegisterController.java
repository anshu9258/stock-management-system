package com.niet.stockmanagement.controller;

import com.niet.stockmanagement.dao.UserDAO;
import com.niet.stockmanagement.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private UserDAO userDAO;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userDAO.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "⚠️ Username already exists");
            return "register";
        }

        user.setRole("USER"); 
        userDAO.save(user);

        model.addAttribute("message", "✅ Registration successful! Please login.");
        return "login";
    }
}
