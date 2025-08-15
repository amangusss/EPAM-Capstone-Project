package com.github.amanguss.shopping_list_application.controller.mvc;

import com.github.amanguss.shopping_list_application.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class WebUserManagementController {

    private final UserService userService;

    @GetMapping("/user-management")
    public String userManagement(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var allUsers = userService.getAllUsers();
            
            model.addAttribute("user", user);
            model.addAttribute("users", allUsers);
            return "profile/user-management";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }
}
