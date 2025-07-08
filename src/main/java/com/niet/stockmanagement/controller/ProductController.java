package com.niet.stockmanagement.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.niet.stockmanagement.dao.ProductDAO;
import com.niet.stockmanagement.dao.CategoryDAO;
import com.niet.stockmanagement.model.Product;
import com.niet.stockmanagement.model.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Controller
public class ProductController {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @GetMapping("/add")
    public String showAddForm(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/unauthorized";
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryDAO.getAllCategories());
        return "new_product";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/unauthorized";
        try {
            MultipartFile imageFile = product.getImageFile();
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                String uploadDir = "uploads";
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();
                Path path = Paths.get(uploadDir, imageName);
                Files.write(path, imageFile.getBytes());
                product.setImage(imageName);
            }
            productDAO.insertProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/unauthorized";
        Product product = productDAO.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryDAO.getAllCategories());
        return "edit_product";
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/unauthorized";
        try {
            MultipartFile imageFile = product.getImageFile();
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                String uploadDir = "uploads";
                File folder = new File(uploadDir);
                if (!folder.exists()) folder.mkdirs();
                Path path = Paths.get(uploadDir, imageName);
                Files.write(path, imageFile.getBytes());
                product.setImage(imageName);
            }
            productDAO.updateProduct(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/sell/{id}")
    public String sellProduct(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/unauthorized";
        productDAO.sellOneItem(id);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/unauthorized";
        productDAO.deleteProductById(id);
        return "redirect:/";
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("uploads").resolve(filename);
            Resource resource = new UrlResource(imagePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(imagePath);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/delete-image/{id}")
    public String deleteImage(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/unauthorized";
        try {
            Product product = productDAO.getProductById(id);
            if (product != null && product.getImage() != null) {
                Path imagePath = Paths.get("uploads", product.getImage());
                Files.deleteIfExists(imagePath);
                product.setImage(null);
                productDAO.updateProduct(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/edit/" + id;
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model, HttpSession session) {
        if (session.getAttribute("currentUser") == null) return "redirect:/login";
        List<Product> products = productDAO.searchProductsByKeyword(keyword);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryDAO.getAllCategories());
        model.addAttribute("totalItems", products.size());
        model.addAttribute("totalStock", productDAO.getTotalStock());
        model.addAttribute("lowStockCount", productDAO.getLowStockCount());
        return "index";
    }

    @GetMapping("/filter")
    public String filterByCategory(@RequestParam("category") String category, Model model, HttpSession session) {
        if (session.getAttribute("currentUser") == null) return "redirect:/login";
        List<Product> products = (category == null || category.isEmpty())
                ? productDAO.getAllProducts()
                : productDAO.getProductsByCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryDAO.getAllCategories());
        model.addAttribute("totalItems", products.size());
        model.addAttribute("totalStock", productDAO.getTotalStock());
        model.addAttribute("lowStockCount", productDAO.getLowStockCount());
        return "index";
    }

    @GetMapping("/download-pdf")
    public void downloadPdf(HttpServletResponse response) {
        try {
            List<Product> products = productDAO.getAllProducts();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=inventory_report.pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("📦 Stock Inventory Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Generated on: " + new java.util.Date()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Stream.of("Product Name", "Quantity", "Price", "Category")
                    .forEach(header -> {
                        PdfPCell cell = new PdfPCell(new Phrase(header));
                        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(cell);
                    });

            for (Product p : products) {
                table.addCell(p.getName());
                table.addCell(String.valueOf(p.getQuantity()));
                table.addCell("₹ " + String.format("%.2f", p.getPrice()));
                table.addCell(p.getCategory());
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAdmin(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }
}
