package com.niet.stockmanagement.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.niet.stockmanagement.dao.ProductDAO;
import com.niet.stockmanagement.model.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
public class ReportController {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private JavaMailSender javaMailSender;

    @GetMapping("/report")
    public String reportForm() {
        return "report_form";
    }

    @PostMapping("/sendReport")
    public String sendReport(@RequestParam("email") String email, Model model) {
        try {
            List<Product> productList = productDAO.getAllProducts();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            document.add(new Paragraph("📦 Stock Report", titleFont));
            document.add(new Paragraph(" ")); // Empty line

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 2, 2, 3});

            table.addCell("ID");
            table.addCell("Name");
            table.addCell("Quantity");
            table.addCell("Price");
            table.addCell("Category");

            for (Product p : productList) {
                table.addCell(String.valueOf(p.getId()));
                table.addCell(p.getName());
                table.addCell(String.valueOf(p.getQuantity()));
                table.addCell(String.valueOf(p.getPrice()));
                table.addCell(p.getCategory());
            }

            document.add(table);
            document.close();

            sendEmailWithAttachment(email, baos.toByteArray());
            model.addAttribute("success", "✅ Report sent successfully to " + email);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "❌ Error sending report: " + e.getMessage());
        }
        return "report_form";
    }

    private void sendEmailWithAttachment(String toEmail, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Stock Report PDF");
        helper.setText("Hi,\n\nPlease find the attached stock report PDF.\n\nRegards,\nStock Management System");
        helper.addAttachment("Stock_Report.pdf", new ByteArrayResource(pdfBytes));

        javaMailSender.send(message);
    }
}
