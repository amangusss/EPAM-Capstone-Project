package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.item.ItemCreateDto;
import com.github.amanguss.shopping_list_application.dto.item.ItemResponseDto;
import com.github.amanguss.shopping_list_application.entity.Category;
import com.github.amanguss.shopping_list_application.entity.Item;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;
import com.github.amanguss.shopping_list_application.repository.CategoryRepository;
import com.github.amanguss.shopping_list_application.repository.ItemRepository;
import com.github.amanguss.shopping_list_application.repository.ShoppingListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private ShoppingList shoppingList;
    private Category category;
    private ItemCreateDto createDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("Groceries");

        shoppingList = new ShoppingList();
        shoppingList.setId(1);
        shoppingList.setName("Test List");

        item = new Item();
        item.setId(1);
        item.setName("Milk");
        item.setDescription("Fresh milk");
        item.setQuantity(1.0);
        item.setEstimatedPrice(3.50);
        item.setPriority(PriorityLevel.MEDIUM);
        item.setIsPurchased(false);
        item.setAddedDate(LocalDateTime.now());
        item.setShoppingList(shoppingList);
        item.setCategory(category);

        createDto = new ItemCreateDto();
        createDto.setName("Milk");
        createDto.setDescription("Fresh milk");
        createDto.setQuantity(1.0);
        createDto.setEstimatedPrice(3.50);
        createDto.setPriority(PriorityLevel.MEDIUM);
        createDto.setCategoryId(1);
    }

    @Test
    void createItem_Success() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = itemService.createItem(1, createDto);

        assertNotNull(result);
        assertEquals("Milk", result.getName());
        assertEquals("Fresh milk", result.getDescription());
        assertEquals(1.0, result.getQuantity());
        assertEquals(3.50, result.getEstimatedPrice());
        assertFalse(result.getIsPurchased());

        verify(shoppingListRepository).findById(1);
        verify(categoryRepository).findById(1);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void markAsPurchased_Success() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = itemService.markAsPurchased(1, 3.75);

        assertNotNull(result);
        verify(itemRepository).findById(1);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void calculateTotalSpent_Success() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(itemRepository.calculateTotalSpentByShoppingList(shoppingList)).thenReturn(25.50);

        Double result = itemService.calculateTotalSpent(1);

        assertEquals(25.50, result);
        verify(shoppingListRepository).findById(1);
        verify(itemRepository).calculateTotalSpentByShoppingList(shoppingList);
    }
}