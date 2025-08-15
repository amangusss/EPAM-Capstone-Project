package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.budget.BudgetCreateDto;
import com.github.amanguss.shopping_list_application.dto.budget.BudgetResponseDto;
import com.github.amanguss.shopping_list_application.entity.Budget;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.enums.Period;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
import com.github.amanguss.shopping_list_application.repository.BudgetRepository;
import com.github.amanguss.shopping_list_application.repository.ItemRepository;
import com.github.amanguss.shopping_list_application.repository.ShoppingListRepository;
import com.github.amanguss.shopping_list_application.service.BudgetService;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BudgetResponseDto createBudget(Integer shoppingListId, BudgetCreateDto dto) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        if (budgetRepository.findByShoppingList(shoppingList).isPresent()) {
            throw new ValidationException("Budget already exists for this shopping list");
        }

        Budget budget = new Budget();
        budget.setLimit(dto.getLimit());
        budget.setCurrency(dto.getCurrency());
        budget.setPeriod(dto.getPeriod());
        budget.setCreationDate(LocalDateTime.now());
        budget.setIsActive(dto.getIsActive());
        budget.setShoppingList(shoppingList);

        Budget saved = budgetRepository.save(budget);

        shoppingList.setBudget(budget);
        budget.setShoppingList(shoppingList);

        return mapToResponseDto(saved);
    }

    @Override
    public BudgetResponseDto getBudgetById(Integer id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        return mapToResponseDto(budget);
    }

    @Override
    public BudgetResponseDto getBudgetByShoppingList(Integer shoppingListId) {
        ShoppingList shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        Budget budget = budgetRepository.findByShoppingList(shoppingList)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found for this shopping list"));

        return mapToResponseDto(budget);
    }

    @Override
    public List<BudgetResponseDto> getActiveBudgets() {
        return budgetRepository.findByIsActiveTrueOrderByCreationDateDesc()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetResponseDto> getBudgetsByPeriod(Period period) {
        return budgetRepository.findByPeriodOrderByCreationDateDesc(period)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetResponseDto> getBudgetsByCurrency(String currency) {
        return budgetRepository.findByCurrencyOrderByCreationDateDesc(currency)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetResponseDto> getOverBudgetLists() {
        return budgetRepository.findOverBudgetLists()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BudgetResponseDto> getBudgetsByUserId(Integer userId) {
        return budgetRepository.findByShoppingListOwnerIdAndIsActiveTrueOrderByCreationDateDesc(userId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BudgetResponseDto updateBudget(Integer id, BudgetCreateDto dto) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        budget.setLimit(dto.getLimit());
        budget.setCurrency(dto.getCurrency());
        budget.setPeriod(dto.getPeriod());
        budget.setIsActive(dto.getIsActive());

        Budget saved = budgetRepository.save(budget);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public BudgetResponseDto activateBudget(Integer id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        budget.setIsActive(true);
        Budget saved = budgetRepository.save(budget);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public BudgetResponseDto deactivateBudget(Integer id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        budget.setIsActive(false);
        Budget saved = budgetRepository.save(budget);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteBudget(Integer id) {
        if (!budgetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Budget not found");
        }
        budgetRepository.deleteById(id);
    }

    @Override
    public Double getCurrentSpent(Integer budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        Double spent = itemRepository.calculateTotalSpentByShoppingList(budget.getShoppingList());
        return spent != null ? spent : 0.0;
    }

    @Override
    public Double getRemainingBudget(Integer budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        Double currentSpent = itemRepository.calculateTotalSpentByShoppingList(budget.getShoppingList());
        currentSpent = currentSpent != null ? currentSpent : 0.0;

        return budget.getLimit() - currentSpent;
    }

    @Override
    public boolean isOverBudget(Integer budgetId) {
        return getRemainingBudget(budgetId) < 0;
    }

    private BudgetResponseDto mapToResponseDto(Budget budget) {
        Double currentSpent = itemRepository.calculateTotalSpentByShoppingList(budget.getShoppingList());
        currentSpent = currentSpent != null ? currentSpent : 0.0;
        Double remainingBudget = budget.getLimit() - currentSpent;

        return new BudgetResponseDto(
                budget.getId(),
                budget.getLimit(),
                budget.getCurrency(),
                budget.getPeriod(),
                budget.getCreationDate(),
                budget.getIsActive(),
                budget.getShoppingList().getId(),
                budget.getShoppingList().getName(),
                currentSpent,
                remainingBudget
        );
    }
}