package com.github.amanguss.shopping_list_application.controller.mvc;

import com.github.amanguss.shopping_list_application.service.UserService;
import com.github.amanguss.shopping_list_application.service.ShoppingListService;
import com.github.amanguss.shopping_list_application.service.ListShareService;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class WebDashboardController {

    private final UserService userService;
    private final ShoppingListService shoppingListService;
    private final ListShareService listShareService;

    @GetMapping("/")
    public String home(Model model) {
        return "dashboard/index";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var lists = shoppingListService.getShoppingListsByOwner(userId);
            var sharedLists = listShareService.getSharesReceivedByUser(userId);

            long completedLists = lists.stream().filter(list -> ListStatus.ARCHIVED.equals(list.getStatus())).count();
            
            model.addAttribute("user", user);
            model.addAttribute("lists", lists);
            model.addAttribute("sharedLists", sharedLists);
            model.addAttribute("totalLists", lists.size());
            model.addAttribute("completedLists", completedLists);
            model.addAttribute("totalShared", sharedLists.size());

            return "dashboard/dashboard";
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login";
        }
    }
}
