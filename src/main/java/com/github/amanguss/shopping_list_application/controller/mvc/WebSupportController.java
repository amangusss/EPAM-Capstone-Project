package com.github.amanguss.shopping_list_application.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class WebSupportController {

    @GetMapping("/help")
    public String helpPage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "info/help";
    }

    @GetMapping("/contact")
    public String contactPage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "info/contact";
    }

    @PostMapping("/contact")
    public String submitContact(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String subject,
                               @RequestParam String message,
                               HttpSession session,
                               Model model) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        
        model.addAttribute("submittedName", name);
        model.addAttribute("submittedEmail", email);
        model.addAttribute("submittedSubject", subject);
        model.addAttribute("submittedMessage", message);
        
        model.addAttribute("success", "Thank you for your message! We'll get back to you soon.");
        return "info/contact";
    }
}
