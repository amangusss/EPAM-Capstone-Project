package com.github.amanguss.shopping_list_application.dto.shoppingList;

import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListResponseDto {

    private Integer id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime lastModifiedDate;
    private ListStatus status;
    private Boolean isTemplate;
    private PriorityLevel priority;
    private String ownerName;
    private Integer ownerId;
    private Integer totalItems;
    private Integer purchasedItems;
}