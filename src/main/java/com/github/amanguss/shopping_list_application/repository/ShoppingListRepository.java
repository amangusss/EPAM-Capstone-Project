package com.github.amanguss.shopping_list_application.repository;

import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Integer> {

    List<ShoppingList> findByOwnerOrderByCreationDateDesc(User owner);
    List<ShoppingList> findByOwnerAndStatusOrderByCreationDateDesc(User owner, ListStatus status);
    List<ShoppingList> findByOwnerAndIsTemplateTrueOrderByCreationDateDesc(User owner);
    boolean existsByNameAndOwner(String name, User owner);

    @Query("SELECT sl FROM ShoppingList sl WHERE sl.owner = :owner " +
            "AND LOWER(sl.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY sl.creationDate DESC")
    List<ShoppingList> findByOwnerAndNameContainingIgnoreCaseOrderByCreationDateDesc(@Param("owner") User owner, @Param("name") String name);

    @Query("SELECT DISTINCT sl FROM ShoppingList sl " +
            "LEFT JOIN sl.shares s " +
            "WHERE sl.owner = :user OR (s.sharedTo = :user AND s.isActive = true) " +
            "ORDER BY sl.creationDate DESC")
    List<ShoppingList> findAccessibleLists(@Param("user") User user);

    @Query("SELECT COUNT(i) FROM Item i WHERE i.shoppingList = :shoppingList")
    Integer countItemsByShoppingList(@Param("shoppingList") ShoppingList shoppingList);

    @Query("SELECT COUNT(i) FROM Item i WHERE i.shoppingList = :shoppingList AND i.isPurchased = true")
    Integer countPurchasedItemsByShoppingList(@Param("shoppingList") ShoppingList shoppingList);
}