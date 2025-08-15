package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListCreateDto;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListResponseDto;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
import com.github.amanguss.shopping_list_application.repository.ShoppingListRepository;
import com.github.amanguss.shopping_list_application.repository.UserRepository;
import com.github.amanguss.shopping_list_application.service.ShoppingListService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ShoppingListResponseDto createShoppingList(ShoppingListCreateDto dto, Integer ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (shoppingListRepository.existsByNameAndOwner(dto.getName(), owner)) {
            throw new ValidationException("Shopping list with this name already exists");
        }

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(dto.getName());
        shoppingList.setDescription(dto.getDescription());
        shoppingList.setCreationDate(LocalDateTime.now());
        shoppingList.setLastModifiedDate(LocalDateTime.now());
        shoppingList.setStatus(dto.getStatus() != null ? dto.getStatus() : ListStatus.ACTIVE);
        shoppingList.setIsTemplate(dto.getIsTemplate() != null ? dto.getIsTemplate() : false);
        shoppingList.setPriority(dto.getPriority() != null ? dto.getPriority() : PriorityLevel.MEDIUM);
        shoppingList.setOwner(owner);

        ShoppingList saved = shoppingListRepository.save(shoppingList);
        return mapToResponseDto(saved);
    }

    @Override
    public ShoppingListResponseDto getShoppingListById(Integer id) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));
        return mapToResponseDto(shoppingList);
    }

    @Override
    public List<ShoppingListResponseDto> getShoppingListsByOwner(Integer ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return shoppingListRepository.findByOwnerOrderByCreationDateDesc(owner)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShoppingListResponseDto> getShoppingListsByOwnerAndStatus(Integer ownerId, ListStatus status) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return shoppingListRepository.findByOwnerAndStatusOrderByCreationDateDesc(owner, status)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShoppingListResponseDto> getTemplatesByOwner(Integer ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return shoppingListRepository.findByOwnerAndIsTemplateTrueOrderByCreationDateDesc(owner)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShoppingListResponseDto> getAccessibleLists(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return shoppingListRepository.findAccessibleLists(user)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShoppingListResponseDto> searchShoppingListsByName(Integer ownerId, String name) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return shoppingListRepository.findByOwnerAndNameContainingIgnoreCaseOrderByCreationDateDesc(owner, name)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShoppingListResponseDto updateShoppingList(Integer id, ShoppingListCreateDto dto) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        if (!shoppingList.getName().equals(dto.getName()) &&
                shoppingListRepository.existsByNameAndOwner(dto.getName(), shoppingList.getOwner())) {
            throw new ValidationException("Shopping list with this name already exists");
        }

        shoppingList.setName(dto.getName());
        shoppingList.setDescription(dto.getDescription());
        shoppingList.setLastModifiedDate(LocalDateTime.now());
        if (dto.getStatus() != null) shoppingList.setStatus(dto.getStatus());
        if (dto.getPriority() != null) shoppingList.setPriority(dto.getPriority());

        ShoppingList saved = shoppingListRepository.save(shoppingList);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public ShoppingListResponseDto updateShoppingListStatus(Integer id, ListStatus status) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        shoppingList.setStatus(status);
        shoppingList.setLastModifiedDate(LocalDateTime.now());

        ShoppingList saved = shoppingListRepository.save(shoppingList);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public ShoppingListResponseDto updateShoppingListPriority(Integer id, PriorityLevel priority) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        shoppingList.setPriority(priority);
        shoppingList.setLastModifiedDate(LocalDateTime.now());

        ShoppingList saved = shoppingListRepository.save(shoppingList);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteShoppingList(Integer id) {
        if (!shoppingListRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shopping list not found");
        }
        shoppingListRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ShoppingListResponseDto duplicateShoppingList(Integer id, String newName) {
        ShoppingList original = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        if (shoppingListRepository.existsByNameAndOwner(newName, original.getOwner())) {
            throw new ValidationException("Shopping list with this name already exists");
        }

        ShoppingList duplicate = new ShoppingList();
        duplicate.setName(newName);
        duplicate.setDescription(original.getDescription());
        duplicate.setCreationDate(LocalDateTime.now());
        duplicate.setLastModifiedDate(LocalDateTime.now());
        duplicate.setStatus(ListStatus.ACTIVE);
        duplicate.setIsTemplate(false);
        duplicate.setPriority(original.getPriority());
        duplicate.setOwner(original.getOwner());

        ShoppingList saved = shoppingListRepository.save(duplicate);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public ShoppingListResponseDto createFromTemplate(Integer templateId, String newName) {
        ShoppingList template = shoppingListRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));

        if (!template.getIsTemplate()) {
            throw new ValidationException("Specified list is not a template");
        }

        if (shoppingListRepository.existsByNameAndOwner(newName, template.getOwner())) {
            throw new ValidationException("Shopping list with this name already exists");
        }

        ShoppingList newList = new ShoppingList();
        newList.setName(newName);
        newList.setDescription(template.getDescription());
        newList.setCreationDate(LocalDateTime.now());
        newList.setLastModifiedDate(LocalDateTime.now());
        newList.setStatus(ListStatus.ACTIVE);
        newList.setIsTemplate(false);
        newList.setPriority(template.getPriority());
        newList.setOwner(template.getOwner());

        ShoppingList saved = shoppingListRepository.save(newList);
        return mapToResponseDto(saved);
    }

    @Override
    public boolean existsByNameAndOwner(String name, Integer ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return shoppingListRepository.existsByNameAndOwner(name, owner);
    }

    private ShoppingListResponseDto mapToResponseDto(ShoppingList shoppingList) {
        Integer totalItems = shoppingListRepository.countItemsByShoppingList(shoppingList);
        Integer purchasedItems = shoppingListRepository.countPurchasedItemsByShoppingList(shoppingList);

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
                .totalItems(totalItems)
                .purchasedItems(purchasedItems)
                .build();
    }
}