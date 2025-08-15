package com.github.amanguss.shopping_list_application.repository;

import com.github.amanguss.shopping_list_application.entity.ListShare;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.Permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListShareRepository extends JpaRepository<ListShare, Integer> {

    List<ListShare> findByShoppingListAndIsActiveTrueOrderBySharedDateDesc(ShoppingList shoppingList);
    List<ListShare> findBySharedToAndIsActiveTrueOrderBySharedDateDesc(User sharedTo);
    List<ListShare> findBySharedByAndIsActiveTrueOrderBySharedDateDesc(User sharedBy);
    Optional<ListShare> findByShoppingListAndSharedToAndIsActiveTrue(ShoppingList shoppingList, User sharedTo);
    List<ListShare> findByExpirationDateBeforeAndIsActiveTrue(LocalDateTime now);
    List<ListShare> findByPermissionAndIsActiveTrueOrderBySharedDateDesc(Permission permission);

    @Query("SELECT COUNT(ls) > 0 FROM ListShare ls " +
            "WHERE ls.shoppingList = :list AND ls.sharedTo = :user AND ls.isActive = true")
    boolean hasAccess(@Param("list") ShoppingList list, @Param("user") User user);

    @Query("SELECT COUNT(ls) > 0 FROM ListShare ls " +
            "WHERE ls.shoppingList = :list AND ls.sharedTo = :user " +
            "AND ls.isActive = true AND (ls.permission = 'EDIT' OR ls.permission = 'ADMIN')")
    boolean hasEditAccess(@Param("list") ShoppingList list, @Param("user") User user);

    @Query("SELECT COUNT(DISTINCT ls.shoppingList) FROM ListShare ls WHERE ls.sharedBy = :user AND ls.isActive = true")
    Long countSharedListsByUser(@Param("user") User user);
}