package com.github.amanguss.shopping_list_application.controller.api;

import com.github.amanguss.shopping_list_application.dto.listShare.ListShareCreateDto;
import com.github.amanguss.shopping_list_application.dto.listShare.ListShareResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.Permission;
import com.github.amanguss.shopping_list_application.service.ListShareService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/list-shares")
@RequiredArgsConstructor
public class ListShareController {

    private final ListShareService listShareService;

    @PostMapping
    public ResponseEntity<ListShareResponseDto> createShare(@Valid @RequestBody ListShareCreateDto dto, HttpSession session) {
        Integer sharedByUserId = (Integer) session.getAttribute("userId");
        if (sharedByUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        ListShareResponseDto created = listShareService.createShare(dto, sharedByUserId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListShareResponseDto> getShareById(@PathVariable Integer id) {
        ListShareResponseDto share = listShareService.getShareById(id);
        return ResponseEntity.ok(share);
    }

    @GetMapping("/shopping-list/{shoppingListId}")
    public ResponseEntity<List<ListShareResponseDto>> getSharesByShoppingList(@PathVariable Integer shoppingListId) {
        List<ListShareResponseDto> shares = listShareService.getSharesByShoppingList(shoppingListId);
        return ResponseEntity.ok(shares);
    }

    @GetMapping("/received/user/{userId}")
    public ResponseEntity<List<ListShareResponseDto>> getSharesReceivedByUser(@PathVariable Integer userId) {
        List<ListShareResponseDto> shares = listShareService.getSharesReceivedByUser(userId);
        return ResponseEntity.ok(shares);
    }

    @GetMapping("/sent/user/{userId}")
    public ResponseEntity<List<ListShareResponseDto>> getSharesSentByUser(@PathVariable Integer userId) {
        List<ListShareResponseDto> shares = listShareService.getSharesSentByUser(userId);
        return ResponseEntity.ok(shares);
    }

    @GetMapping("/permission/{permission}")
    public ResponseEntity<List<ListShareResponseDto>> getSharesByPermission(@PathVariable Permission permission) {
        List<ListShareResponseDto> shares = listShareService.getSharesByPermission(permission);
        return ResponseEntity.ok(shares);
    }

    @PutMapping("/{id}/permission")
    public ResponseEntity<ListShareResponseDto> updateSharePermission(@PathVariable Integer id, @RequestParam Permission permission) {
        ListShareResponseDto updated = listShareService.updateSharePermission(id, permission);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/revoke")
    public ResponseEntity<Void> revokeShare(@PathVariable Integer id) {
        listShareService.revokeShare(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/expire")
    public ResponseEntity<Void> expireShare(@PathVariable Integer id) {
        listShareService.expireShare(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/access")
    public ResponseEntity<Boolean> hasAccess(@RequestParam Integer shoppingListId, @RequestParam Integer userId) {
        boolean hasAccess = listShareService.hasAccess(shoppingListId, userId);
        return ResponseEntity.ok(hasAccess);
    }

    @GetMapping("/edit-access")
    public ResponseEntity<Boolean> hasEditAccess(@RequestParam Integer shoppingListId, @RequestParam Integer userId) {
        boolean hasEditAccess = listShareService.hasEditAccess(shoppingListId, userId);
        return ResponseEntity.ok(hasEditAccess);
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> countSharedListsByUser(@PathVariable Integer userId) {
        Long count = listShareService.countSharedListsByUser(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/cleanup-expired")
    public ResponseEntity<Void> cleanupExpiredShares() {
        listShareService.cleanupExpiredShares();
        return ResponseEntity.noContent().build();
    }
}