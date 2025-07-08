package com.niet.stockmanagement.controller;

import com.google.gson.Gson;
import com.niet.stockmanagement.dao.ProductDAO;
import com.niet.stockmanagement.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private ProductDAO productDAO;

    @GetMapping("/analytics")
    public String showAnalytics(Model model) {
        List<String> categories = productDAO.getAllCategories();
        List<Integer> stockCounts = productDAO.getStockCountPerCategory();
        int totalProducts = productDAO.getTotalProductCount();
        int lowStock = productDAO.getLowStockCount();
        List<Product> topProducts = productDAO.getTopExpensiveProducts(5);

        // Convert to JSON using Gson
        Gson gson = new Gson();
        String jsonCategories = gson.toJson(categories);
        String jsonStockCounts = gson.toJson(stockCounts);

        List<String> topLabels = topProducts.stream().map(Product::getName).collect(Collectors.toList());
        List<Double> topPrices = topProducts.stream().map(Product::getPrice).collect(Collectors.toList());
        String jsonTopLabels = gson.toJson(topLabels);
        String jsonTopPrices = gson.toJson(topPrices);

        // Pass to Thymeleaf
        model.addAttribute("categoryLabels", jsonCategories);
        model.addAttribute("categoryData", jsonStockCounts);
        model.addAttribute("lowStockPercentage", totalProducts == 0 ? 0 : (lowStock * 100.0) / totalProducts);
        model.addAttribute("expensiveLabels", jsonTopLabels);
        model.addAttribute("expensiveData", jsonTopPrices);

        return "analytics";
    }
}
