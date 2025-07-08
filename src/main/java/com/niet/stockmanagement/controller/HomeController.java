package com.niet.stockmanagement.controller;

import com.niet.stockmanagement.dao.CategoryDAO;
import com.niet.stockmanagement.dao.ProductDAO;
import com.niet.stockmanagement.model.Category;
import com.niet.stockmanagement.model.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        List<Product> products = productDAO.getAllProducts();
        List<Category> categories = categoryDAO.getAllCategories();

        int totalItems = products.size(); // total number of products
        int totalStock = products.stream().mapToInt(Product::getQuantity).sum(); // total quantity
        long lowStockCount = products.stream().filter(p -> p.getQuantity() < 10).count(); // low stock count

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalStock", totalStock);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("currentUser", session.getAttribute("currentUser"));

        return "index";
    }
}
