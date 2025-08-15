package com.github.amanguss.shopping_list_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amanguss.shopping_list_application.controller.api.UserController;
import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.dto.user.UserResponseDto;
import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;
import com.github.amanguss.shopping_list_application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreateDto userCreateDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName("John");
        userCreateDto.setLastName("Doe");
        userCreateDto.setEmail("john.doe@example.com");
        userCreateDto.setPassword("password123");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1);
        userResponseDto.setFirstName("John");
        userResponseDto.setLastName("Doe");
        userResponseDto.setEmail("john.doe@example.com");
        userResponseDto.setRegistrationDate(LocalDateTime.now());
        userResponseDto.setAccountStatus(AccountStatus.ACTIVE);
        userResponseDto.setIsVerified(false);
        userResponseDto.setShoppingListCount(0);
    }

    @Test
    void createUser_ValidInput_ReturnsCreated() throws Exception {
        when(userService.createUser(any(UserCreateDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void createUser_InvalidInput_ReturnsBadRequest() throws Exception {
        UserCreateDto invalidDto = new UserCreateDto();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() throws Exception {
        when(userService.getUserById(1)).thenReturn(userResponseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getAllUsers_ReturnsUserList() throws Exception {
        List<UserResponseDto> users = Collections.singletonList(userResponseDto);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void updateUser_ValidInput_ReturnsUpdatedUser() throws Exception {
        when(userService.updateUser(anyInt(), any(UserCreateDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void deleteUser_ExistingUser_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void existsByEmail_ExistingEmail_ReturnsTrue() throws Exception {
        when(userService.existsByEmail(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/users/exists")
                        .param("email", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}