package com.github.amanguss.shopping_list_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.amanguss.shopping_list_application.controller.api.CategoryController;
import com.github.amanguss.shopping_list_application.dto.category.CategoryCreateDto;
import com.github.amanguss.shopping_list_application.dto.category.CategoryResponseDto;
import com.github.amanguss.shopping_list_application.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryCreateDto createDto;
    private CategoryResponseDto responseDto;

    @BeforeEach
    void setUp() {
        createDto = new CategoryCreateDto();
        createDto.setName("Groceries");
        createDto.setDescription("Food items");
        createDto.setColor("#FF0000");

        responseDto = new CategoryResponseDto();
        responseDto.setId(1);
        responseDto.setName("Groceries");
        responseDto.setDescription("Food items");
        responseDto.setColor("#FF0000");
        responseDto.setIsSystemCategory(false);
        responseDto.setCreationDate(LocalDateTime.now());
        responseDto.setDisplayOrder(1);
        responseDto.setItemCount(0);
    }

    @Test
    void createCategory_ValidInput_ReturnsCreated() throws Exception {
        when(categoryService.createCategory(any(CategoryCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Groceries"));
    }

    @Test
    void getCategoryById_ExistingCategory_ReturnsCategory() throws Exception {
        when(categoryService.getCategoryById(1)).thenReturn(responseDto);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Groceries"));
    }

    @Test
    void getAllCategories_ReturnsCategoryList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Groceries"));
    }
}
