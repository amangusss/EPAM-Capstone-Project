package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.category.CategoryCreateDto;
import com.github.amanguss.shopping_list_application.dto.category.CategoryResponseDto;
import com.github.amanguss.shopping_list_application.entity.Category;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
import com.github.amanguss.shopping_list_application.repository.CategoryRepository;
import com.github.amanguss.shopping_list_application.service.CategoryService;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryCreateDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ValidationException("Category with this name already exists");
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());
        category.setIsSystemCategory(dto.getIsSystemCategory() != null ? dto.getIsSystemCategory() : false);
        category.setCreationDate(LocalDateTime.now());
        category.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);

        Category saved = categoryRepository.save(category);
        return mapToResponseDto(saved);
    }

    @Override
    public CategoryResponseDto getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return mapToResponseDto(category);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAllOrderByDisplayOrderAndName()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDto> getSystemCategories() {
        return categoryRepository.findByIsSystemCategoryTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDto> getUserCategories() {
        return categoryRepository.findByIsSystemCategoryFalse()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDto> getCategoriesWithItems() {
        return categoryRepository.findCategoriesWithItems()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Integer id, CategoryCreateDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getName().equalsIgnoreCase(dto.getName()) &&
                categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ValidationException("Category with this name already exists");
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());
        category.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : category.getDisplayOrder());

        Category saved = categoryRepository.save(category);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (category.getIsSystemCategory()) {
            throw new ValidationException("Cannot delete system category");
        }

        Integer itemCount = categoryRepository.countItemsByCategory(category);
        if (itemCount > 0) {
            throw new ValidationException("Cannot delete category with existing items");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        Integer itemCount = categoryRepository.countItemsByCategory(category);

        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getColor(),
                category.getIsSystemCategory(),
                category.getCreationDate(),
                category.getDisplayOrder(),
                itemCount
        );
    }
}