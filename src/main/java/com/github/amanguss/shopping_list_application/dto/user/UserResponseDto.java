package com.github.amanguss.shopping_list_application.dto.user;

import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginDate;
    private AccountStatus accountStatus;
    private Boolean isVerified;
    private Integer shoppingListCount;
}
