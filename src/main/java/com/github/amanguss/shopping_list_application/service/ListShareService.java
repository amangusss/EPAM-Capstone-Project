package com.github.amanguss.shopping_list_application.service;

import com.github.amanguss.shopping_list_application.dto.listShare.ListShareCreateDto;
import com.github.amanguss.shopping_list_application.dto.listShare.ListShareResponseDto;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.Permission;

import java.util.List;

public interface ListShareService {

    ListShareResponseDto createShare(ListShareCreateDto dto, Integer sharedByUserId);
    ListShareResponseDto getShareById(Integer id);
    List<ListShareResponseDto> getSharesByShoppingList(Integer shoppingListId);
    List<ListShareResponseDto> getSharesReceivedByUser(Integer userId);
    List<ListShareResponseDto> getSharesSentByUser(Integer userId);
    List<ListShareResponseDto> getSharesByPermission(Permission permission);
    ListShareResponseDto updateSharePermission(Integer shareId, Permission permission);
    void revokeShare(Integer shareId);
    void expireShare(Integer shareId);
    boolean hasAccess(Integer shoppingListId, Integer userId);
    boolean hasEditAccess(Integer shoppingListId, Integer userId);
    Long countSharedListsByUser(Integer userId);
    void cleanupExpiredShares();
    List<ShoppingListResponseDto> getSharedShoppingListsByUser(Integer userId);
}