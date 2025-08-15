package com.github.amanguss.shopping_list_application.controller.api;

import com.github.amanguss.shopping_list_application.dto.budget.BudgetCreateDto;
import com.github.amanguss.shopping_list_application.dto.budget.BudgetResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.Period;
import com.github.amanguss.shopping_list_application.service.BudgetService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponseDto> createBudget(@RequestParam Integer shoppingListId, @Valid @RequestBody BudgetCreateDto dto) {
        BudgetResponseDto created = budgetService.createBudget(shoppingListId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> getBudgetById(@PathVariable Integer id) {
        BudgetResponseDto budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    @GetMapping("/shopping-list/{shoppingListId}")
    public ResponseEntity<BudgetResponseDto> getBudgetByShoppingList(@PathVariable Integer shoppingListId) {
        BudgetResponseDto budget = budgetService.getBudgetByShoppingList(shoppingListId);
        return ResponseEntity.ok(budget);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BudgetResponseDto>> getActiveBudgets() {
        List<BudgetResponseDto> budgets = budgetService.getActiveBudgets();
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/period/{period}")
    public ResponseEntity<List<BudgetResponseDto>> getBudgetsByPeriod(@PathVariable Period period) {
        List<BudgetResponseDto> budgets = budgetService.getBudgetsByPeriod(period);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/currency/{currency}")
    public ResponseEntity<List<BudgetResponseDto>> getBudgetsByCurrency(@PathVariable String currency) {
        List<BudgetResponseDto> budgets = budgetService.getBudgetsByCurrency(currency);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/over-budget")
    public ResponseEntity<List<BudgetResponseDto>> getOverBudgetLists() {
        List<BudgetResponseDto> budgets = budgetService.getOverBudgetLists();
        return ResponseEntity.ok(budgets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> updateBudget(@PathVariable Integer id, @Valid @RequestBody BudgetCreateDto dto) {
        BudgetResponseDto updated = budgetService.updateBudget(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<BudgetResponseDto> activateBudget(@PathVariable Integer id) {
        BudgetResponseDto budget = budgetService.activateBudget(id);
        return ResponseEntity.ok(budget);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<BudgetResponseDto> deactivateBudget(@PathVariable Integer id) {
        BudgetResponseDto budget = budgetService.deactivateBudget(id);
        return ResponseEntity.ok(budget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Integer id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/current-spent")
    public ResponseEntity<Double> getCurrentSpent(@PathVariable Integer id) {
        Double spent = budgetService.getCurrentSpent(id);
        return ResponseEntity.ok(spent);
    }

    @GetMapping("/{id}/remaining")
    public ResponseEntity<Double> getRemainingBudget(@PathVariable Integer id) {
        Double remaining = budgetService.getRemainingBudget(id);
        return ResponseEntity.ok(remaining);
    }

    @GetMapping("/{id}/is-over-budget")
    public ResponseEntity<Boolean> isOverBudget(@PathVariable Integer id) {
        boolean isOver = budgetService.isOverBudget(id);
        return ResponseEntity.ok(isOver);
    }
}