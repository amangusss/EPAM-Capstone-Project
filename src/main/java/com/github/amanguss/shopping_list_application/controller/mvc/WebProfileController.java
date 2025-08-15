package com.github.amanguss.shopping_list_application.controller.mvc;

import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.service.UserService;
import com.github.amanguss.shopping_list_application.service.ShoppingListService;
import com.github.amanguss.shopping_list_application.service.ListShareService;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class WebProfileController {

    private final UserService userService;
    private final ShoppingListService shoppingListService;
    private final ListShareService listShareService;

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
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
            model.addAttribute("totalLists", lists.size());
            model.addAttribute("completedLists", completedLists);
            model.addAttribute("sharedLists", sharedLists.size());
            
            return "profile/profile";
        } catch (Exception e) {
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") com.github.amanguss.shopping_list_application.entity.User user,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            UserCreateDto updateDto = new UserCreateDto();
            updateDto.setFirstName(user.getFirstName());
            updateDto.setLastName(user.getLastName());
            updateDto.setEmail(user.getEmail());
            updateDto.setPhoneNumber(user.getPhoneNumber());
            
            userService.updateUser(userId, updateDto);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }
}
