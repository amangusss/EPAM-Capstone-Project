package com.github.amanguss.shopping_list_application.service;

import com.github.amanguss.shopping_list_application.dto.category.CategoryCreateDto;
import com.github.amanguss.shopping_list_application.dto.category.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto createCategory(CategoryCreateDto dto);
    CategoryResponseDto getCategoryById(Integer id);
    List<CategoryResponseDto> getAllCategories();
    List<CategoryResponseDto> getSystemCategories();
    List<CategoryResponseDto> getUserCategories();
    List<CategoryResponseDto> getCategoriesWithItems();
    CategoryResponseDto updateCategory(Integer id, CategoryCreateDto dto);
    void deleteCategory(Integer id);
    boolean existsByName(String name);
}