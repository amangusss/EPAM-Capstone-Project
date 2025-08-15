package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.category.CategoryCreateDto;
import com.github.amanguss.shopping_list_application.dto.category.CategoryResponseDto;
import com.github.amanguss.shopping_list_application.entity.Category;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
import com.github.amanguss.shopping_list_application.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryCreateDto createDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setName("Groceries");
        category.setDescription("Food items");
        category.setColor("#FF0000");
        category.setIsSystemCategory(false);
        category.setCreationDate(LocalDateTime.now());
        category.setDisplayOrder(1);

        createDto = new CategoryCreateDto();
        createDto.setName("Groceries");
        createDto.setDescription("Food items");
        createDto.setColor("#FF0000");
        createDto.setIsSystemCategory(false);
        createDto.setDisplayOrder(1);
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryRepository.countItemsByCategory(any(Category.class))).thenReturn(0);

        CategoryResponseDto result = categoryService.createCategory(createDto);

        assertNotNull(result);
        assertEquals("Groceries", result.getName());
        assertEquals("Food items", result.getDescription());
        assertEquals("#FF0000", result.getColor());
        assertFalse(result.getIsSystemCategory());

        verify(categoryRepository).existsByNameIgnoreCase("Groceries");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_NameAlreadyExists_ThrowsValidationException() {
        when(categoryRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> categoryService.createCategory(createDto));

        assertEquals("Category with this name already exists", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.countItemsByCategory(any(Category.class))).thenReturn(3);

        CategoryResponseDto result = categoryService.getCategoryById(1);

        assertNotNull(result);
        assertEquals(0, result.getId());
        assertEquals("Groceries", result.getName());
        assertEquals(3, result.getItemCount());

        verify(categoryRepository).findById(1);
    }

    @Test
    void deleteCategory_SystemCategory_ThrowsValidationException() {
        category.setIsSystemCategory(true);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> categoryService.deleteCategory(1));

        assertEquals("Cannot delete system category", exception.getMessage());
        verify(categoryRepository, never()).deleteById(1);
    }
}
