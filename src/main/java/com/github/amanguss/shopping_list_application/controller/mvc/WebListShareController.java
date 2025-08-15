package com.github.amanguss.shopping_list_application.controller.mvc;

import com.github.amanguss.shopping_list_application.service.UserService;
import com.github.amanguss.shopping_list_application.service.ListShareService;
import com.github.amanguss.shopping_list_application.service.ShoppingListService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class WebListShareController {

    private final UserService userService;
    private final ListShareService listShareService;
    private final ShoppingListService shoppingListService;

    @GetMapping("/list-shares")
    public String listShares(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var sentShares = listShareService.getSharesSentByUser(userId);
            var receivedShares = listShareService.getSharesReceivedByUser(userId);
            var lists = shoppingListService.getShoppingListsByOwner(userId);
            
            model.addAttribute("user", user);
            model.addAttribute("sentShares", sentShares);
            model.addAttribute("receivedShares", receivedShares);
            model.addAttribute("shoppingLists", lists);
            return "lists/list-shares";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/lists/{id}/share")
    public String shareList(@PathVariable Integer id, HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var shoppingList = shoppingListService.getShoppingListById(id);
            var existingShares = listShareService.getSharesByShoppingList(id);
            
            if (!shoppingList.getOwnerId().equals(userId)) {
                return "redirect:/dashboard";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("shoppingList", shoppingList);
            model.addAttribute("existingShares", existingShares);
            return "lists/list-share";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }
}
