package com.github.amanguss.shopping_list_application.service;

import com.github.amanguss.shopping_list_application.dto.item.ItemCreateDto;
import com.github.amanguss.shopping_list_application.dto.item.ItemResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;

import java.util.List;

public interface ItemService {

    ItemResponseDto createItem(Integer shoppingListId, ItemCreateDto dto);
    ItemResponseDto getItemById(Integer id);
    List<ItemResponseDto> getItemsByShoppingList(Integer shoppingListId);
    List<ItemResponseDto> getPurchasedItemsByShoppingList(Integer shoppingListId);
    List<ItemResponseDto> getUnpurchasedItemsByShoppingList(Integer shoppingListId);
    List<ItemResponseDto> getItemsByCategory(Integer categoryId);
    List<ItemResponseDto> getItemsByPriority(PriorityLevel priority);
    List<ItemResponseDto> getAllItemsByUser(Integer userId);
    ItemResponseDto updateItem(Integer id, ItemCreateDto dto);
    ItemResponseDto markAsPurchased(Integer id, Double actualPrice);
    ItemResponseDto markAsUnpurchased(Integer id);
    ItemResponseDto updateItemPriority(Integer id, PriorityLevel priority);
    void deleteItem(Integer id);
    void deleteAllPurchasedItems(Integer shoppingListId);
    Double calculateTotalSpent(Integer shoppingListId);
    Double calculateEstimatedTotal(Integer shoppingListId);
}
