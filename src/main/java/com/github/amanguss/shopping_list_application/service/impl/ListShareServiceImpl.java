package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.listShare.ListShareCreateDto;
import com.github.amanguss.shopping_list_application.dto.listShare.ListShareResponseDto;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListResponseDto;
import com.github.amanguss.shopping_list_application.entity.ListShare;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.Permission;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
import com.github.amanguss.shopping_list_application.repository.ListShareRepository;
import com.github.amanguss.shopping_list_application.repository.ShoppingListRepository;
import com.github.amanguss.shopping_list_application.repository.UserRepository;
import com.github.amanguss.shopping_list_application.service.ListShareService;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListShareServiceImpl implements ListShareService {

    private final ListShareRepository listShareRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ListShareResponseDto createShare(ListShareCreateDto dto, Integer sharedByUserId) {
        ShoppingList shoppingList = shoppingListRepository.findById(dto.getShoppingListId())
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        User sharedBy = userRepository.findById(sharedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Sharing user not found"));

        User sharedTo;

        if (dto.getSharedToEmail() != null && !dto.getSharedToEmail().trim().isEmpty()) {
            sharedTo = userRepository.findByEmail(dto.getSharedToEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User with email " + dto.getSharedToEmail() + " not found"));
        } else if (dto.getSharedToUserId() != null) {
            sharedTo = userRepository.findById(dto.getSharedToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));
        } else {
            throw new ValidationException("Either sharedToEmail or sharedToUserId must be provided");
        }

        if (!shoppingList.getOwner().getId().equals(sharedByUserId)) {
            throw new ValidationException("Only the owner can share this list");
        }

        if (sharedBy.getId().equals(sharedTo.getId())) {
            throw new ValidationException("Cannot share list with yourself");
        }

        if (listShareRepository.findByShoppingListAndSharedToAndIsActiveTrue(shoppingList, sharedTo).isPresent()) {
            throw new ValidationException("List is already shared with this user");
        }

        ListShare listShare = new ListShare();
        listShare.setShoppingList(shoppingList);
        listShare.setSharedBy(sharedBy);
        listShare.setSharedTo(sharedTo);
        listShare.setPermission(dto.getPermission());
        listShare.setSharedDate(LocalDateTime.now());
        listShare.setExpirationDate(dto.getExpirationDate());
        listShare.setIsActive(true);

        ListShare saved = listShareRepository.save(listShare);
        return mapToResponseDto(saved);
    }

    @Override
    public ListShareResponseDto getShareById(Integer id) {
        ListShare listShare = listShareRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Share not found"));
        return mapToResponseDto(listShare);
    }

    @Override
    public List<ListShareResponseDto> getSharesByShoppingList(Integer shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        return listShareRepository.findByShoppingListAndIsActiveTrueOrderBySharedDateDesc(shoppingList)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ListShareResponseDto> getSharesReceivedByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return listShareRepository.findBySharedToAndIsActiveTrueOrderBySharedDateDesc(user)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ListShareResponseDto> getSharesSentByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return listShareRepository.findBySharedByAndIsActiveTrueOrderBySharedDateDesc(user)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ListShareResponseDto> getSharesByPermission(Permission permission) {
        return listShareRepository.findByPermissionAndIsActiveTrueOrderBySharedDateDesc(permission)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ListShareResponseDto updateSharePermission(Integer shareId, Permission permission) {
        ListShare listShare = listShareRepository.findById(shareId)
                .orElseThrow(() -> new ResourceNotFoundException("Share not found"));

        listShare.setPermission(permission);
        ListShare saved = listShareRepository.save(listShare);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public void revokeShare(Integer shareId) {
        ListShare listShare = listShareRepository.findById(shareId)
                .orElseThrow(() -> new ResourceNotFoundException("Share not found"));

        listShareRepository.delete(listShare);
    }

    @Override
    @Transactional
    public void expireShare(Integer shareId) {
        ListShare listShare = listShareRepository.findById(shareId)
                .orElseThrow(() -> new ResourceNotFoundException("Share not found"));

        listShare.setExpirationDate(LocalDateTime.now());
        listShare.setIsActive(false);
        listShareRepository.save(listShare);
    }

    @Override
    public boolean hasAccess(Integer shoppingListId, Integer userId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return listShareRepository.hasAccess(shoppingList, user);
    }

    @Override
    public boolean hasEditAccess(Integer shoppingListId, Integer userId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return listShareRepository.hasEditAccess(shoppingList, user);
    }

    @Override
    public Long countSharedListsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return listShareRepository.countSharedListsByUser(user);
    }

    @Override
    @Transactional
    public void cleanupExpiredShares() {
        LocalDateTime now = LocalDateTime.now();
        List<ListShare> expiredShares = listShareRepository.findByExpirationDateBeforeAndIsActiveTrue(now);
        
        expiredShares.forEach(share -> {
            share.setIsActive(false);
            listShareRepository.save(share);
        });
    }

    @Override
    public List<ShoppingListResponseDto> getSharedShoppingListsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ListShare> activeShares = listShareRepository.findBySharedToAndIsActiveTrueOrderBySharedDateDesc(user);
        
        return activeShares.stream()
                .map(share -> {
                    ShoppingList shoppingList = share.getShoppingList();
                    return ShoppingListResponseDto.builder()
                            .id(shoppingList.getId())
                            .name(shoppingList.getName())
                            .description(shoppingList.getDescription())
                            .creationDate(shoppingList.getCreationDate())
                            .lastModifiedDate(shoppingList.getLastModifiedDate())
                            .status(shoppingList.getStatus())
                            .isTemplate(shoppingList.getIsTemplate())
                            .priority(shoppingList.getPriority())
                            .ownerName(shoppingList.getOwner().getFirstName() + " " + shoppingList.getOwner().getLastName())
                            .ownerId(shoppingList.getOwner().getId())
                            .totalItems(shoppingListRepository.countItemsByShoppingList(shoppingList))
                            .purchasedItems(shoppingListRepository.countPurchasedItemsByShoppingList(shoppingList))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ListShareResponseDto mapToResponseDto(ListShare listShare) {
        return new ListShareResponseDto(
                listShare.getId(),
                listShare.getPermission(),
                listShare.getSharedDate(),
                listShare.getExpirationDate(),
                listShare.getIsActive(),
                listShare.getShoppingList().getName(),
                listShare.getSharedBy().getFirstName() + " " + listShare.getSharedBy().getLastName(),
                listShare.getSharedTo().getFirstName() + " " + listShare.getSharedTo().getLastName(),
                listShare.getShoppingList().getId(),
                listShare.getSharedBy().getId(),
                listShare.getSharedTo().getId()
        );
    }
}