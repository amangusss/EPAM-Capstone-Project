package com.github.amanguss.shopping_list_application.dto.userSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponseDto {

    private Integer id;
    private String sessionToken;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private LocalDateTime lastActivityTime;
    private LocalDateTime lastModifiedDate;
    private String ipAddress;
    private String userAgent;
    private Boolean isActive;
    private String userName;
    private Integer userId;
}