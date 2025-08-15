package com.github.amanguss.shopping_list_application.controller.mvc;

import com.github.amanguss.shopping_list_application.service.ItemService;
import com.github.amanguss.shopping_list_application.service.CategoryService;
import com.github.amanguss.shopping_list_application.service.ShoppingListService;
import com.github.amanguss.shopping_list_application.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class WebItemController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final ShoppingListService shoppingListService;
    private final UserService userService;

    @GetMapping("/items")
    public String viewAllItems(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var items = itemService.getAllItemsByUser(userId);
            var categories = categoryService.getAllCategories();
            var shoppingLists = shoppingListService.getShoppingListsByOwner(userId);

            model.addAttribute("user", user);
            model.addAttribute("items", items);
            model.addAttribute("categories", categories);
            model.addAttribute("shoppingLists", shoppingLists);
            return "items/items";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/items/{id}/toggle")
    public String toggleItem(@PathVariable Integer id,
                             @RequestParam(required = false) Double actualPrice,
                             HttpSession session,
                             Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var item = itemService.getItemById(id);
            if (item.getIsPurchased()) {
                itemService.markAsUnpurchased(id);
            } else {
                itemService.markAsPurchased(id, actualPrice != null ? actualPrice : item.getEstimatedPrice());
            }

            return "redirect:/lists/" + item.getShoppingListId();
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable Integer id, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var item = itemService.getItemById(id);
            Integer listId = item.getShoppingListId();
            itemService.deleteItem(id);

            return "redirect:/lists/" + listId;
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }
}
