package com.github.amanguss.shopping_list_application.repository;

import com.github.amanguss.shopping_list_application.entity.Budget;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.enums.Period;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    Optional<Budget> findByShoppingList(ShoppingList shoppingList);
    List<Budget> findByIsActiveTrueOrderByCreationDateDesc();
    List<Budget> findByShoppingListOwnerIdAndIsActiveTrueOrderByCreationDateDesc(Integer ownerId);
    List<Budget> findByPeriodOrderByCreationDateDesc(Period period);
    List<Budget> findByCurrencyOrderByCreationDateDesc(String currency);

    @Query("SELECT b FROM Budget b JOIN b.shoppingList sl JOIN sl.items i " +
            "WHERE b.isActive = true " +
            "GROUP BY b " +
            "HAVING SUM(CASE WHEN i.isPurchased = true THEN i.actualPrice * i.quantity ELSE 0 END) > b.limit")
    List<Budget> findOverBudgetLists();
}
