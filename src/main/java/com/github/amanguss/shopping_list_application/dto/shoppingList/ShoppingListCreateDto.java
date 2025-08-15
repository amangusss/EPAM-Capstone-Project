package com.github.amanguss.shopping_list_application.dto.shoppingList;

import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListCreateDto {

    @NotBlank(message = "Shopping list name is required")
    @Size(max = 200, message = "Shopping list name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private ListStatus status;

    private Boolean isTemplate;

    private PriorityLevel priority;
}