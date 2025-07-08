package com.niet.stockmanagement.controller;

import com.niet.stockmanagement.dao.UserDAO;
import com.niet.stockmanagement.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SupportController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private JavaMailSender mailSender;  // ✅ Inject mail sender

    @GetMapping("/support")
    public String supportPage(HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }
        return "support";
    }

    @PostMapping("/support")
    public String handleSupport(@RequestParam String email,
                                @RequestParam String message,
                                HttpSession session,
                                Model model) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo("anshu02122002@gmail.com"); // ✅ Replace with your real admin email
            mail.setSubject("📩 Support Request from " + email);
            mail.setText("Message:\n" + message);

            mailSender.send(mail);

            model.addAttribute("successMessage", "✅ Your support request has been sent to the admin.");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "❌ Failed to send support request. Please try again later.");
        }

        return "support";
    }

    @GetMapping("/change-password")
    public String showChangePasswordPage(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        return "change_password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {

        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        String username = currentUser.getUsername();

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "❌ New password and confirm password do not match.");
            return "change_password";
        }

        boolean success = userDAO.changePassword(username, oldPassword, newPassword);
        if (success) {
            model.addAttribute("success", "✅ Password changed successfully.");
        } else {
            model.addAttribute("error", "❌ Old password is incorrect.");
        }

        return "change_password";
    }
}
