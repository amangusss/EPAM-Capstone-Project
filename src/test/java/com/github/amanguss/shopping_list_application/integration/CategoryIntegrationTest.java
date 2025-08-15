package com.github.amanguss.shopping_list_application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amanguss.shopping_list_application.dto.category.CategoryCreateDto;
import com.github.amanguss.shopping_list_application.entity.Category;
import com.github.amanguss.shopping_list_application.repository.CategoryRepository;
import jakarta.transaction.Transactional;
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
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void categoryWorkflow_CreateRetrieveUpdate() throws Exception {
        CategoryCreateDto createDto = new CategoryCreateDto();
        createDto.setName("Electronics");
        createDto.setDescription("Electronic devices");
        createDto.setColor("#0000FF");
        createDto.setIsSystemCategory(false);
        createDto.setDisplayOrder(1);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronic devices"))
                .andExpect(jsonPath("$.color").value("#0000FF"));

        assertTrue(categoryRepository.existsByNameIgnoreCase("Electronics"));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/categories/exists")
                        .param("name", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void createCategory_DuplicateName_ReturnsBadRequest() throws Exception {
        Category existingCategory = new Category();
        existingCategory.setName("Food");
        existingCategory.setDescription("Food items");
        existingCategory.setCreationDate(LocalDateTime.now());
        existingCategory.setIsSystemCategory(false);
        categoryRepository.save(existingCategory);

        CategoryCreateDto duplicateDto = new CategoryCreateDto();
        duplicateDto.setName("Food");
        duplicateDto.setDescription("Different description");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isBadRequest());
    }
}