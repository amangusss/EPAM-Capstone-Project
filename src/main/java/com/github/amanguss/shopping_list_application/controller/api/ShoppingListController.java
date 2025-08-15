package com.github.amanguss.shopping_list_application.controller.api;

import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListCreateDto;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;
import com.github.amanguss.shopping_list_application.service.ShoppingListService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @PostMapping
    public ResponseEntity<ShoppingListResponseDto> createShoppingList(@Valid @RequestBody ShoppingListCreateDto dto, @RequestParam Integer ownerId) {
        ShoppingListResponseDto created = shoppingListService.createShoppingList(dto, ownerId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingListResponseDto> getShoppingListById(@PathVariable Integer id) {
        ShoppingListResponseDto shoppingList = shoppingListService.getShoppingListById(id);
        return ResponseEntity.ok(shoppingList);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ShoppingListResponseDto>> getShoppingListsByOwner(@PathVariable Integer ownerId) {
        List<ShoppingListResponseDto> shoppingLists = shoppingListService.getShoppingListsByOwner(ownerId);
        return ResponseEntity.ok(shoppingLists);
    }

    @GetMapping("/owner/{ownerId}/status/{status}")
    public ResponseEntity<List<ShoppingListResponseDto>> getShoppingListsByOwnerAndStatus(@PathVariable Integer ownerId, @PathVariable ListStatus status) {
        List<ShoppingListResponseDto> shoppingLists = shoppingListService.getShoppingListsByOwnerAndStatus(ownerId, status);
        return ResponseEntity.ok(shoppingLists);
    }


    @GetMapping("/templates/owner/{ownerId}")
    public ResponseEntity<List<ShoppingListResponseDto>> getTemplatesByOwner(@PathVariable Integer ownerId) {
        List<ShoppingListResponseDto> templates = shoppingListService.getTemplatesByOwner(ownerId);
        return ResponseEntity.ok(templates);
    }

    @GetMapping("/accessible/{userId}")
    public ResponseEntity<List<ShoppingListResponseDto>> getAccessibleLists(@PathVariable Integer userId) {
        List<ShoppingListResponseDto> lists = shoppingListService.getAccessibleLists(userId);
        return ResponseEntity.ok(lists);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ShoppingListResponseDto>> searchShoppingListsByName(@RequestParam Integer ownerId, @RequestParam String name) {
        List<ShoppingListResponseDto> lists = shoppingListService.searchShoppingListsByName(ownerId, name);
        return ResponseEntity.ok(lists);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShoppingListResponseDto> updateShoppingList(@PathVariable Integer id, @Valid @RequestBody ShoppingListCreateDto dto) {
        ShoppingListResponseDto updated = shoppingListService.updateShoppingList(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ShoppingListResponseDto> updateShoppingListStatus(@PathVariable Integer id, @RequestParam ListStatus status) {
        ShoppingListResponseDto updated = shoppingListService.updateShoppingListStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/priority")
    public ResponseEntity<ShoppingListResponseDto> updateShoppingListPriority(@PathVariable Integer id, @RequestParam PriorityLevel priority) {
        ShoppingListResponseDto updated = shoppingListService.updateShoppingListPriority(id, priority);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShoppingList(@PathVariable Integer id) {
        shoppingListService.deleteShoppingList(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<ShoppingListResponseDto> duplicateShoppingList(@PathVariable Integer id, @RequestParam String newName) {
        ShoppingListResponseDto duplicated = shoppingListService.duplicateShoppingList(id, newName);
        return new ResponseEntity<>(duplicated, HttpStatus.CREATED);
    }

    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<ShoppingListResponseDto> createFromTemplate(@PathVariable Integer templateId, @RequestParam String newName) {
        ShoppingListResponseDto created = shoppingListService.createFromTemplate(templateId, newName);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByNameAndOwner(@RequestParam String name, @RequestParam Integer ownerId) {
        boolean exists = shoppingListService.existsByNameAndOwner(name, ownerId);
        return ResponseEntity.ok(exists);
    }
}