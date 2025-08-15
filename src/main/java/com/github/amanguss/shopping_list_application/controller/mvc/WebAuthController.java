package com.github.amanguss.shopping_list_application.controller.mvc;

import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class WebAuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        try {
            var user = userService.getUserByEmail(email);
            if (!userService.verifyPassword(user.getId(), password)) {
                redirectAttributes.addFlashAttribute("error", "Invalid credentials");
                return "redirect:/login";
            }
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getFirstName() + " " + user.getLastName());
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserCreateDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserCreateDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (userService.existsByEmail(dto.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
            return "auth/register";
        }

        try {
            userService.createUser(dto);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to register: " + e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            if (userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("success", "Password reset instructions sent to your email.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Email not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to process request: " + e.getMessage());
        }
        
        return "redirect:/login";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token,
                                   Model model) {
        if (token == null || token.isEmpty()) {
            return "redirect:/forgot-password";
        }
        
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/reset-password?token=" + token;
        }

        try {
            userService.resetPassword(token, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password reset successfully! Please login with your new password.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reset password: " + e.getMessage());
            return "redirect:/reset-password?token=" + token;
        }
    }

    @GetMapping("/profile/change-password")
    public String changePasswordPage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            model.addAttribute("user", user);
            return "auth/change-password";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/profile/change-password";
        }

        try {
            if (!userService.verifyPassword(userId, currentPassword)) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
                return "redirect:/profile/change-password";
            }
            userService.changePassword(userId, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }
}
