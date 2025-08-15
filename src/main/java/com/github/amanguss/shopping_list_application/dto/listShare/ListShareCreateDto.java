package com.github.amanguss.shopping_list_application.dto.listShare;

import com.github.amanguss.shopping_list_application.entity.enums.Permission;
import com.github.amanguss.shopping_list_application.validation.EitherUserIdOrEmail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EitherUserIdOrEmail
public class ListShareCreateDto {

    @NotNull(message = "Shopping list ID is required")
    private Integer shoppingListId;

    private Integer sharedToUserId;

    private String sharedToEmail;

    @NotNull(message = "Permission is required")
    private Permission permission;

    private LocalDateTime expirationDate;
}