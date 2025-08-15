package com.github.amanguss.shopping_list_application.dto.item;

import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDto {

    @NotBlank(message = "Item name is required")
    @Size(max = 200, message = "Item name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Positive(message = "Quantity must be positive")
    private Double quantity = 1.0;

    @Size(max = 50, message = "Unit of measure must not exceed 50 characters")
    private String unitOfMeasure;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private Double estimatedPrice;

    private PriorityLevel priority;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;
}