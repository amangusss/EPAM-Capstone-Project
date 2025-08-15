package com.github.amanguss.shopping_list_application.repository;

import com.github.amanguss.shopping_list_application.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByIsSystemCategoryTrue();
    List<Category> findByIsSystemCategoryFalse();
    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT c FROM Category c ORDER BY c.displayOrder ASC, c.name ASC")
    List<Category> findAllOrderByDisplayOrderAndName();

    @Query("SELECT c FROM Category c WHERE SIZE(c.items) > 0 ORDER BY c.name ASC")
    List<Category> findCategoriesWithItems();

    @Query("SELECT COUNT(i) FROM Item i WHERE i.category = :category")
    Integer countItemsByCategory(@Param("category") Category category);
}