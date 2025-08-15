package com.github.amanguss.shopping_list_application.dto.budget;

import com.github.amanguss.shopping_list_application.entity.enums.Period;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponseDto {

    private Integer id;
    private Double limit;
    private String currency;
    private Period period;
    private LocalDateTime creationDate;
    private Boolean isActive;
    private Integer shoppingListId;
    private String shoppingListName;
    private Double currentSpent;
    private Double remainingBudget;
}