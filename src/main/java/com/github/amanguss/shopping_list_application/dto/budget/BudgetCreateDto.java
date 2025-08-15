package com.github.amanguss.shopping_list_application.dto.budget;

import com.github.amanguss.shopping_list_application.entity.enums.Period;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetCreateDto {

    @NotNull(message = "Budget limit is required")
    @Positive(message = "Budget limit must be positive")
    private Double limit;

    @Size(max = 3, message = "Currency code must not exceed 3 characters")
    private String currency = "USD";

    @NotNull(message = "Budget period is required")
    private Period period;

    private Boolean isActive = true;
}

