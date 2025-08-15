package com.github.amanguss.shopping_list_application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;
import com.github.amanguss.shopping_list_application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setRegistrationDate(LocalDateTime.now());
        testUser.setAccountStatus(AccountStatus.ACTIVE);
        testUser.setIsVerified(false);
        testUser = userRepository.save(testUser);
    }

    @Test
    void userWorkflow_CreateRetrieveUpdateDelete() throws Exception {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setFirstName("Jane");
        createDto.setLastName("Smith");
        createDto.setEmail("jane.smith@example.com");
        createDto.setPassword("password456");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(userRepository.existsByEmail("jane.smith@example.com"));

        mockMvc.perform(get("/api/users/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        UserCreateDto updateDto = new UserCreateDto();
        updateDto.setFirstName("John Updated");
        updateDto.setLastName("Doe Updated");
        updateDto.setEmail("john.updated@example.com");
        updateDto.setPassword("newpassword");

        mockMvc.perform(put("/api/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/users/" + testUser.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(testUser.getId()));
    }

    @Test
    void getUsersByName_ReturnsMatchingUsers() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void createUser_DuplicateEmail_ReturnsBadRequest() throws Exception {
        UserCreateDto duplicateDto = new UserCreateDto();
        duplicateDto.setFirstName("Another");
        duplicateDto.setLastName("User");
        duplicateDto.setEmail("john.doe@example.com");
        duplicateDto.setPassword("password789");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isBadRequest());
    }
}