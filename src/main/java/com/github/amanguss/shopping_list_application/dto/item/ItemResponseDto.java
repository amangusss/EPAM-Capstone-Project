package com.github.amanguss.shopping_list_application.dto.item;

import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {

    private Integer id;
    private String name;
    private String description;
    private Double quantity;
    private String unitOfMeasure;
    private Double estimatedPrice;
    private Double actualPrice;
    private Boolean isPurchased;
    private LocalDateTime purchasedDate;
    private LocalDateTime addedDate;
    private PriorityLevel priority;
    private String notes;
    private String categoryName;
    private Integer categoryId;
    private Integer shoppingListId;
}