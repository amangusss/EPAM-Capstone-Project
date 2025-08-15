package com.github.amanguss.shopping_list_application.dto.userSession;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionCreateDto {

    @NotBlank(message = "IP address is required")
    @Size(max = 45, message = "IP address must not exceed 45 characters")
    private String ipAddress;

    @Size(max = 500, message = "User agent must not exceed 500 characters")
    private String userAgent;
}
