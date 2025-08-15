package com.github.amanguss.shopping_list_application.repository;

import com.github.amanguss.shopping_list_application.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u ORDER BY u.lastName ASC, u.firstName ASC")
    List<User> findAllOrderByName();

    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT COUNT(sl) FROM ShoppingList sl WHERE sl.owner = :user")
    Integer countShoppingListsByUser(@Param("user") User user);
}