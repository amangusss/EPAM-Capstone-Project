package com.github.amanguss.shopping_list_application.repository;

import com.github.amanguss.shopping_list_application.entity.Category;
import com.github.amanguss.shopping_list_application.entity.Item;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByShoppingListOrderByAddedDateDesc(ShoppingList shoppingList);
    List<Item> findByShoppingListAndIsPurchasedOrderByAddedDateDesc(ShoppingList shoppingList, Boolean isPurchased);
    List<Item> findByCategoryOrderByAddedDateDesc(Category category);
    List<Item> findByPriorityOrderByAddedDateDesc(PriorityLevel priority);

    @Query("SELECT SUM(i.actualPrice * i.quantity) FROM Item i WHERE i.shoppingList = :shoppingList AND i.isPurchased = true")
    Double calculateTotalSpentByShoppingList(@Param("shoppingList") ShoppingList shoppingList);

    @Query("SELECT SUM(i.estimatedPrice * i.quantity) FROM Item i WHERE i.shoppingList = :shoppingList")
    Double calculateEstimatedTotalByShoppingList(@Param("shoppingList") ShoppingList shoppingList);

    @Query("SELECT i FROM Item i JOIN i.shoppingList sl WHERE sl.owner.id = :ownerId ORDER BY i.addedDate DESC")
    List<Item> findByShoppingListOwnerIdOrderByAddedDateDesc(@Param("ownerId") Integer ownerId);
}