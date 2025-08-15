package com.github.amanguss.shopping_list_application.controller.api;

import com.github.amanguss.shopping_list_application.dto.item.ItemCreateDto;
import com.github.amanguss.shopping_list_application.dto.item.ItemResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;
import com.github.amanguss.shopping_list_application.service.ItemService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestParam Integer shoppingListId, @Valid @RequestBody ItemCreateDto dto) {
        ItemResponseDto created = itemService.createItem(shoppingListId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Integer id) {
        ItemResponseDto item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/shopping-list/{shoppingListId}")
    public ResponseEntity<List<ItemResponseDto>> getItemsByShoppingList(@PathVariable Integer shoppingListId) {
        List<ItemResponseDto> items = itemService.getItemsByShoppingList(shoppingListId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/shopping-list/{shoppingListId}/purchased")
    public ResponseEntity<List<ItemResponseDto>> getPurchasedItemsByShoppingList(@PathVariable Integer shoppingListId) {
        List<ItemResponseDto> items = itemService.getPurchasedItemsByShoppingList(shoppingListId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/shopping-list/{shoppingListId}/unpurchased")
    public ResponseEntity<List<ItemResponseDto>> getUnpurchasedItemsByShoppingList(@PathVariable Integer shoppingListId) {
        List<ItemResponseDto> items = itemService.getUnpurchasedItemsByShoppingList(shoppingListId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ItemResponseDto>> getItemsByCategory(@PathVariable Integer categoryId) {
        List<ItemResponseDto> items = itemService.getItemsByCategory(categoryId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<ItemResponseDto>> getItemsByPriority(@PathVariable PriorityLevel priority) {
        List<ItemResponseDto> items = itemService.getItemsByPriority(priority);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable Integer id, @Valid @RequestBody ItemCreateDto dto) {
        ItemResponseDto updated = itemService.updateItem(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<ItemResponseDto> markAsPurchased(@PathVariable Integer id, @RequestParam Double actualPrice) {
        ItemResponseDto updated = itemService.markAsPurchased(id, actualPrice);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/unpurchase")
    public ResponseEntity<ItemResponseDto> markAsUnpurchased(@PathVariable Integer id) {
        ItemResponseDto updated = itemService.markAsUnpurchased(id);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/priority")
    public ResponseEntity<ItemResponseDto> updateItemPriority(@PathVariable Integer id, @RequestParam PriorityLevel priority) {
        ItemResponseDto updated = itemService.updateItemPriority(id, priority);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/shopping-list/{shoppingListId}/purchased")
    public ResponseEntity<Void> deleteAllPurchasedItems(@PathVariable Integer shoppingListId) {
        itemService.deleteAllPurchasedItems(shoppingListId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shopping-list/{shoppingListId}/total-spent")
    public ResponseEntity<Double> calculateTotalSpent(@PathVariable Integer shoppingListId) {
        Double total = itemService.calculateTotalSpent(shoppingListId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/shopping-list/{shoppingListId}/estimated-total")
    public ResponseEntity<Double> calculateEstimatedTotal(@PathVariable Integer shoppingListId) {
        Double total = itemService.calculateEstimatedTotal(shoppingListId);
        return ResponseEntity.ok(total);
    }
}