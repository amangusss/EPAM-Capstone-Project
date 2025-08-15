package com.github.amanguss.shopping_list_application.dto.listShare;

import com.github.amanguss.shopping_list_application.entity.enums.Permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListShareResponseDto {

    private Integer id;
    private Permission permission;
    private LocalDateTime sharedDate;
    private LocalDateTime expirationDate;
    private Boolean isActive;
    private String shoppingListName;
    private String sharedByUserName;
    private String sharedToUserName;
    private Integer shoppingListId;
    private Integer sharedByUserId;
    private Integer sharedToUserId;
}