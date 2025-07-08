package com.niet.stockmanagement.controller;

import com.niet.stockmanagement.dao.CategoryDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryDAO categoryDAO;

    @PostMapping("/add")
    public String addCategory(@RequestParam String name, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }
        categoryDAO.addCategory(name);
        return "redirect:/add";
    }
}
