package com.github.amanguss.shopping_list_application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListCreateDto;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;
import com.github.amanguss.shopping_list_application.repository.ShoppingListRepository;
import com.github.amanguss.shopping_list_application.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ShoppingListIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

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
    void shoppingListWorkflow_CreateRetrieveUpdate() throws Exception {
        ShoppingListCreateDto createDto = new ShoppingListCreateDto();
        createDto.setName("Grocery List");
        createDto.setDescription("Weekly groceries");
        createDto.setStatus(ListStatus.ACTIVE);
        createDto.setIsTemplate(false);
        createDto.setPriority(PriorityLevel.HIGH);

        mockMvc.perform(post("/api/shopping-lists")
                        .param("ownerId", testUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Grocery List"))
                .andExpect(jsonPath("$.description").value("Weekly groceries"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(shoppingListRepository.existsByNameAndOwner("Grocery List", testUser));

        mockMvc.perform(get("/api/shopping-lists/owner/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Grocery List"));
    }

    @Test
    void duplicateShoppingList_Success() throws Exception {
        ShoppingList originalList = new ShoppingList();
        originalList.setName("Original List");
        originalList.setDescription("Original description");
        originalList.setCreationDate(LocalDateTime.now());
        originalList.setLastModifiedDate(LocalDateTime.now());
        originalList.setStatus(ListStatus.ACTIVE);
        originalList.setIsTemplate(false);
        originalList.setPriority(PriorityLevel.MEDIUM);
        originalList.setOwner(testUser);
        originalList = shoppingListRepository.save(originalList);

        mockMvc.perform(post("/api/shopping-lists/" + originalList.getId() + "/duplicate")
                        .param("newName", "Duplicated List"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Duplicated List"));

        assertTrue(shoppingListRepository.existsByNameAndOwner("Duplicated List", testUser));
    }
}