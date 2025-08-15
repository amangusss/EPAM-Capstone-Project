package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.item.ItemCreateDto;
import com.github.amanguss.shopping_list_application.dto.item.ItemResponseDto;
import com.github.amanguss.shopping_list_application.entity.Category;
import com.github.amanguss.shopping_list_application.entity.Item;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.repository.CategoryRepository;
import com.github.amanguss.shopping_list_application.repository.ItemRepository;
import com.github.amanguss.shopping_list_application.repository.ShoppingListRepository;
import com.github.amanguss.shopping_list_application.service.ItemService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ItemResponseDto createItem(Integer shoppingListId, ItemCreateDto dto) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1.0);
        item.setUnitOfMeasure(dto.getUnitOfMeasure());
        item.setEstimatedPrice(dto.getEstimatedPrice());
        item.setPriority(dto.getPriority() != null ? dto.getPriority() : PriorityLevel.MEDIUM);
        item.setNotes(dto.getNotes());
        item.setIsPurchased(false);
        item.setAddedDate(LocalDateTime.now());
        item.setShoppingList(shoppingList);
        item.setCategory(category);

        Item saved = itemRepository.save(item);
        return mapToResponseDto(saved);
    }

    @Override
    public ItemResponseDto getItemById(Integer id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        return mapToResponseDto(item);
    }

    @Override
    public List<ItemResponseDto> getItemsByShoppingList(Integer shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        return itemRepository.findByShoppingListOrderByAddedDateDesc(shoppingList)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> getPurchasedItemsByShoppingList(Integer shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        return itemRepository.findByShoppingListAndIsPurchasedOrderByAddedDateDesc(shoppingList, true)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> getUnpurchasedItemsByShoppingList(Integer shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        return itemRepository.findByShoppingListAndIsPurchasedOrderByAddedDateDesc(shoppingList, false)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> getItemsByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return itemRepository.findByCategoryOrderByAddedDateDesc(category)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> getItemsByPriority(PriorityLevel priority) {
        return itemRepository.findByPriorityOrderByAddedDateDesc(priority)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> getAllItemsByUser(Integer userId) {
        return itemRepository.findByShoppingListOwnerIdOrderByAddedDateDesc(userId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Integer id, ItemCreateDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getQuantity() != null) item.setQuantity(dto.getQuantity());
        if (dto.getUnitOfMeasure() != null) item.setUnitOfMeasure(dto.getUnitOfMeasure());
        if (dto.getEstimatedPrice() != null) item.setEstimatedPrice(dto.getEstimatedPrice());
        if (dto.getPriority() != null) item.setPriority(dto.getPriority());
        if (dto.getNotes() != null) item.setNotes(dto.getNotes());
        item.setCategory(category);

        Item saved = itemRepository.save(item);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public ItemResponseDto markAsPurchased(Integer id, Double actualPrice) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        item.setIsPurchased(true);
        item.setActualPrice(actualPrice);
        item.setPurchasedDate(LocalDateTime.now());

        Item saved = itemRepository.save(item);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public ItemResponseDto markAsUnpurchased(Integer id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        item.setIsPurchased(false);
        item.setActualPrice(null);
        item.setPurchasedDate(null);

        Item saved = itemRepository.save(item);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public ItemResponseDto updateItemPriority(Integer id, PriorityLevel priority) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        item.setPriority(priority);
        Item saved = itemRepository.save(item);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteItem(Integer id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found");
        }
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAllPurchasedItems(Integer shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        List<Item> purchasedItems = itemRepository.findByShoppingListAndIsPurchasedOrderByAddedDateDesc(shoppingList, true);
        itemRepository.deleteAll(purchasedItems);
    }

    @Override
    public Double calculateTotalSpent(Integer shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        Double total = itemRepository.calculateTotalSpentByShoppingList(shoppingList);
        return total != null ? total : 0.0;
    }

    @Override
    public Double calculateEstimatedTotal(Integer shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        Double total = itemRepository.calculateEstimatedTotalByShoppingList(shoppingList);
        return total != null ? total : 0.0;
    }

    private ItemResponseDto mapToResponseDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitOfMeasure(),
                item.getEstimatedPrice(),
                item.getActualPrice(),
                item.getIsPurchased(),
                item.getPurchasedDate(),
                item.getAddedDate(),
                item.getPriority(),
                item.getNotes(),
                item.getCategory() != null ? item.getCategory().getName() : null,
                item.getCategory() != null ? item.getCategory().getId() : null,
                item.getShoppingList().getId()
        );
    }
}