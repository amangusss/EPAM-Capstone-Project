package com.github.amanguss.shopping_list_application.controller.mvc;

import com.github.amanguss.shopping_list_application.dto.budget.BudgetCreateDto;
import com.github.amanguss.shopping_list_application.entity.enums.Period;
import com.github.amanguss.shopping_list_application.service.UserService;
import com.github.amanguss.shopping_list_application.service.ShoppingListService;
import com.github.amanguss.shopping_list_application.service.BudgetService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class WebBudgetController {

    private final UserService userService;
    private final ShoppingListService shoppingListService;
    private final BudgetService budgetService;

    @GetMapping("/budgets")
    public String budgets(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var lists = shoppingListService.getShoppingListsByOwner(userId);
            var budgets = budgetService.getBudgetsByUserId(userId);
            
            model.addAttribute("user", user);
            model.addAttribute("shoppingLists", lists);
            model.addAttribute("budgets", budgets);
            return "budgets/budgets";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/budgets/create")
    public String createBudget(@RequestParam Integer shoppingListId,
                               @RequestParam Double amount,
                               @RequestParam String currency,
                               @RequestParam String period,
                               @RequestParam(required = false) String description,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            BudgetCreateDto budgetDto = new BudgetCreateDto();
            budgetDto.setLimit(amount);
            budgetDto.setCurrency(currency);
            budgetDto.setPeriod(Period.valueOf(period));
            budgetDto.setIsActive(true);
            
            budgetService.createBudget(shoppingListId, budgetDto);
            redirectAttributes.addFlashAttribute("success", "Budget created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create budget: " + e.getMessage());
        }
        
        return "redirect:/budgets";
    }
}
