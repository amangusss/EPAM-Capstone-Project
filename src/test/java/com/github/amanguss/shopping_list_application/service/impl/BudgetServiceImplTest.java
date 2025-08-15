package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.budget.BudgetCreateDto;
import com.github.amanguss.shopping_list_application.dto.budget.BudgetResponseDto;
import com.github.amanguss.shopping_list_application.entity.Budget;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.Period;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
import com.github.amanguss.shopping_list_application.repository.BudgetRepository;
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
class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private Budget budget;
    private ShoppingList shoppingList;
    private BudgetCreateDto createDto;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");

        shoppingList = new ShoppingList();
        shoppingList.setId(1);
        shoppingList.setName("Test List");
        shoppingList.setOwner(user);

        budget = new Budget();
        budget.setId(1);
        budget.setLimit(1000.0);
        budget.setCurrency("USD");
        budget.setPeriod(Period.MONTHLY);
        budget.setCreationDate(LocalDateTime.now());
        budget.setIsActive(true);
        budget.setShoppingList(shoppingList);

        createDto = new BudgetCreateDto();
        createDto.setLimit(1000.0);
        createDto.setCurrency("USD");
        createDto.setPeriod(Period.MONTHLY);
        createDto.setIsActive(true);
    }

    @Test
    void createBudget_Success() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(budgetRepository.findByShoppingList(shoppingList)).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);
        when(itemRepository.calculateTotalSpentByShoppingList(shoppingList)).thenReturn(500.0);

        BudgetResponseDto result = budgetService.createBudget(1, createDto);

        assertNotNull(result);
        assertEquals(1000.0, result.getLimit());
        assertEquals("USD", result.getCurrency());
        assertEquals(Period.MONTHLY, result.getPeriod());
        assertTrue(result.getIsActive());

        verify(shoppingListRepository).findById(1);
        verify(budgetRepository).findByShoppingList(shoppingList);
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void createBudget_ShoppingListNotFound_ThrowsResourceNotFoundException() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> budgetService.createBudget(1, createDto));

        assertEquals("Shopping list not found", exception.getMessage());
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void createBudget_BudgetAlreadyExists_ThrowsValidationException() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(budgetRepository.findByShoppingList(shoppingList)).thenReturn(Optional.of(budget));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> budgetService.createBudget(1, createDto));

        assertEquals("Budget already exists for this shopping list", exception.getMessage());
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void getCurrentSpent_Success() {
        when(budgetRepository.findById(1)).thenReturn(Optional.of(budget));
        when(itemRepository.calculateTotalSpentByShoppingList(shoppingList)).thenReturn(750.0);

        Double result = budgetService.getCurrentSpent(1);

        assertEquals(750.0, result);
        verify(budgetRepository, times(1)).findById(1);
        verify(itemRepository, times(1)).calculateTotalSpentByShoppingList(shoppingList);
    }

    @Test
    void getRemainingBudget_Success() {
        when(budgetRepository.findById(1)).thenReturn(Optional.of(budget));
        when(itemRepository.calculateTotalSpentByShoppingList(shoppingList)).thenReturn(300.0);

        Double result = budgetService.getRemainingBudget(1);

        assertEquals(700.0, result);
        verify(budgetRepository, times(1)).findById(1);
        verify(itemRepository, times(1)).calculateTotalSpentByShoppingList(shoppingList);
    }

    @Test
    void isOverBudget_True() {
        when(budgetRepository.findById(1)).thenReturn(Optional.of(budget));
        when(itemRepository.calculateTotalSpentByShoppingList(shoppingList)).thenReturn(1200.0);

        boolean result = budgetService.isOverBudget(1);

        assertTrue(result);
        verify(budgetRepository, times(1)).findById(1);
    }

    @Test
    void isOverBudget_False() {
        when(budgetRepository.findById(1)).thenReturn(Optional.of(budget));
        when(itemRepository.calculateTotalSpentByShoppingList(shoppingList)).thenReturn(800.0);

        boolean result = budgetService.isOverBudget(1);

        assertFalse(result);
        verify(budgetRepository, times(1)).findById(1);
    }

    @Test
    void getBudgetById_Success() {
        when(budgetRepository.findById(1)).thenReturn(Optional.of(budget));
        when(itemRepository.calculateTotalSpentByShoppingList(shoppingList)).thenReturn(400.0);

        BudgetResponseDto result = budgetService.getBudgetById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1000.0, result.getLimit());
        assertEquals(400.0, result.getCurrentSpent());
        assertEquals(600.0, result.getRemainingBudget());

        verify(budgetRepository, times(1)).findById(1);
    }

    @Test
    void getBudgetById_NotFound_ThrowsResourceNotFoundException() {
        when(budgetRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> budgetService.getBudgetById(1));

        assertEquals("Budget not found", exception.getMessage());
        verify(budgetRepository, times(1)).findById(1);
    }
}
