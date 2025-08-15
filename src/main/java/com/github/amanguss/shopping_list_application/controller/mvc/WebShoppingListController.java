package com.github.amanguss.shopping_list_application.controller.mvc;

import com.github.amanguss.shopping_list_application.dto.item.ItemResponseDto;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListCreateDto;
import com.github.amanguss.shopping_list_application.dto.item.ItemCreateDto;
import com.github.amanguss.shopping_list_application.service.UserService;
import com.github.amanguss.shopping_list_application.service.ShoppingListService;
import com.github.amanguss.shopping_list_application.service.ItemService;
import com.github.amanguss.shopping_list_application.service.CategoryService;
import com.github.amanguss.shopping_list_application.service.ListShareService;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;

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
public class WebShoppingListController {

    private final UserService userService;
    private final ShoppingListService shoppingListService;
    private final ItemService itemService;
    private final CategoryService categoryService;
    private final ListShareService listShareService;

    @GetMapping("/lists")
    public String listIndex(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var lists = shoppingListService.getShoppingListsByOwner(userId);
            var sharedLists = listShareService.getSharedShoppingListsByUser(userId);
            
            model.addAttribute("user", user);
            model.addAttribute("lists", lists);
            model.addAttribute("sharedLists", sharedLists);
            return "lists/lists";
        } catch (ResourceNotFoundException e) {
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/dashboard?error=An error occurred while loading lists";
        }
    }

    @GetMapping("/lists/create")
    public String createListForm(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("list", new ShoppingListCreateDto());
            return "lists/list-create";
        } catch (ResourceNotFoundException e) {
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/dashboard?error=An error occurred while loading create form";
        }
    }

    @PostMapping("/lists/create")
    public String createList(@Valid @ModelAttribute("list") ShoppingListCreateDto dto,
                             BindingResult result,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "lists/list-create";
        }

        try {
            var created = shoppingListService.createShoppingList(dto, userId);
            redirectAttributes.addFlashAttribute("success", "List created successfully!");
            return "redirect:/lists/" + created.getId();
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "User not found: " + e.getMessage());
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create list: " + e.getMessage());
            return "lists/list-create";
        }
    }

    @GetMapping("/lists/{id}")
    public String viewList(@PathVariable Integer id,
                           HttpSession session,
                           Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var list = shoppingListService.getShoppingListById(id);
            var items = itemService.getItemsByShoppingList(id);
            var categories = categoryService.getAllCategories();

            model.addAttribute("user", user);
            model.addAttribute("list", list);
            model.addAttribute("items", items);
            model.addAttribute("categories", categories);
            model.addAttribute("newItem", new ItemCreateDto());

            double totalEstimated = items.stream()
                    .mapToDouble(i -> i.getEstimatedPrice() != null ? i.getEstimatedPrice() * i.getQuantity() : 0)
                    .sum();
            double totalSpent = items.stream()
                    .filter(ItemResponseDto::getIsPurchased)
                    .mapToDouble(i -> i.getActualPrice() != null ? i.getActualPrice() * i.getQuantity() : 0)
                    .sum();

            model.addAttribute("totalEstimated", totalEstimated);
            model.addAttribute("totalSpent", totalSpent);
            model.addAttribute("completionPercent",
                    items.isEmpty() ? 0 : (items.stream().filter(ItemResponseDto::getIsPurchased).count() * 100.0 / items.size()));

            return "lists/list-detail";
        } catch (ResourceNotFoundException e) {
            return "redirect:/lists?error=List not found or you don't have access to it";
        } catch (Exception e) {
            return "redirect:/lists?error=An error occurred while loading the list";
        }
    }

    @GetMapping("/lists/{id}/edit")
    public String editListForm(@PathVariable Integer id,
                               HttpSession session,
                               Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var user = userService.getUserById(userId);
            var list = shoppingListService.getShoppingListById(id);
            
            if (!list.getOwnerId().equals(userId)) {
                return "redirect:/lists/" + id;
            }

            model.addAttribute("user", user);
            model.addAttribute("list", list);
            model.addAttribute("editList", new ShoppingListCreateDto());
            return "lists/list-edit";
        } catch (ResourceNotFoundException e) {
            return "redirect:/lists?error=List not found or you don't have access to it";
        } catch (Exception e) {
            return "redirect:/lists?error=An error occurred while loading edit form";
        }
    }

    @PostMapping("/lists/{id}/edit")
    public String editList(@PathVariable Integer id,
                           @Valid @ModelAttribute("editList") ShoppingListCreateDto dto,
                           BindingResult result,
                           HttpSession session,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "lists/list-edit";
        }

        try {
            var list = shoppingListService.getShoppingListById(id);
            
            if (!list.getOwnerId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this list");
                return "redirect:/lists/" + id;
            }

            shoppingListService.updateShoppingList(id, dto);
            redirectAttributes.addFlashAttribute("success", "List updated successfully!");
            return "redirect:/lists/" + id;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "List not found: " + e.getMessage());
            return "redirect:/lists";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update list: " + e.getMessage());
            return "redirect:/lists/" + id;
        }
    }

    @PostMapping("/lists/{listId}/items")
    public String addItem(@PathVariable Integer listId,
                          @Valid @ModelAttribute("newItem") ItemCreateDto dto,
                          BindingResult result,
                          HttpSession session,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("itemError", "Please fill all required fields");
            return "redirect:/lists/" + listId;
        }

        try {
            itemService.createItem(listId, dto);
            redirectAttributes.addFlashAttribute("success", "Item added successfully!");
            return "redirect:/lists/" + listId;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "List or category not found: " + e.getMessage());
            return "redirect:/lists/" + listId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add item: " + e.getMessage());
            return "redirect:/lists/" + listId;
        }
    }

    @PostMapping("/lists/{id}/status")
    public String toggleListStatus(@PathVariable Integer id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            var list = shoppingListService.getShoppingListById(id);
            
            if (!list.getOwnerId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to modify this list");
                return "redirect:/lists/" + id;
            }

            if (list.getStatus() == ListStatus.ACTIVE) {
                shoppingListService.updateShoppingListStatus(id, ListStatus.ARCHIVED);
                redirectAttributes.addFlashAttribute("success", "List marked as completed!");
            } else if (list.getStatus() == ListStatus.ARCHIVED) {
                shoppingListService.updateShoppingListStatus(id, ListStatus.ACTIVE);
                redirectAttributes.addFlashAttribute("success", "List reactivated successfully!");
            }

            return "redirect:/lists/" + id;
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "List not found: " + e.getMessage());
            return "redirect:/lists";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update list status: " + e.getMessage());
            return "redirect:/lists/" + id;
        }
    }
}
