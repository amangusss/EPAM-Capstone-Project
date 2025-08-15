package com.github.amanguss.shopping_list_application.service;

import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListCreateDto;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;

import java.util.List;

public interface ShoppingListService {

    ShoppingListResponseDto createShoppingList(ShoppingListCreateDto dto, Integer ownerId);
    ShoppingListResponseDto getShoppingListById(Integer id);
    List<ShoppingListResponseDto> getShoppingListsByOwner(Integer ownerId);
    List<ShoppingListResponseDto> getShoppingListsByOwnerAndStatus(Integer ownerId, ListStatus status);
    List<ShoppingListResponseDto> getTemplatesByOwner(Integer ownerId);
    List<ShoppingListResponseDto> getAccessibleLists(Integer userId);
    List<ShoppingListResponseDto> searchShoppingListsByName(Integer ownerId, String name);
    ShoppingListResponseDto updateShoppingList(Integer id, ShoppingListCreateDto dto);
    ShoppingListResponseDto updateShoppingListStatus(Integer id, ListStatus status);
    ShoppingListResponseDto updateShoppingListPriority(Integer id, PriorityLevel priority);
    void deleteShoppingList(Integer id);
    ShoppingListResponseDto duplicateShoppingList(Integer id, String newName);
    ShoppingListResponseDto createFromTemplate(Integer templateId, String newName);
    boolean existsByNameAndOwner(String name, Integer ownerId);
}
