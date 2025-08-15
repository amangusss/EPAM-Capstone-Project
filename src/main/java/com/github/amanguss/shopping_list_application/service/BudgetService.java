package com.github.amanguss.shopping_list_application.service;

import com.github.amanguss.shopping_list_application.dto.budget.BudgetCreateDto;
import com.github.amanguss.shopping_list_application.dto.budget.BudgetResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.Period;

import java.util.List;

public interface BudgetService {

    BudgetResponseDto createBudget(Integer shoppingListId, BudgetCreateDto dto);
    BudgetResponseDto getBudgetById(Integer id);
    BudgetResponseDto getBudgetByShoppingList(Integer shoppingListId);
    List<BudgetResponseDto> getActiveBudgets();
    List<BudgetResponseDto> getBudgetsByPeriod(Period period);
    List<BudgetResponseDto> getBudgetsByCurrency(String currency);
    List<BudgetResponseDto> getOverBudgetLists();
    List<BudgetResponseDto> getBudgetsByUserId(Integer userId);
    BudgetResponseDto updateBudget(Integer id, BudgetCreateDto dto);
    BudgetResponseDto activateBudget(Integer id);
    BudgetResponseDto deactivateBudget(Integer id);
    void deleteBudget(Integer id);
    Double getCurrentSpent(Integer budgetId);
    Double getRemainingBudget(Integer budgetId);
    boolean isOverBudget(Integer budgetId);
}
