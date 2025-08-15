package com.github.amanguss.shopping_list_application.controller.mvc;

import com.github.amanguss.shopping_list_application.dto.category.CategoryCreateDto;
import com.github.amanguss.shopping_list_application.service.UserService;
import com.github.amanguss.shopping_list_application.service.CategoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class WebCategoryController {

    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String categories(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var categories = categoryService.getAllCategories();

            model.addAttribute("user", user);
            model.addAttribute("categories", categories);
            return "categories/categories";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/categories/create")
    public String createCategory(@RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            CategoryCreateDto categoryDto = new CategoryCreateDto();
            categoryDto.setName(name);
            categoryDto.setDescription(description);
            categoryDto.setColor("#007bff");
            categoryDto.setIsSystemCategory(false);
            categoryDto.setDisplayOrder(0);
            
            categoryService.createCategory(categoryDto);
            redirectAttributes.addFlashAttribute("success", "Category created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create category: " + e.getMessage());
        }
        
        return "redirect:/categories";
    }
}
