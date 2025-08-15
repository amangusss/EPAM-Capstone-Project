package com.github.amanguss.shopping_list_application.controller.api;

import com.github.amanguss.shopping_list_application.dto.category.CategoryCreateDto;
import com.github.amanguss.shopping_list_application.dto.category.CategoryResponseDto;
import com.github.amanguss.shopping_list_application.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryCreateDto dto) {
        CategoryResponseDto created = categoryService.createCategory(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Integer id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/system")
    public ResponseEntity<List<CategoryResponseDto>> getSystemCategories() {
        List<CategoryResponseDto> categories = categoryService.getSystemCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/user")
    public ResponseEntity<List<CategoryResponseDto>> getUserCategories() {
        List<CategoryResponseDto> categories = categoryService.getUserCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/with-items")
    public ResponseEntity<List<CategoryResponseDto>> getCategoriesWithItems() {
        List<CategoryResponseDto> categories = categoryService.getCategoriesWithItems();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryCreateDto dto) {
        CategoryResponseDto updated = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
        boolean exists = categoryService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}