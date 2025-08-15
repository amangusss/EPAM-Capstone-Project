package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListCreateDto;
import com.github.amanguss.shopping_list_application.dto.shoppingList.ShoppingListResponseDto;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;
import com.github.amanguss.shopping_list_application.entity.enums.ListStatus;
import com.github.amanguss.shopping_list_application.entity.enums.PriorityLevel;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
import com.github.amanguss.shopping_list_application.repository.ShoppingListRepository;
import com.github.amanguss.shopping_list_application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceImplTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShoppingListServiceImpl shoppingListService;

    private User user;
    private ShoppingList shoppingList;
    private ShoppingListCreateDto createDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setAccountStatus(AccountStatus.ACTIVE);

        shoppingList = new ShoppingList();
        shoppingList.setId(1);
        shoppingList.setName("Test List");
        shoppingList.setDescription("Test Description");
        shoppingList.setCreationDate(LocalDateTime.now());
        shoppingList.setLastModifiedDate(LocalDateTime.now());
        shoppingList.setStatus(ListStatus.ACTIVE);
        shoppingList.setIsTemplate(false);
        shoppingList.setPriority(PriorityLevel.MEDIUM);
        shoppingList.setOwner(user);

        createDto = new ShoppingListCreateDto();
        createDto.setName("Test List");
        createDto.setDescription("Test Description");
        createDto.setStatus(ListStatus.ACTIVE);
        createDto.setIsTemplate(false);
        createDto.setPriority(PriorityLevel.MEDIUM);
    }

    @Test
    void createShoppingList_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(shoppingListRepository.existsByNameAndOwner(anyString(), any(User.class))).thenReturn(false);
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);
        when(shoppingListRepository.countItemsByShoppingList(any(ShoppingList.class))).thenReturn(0);
        when(shoppingListRepository.countPurchasedItemsByShoppingList(any(ShoppingList.class))).thenReturn(0);

        ShoppingListResponseDto result = shoppingListService.createShoppingList(createDto, 1);

        assertNotNull(result);
        assertEquals("Test List", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(ListStatus.ACTIVE, result.getStatus());
        assertFalse(result.getIsTemplate());

        verify(userRepository).findById(1);
        verify(shoppingListRepository).existsByNameAndOwner("Test List", user);
        verify(shoppingListRepository).save(any(ShoppingList.class));
    }

    @Test
    void createShoppingList_UserNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> shoppingListService.createShoppingList(createDto, 1));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(1);
        verify(shoppingListRepository, never()).save(any(ShoppingList.class));
    }

    @Test
    void createShoppingList_NameAlreadyExists_ThrowsValidationException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(shoppingListRepository.existsByNameAndOwner(anyString(), any(User.class))).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> shoppingListService.createShoppingList(createDto, 1));

        assertEquals("Shopping list with this name already exists", exception.getMessage());
        verify(shoppingListRepository, never()).save(any(ShoppingList.class));
    }

    @Test
    void getShoppingListById_Success() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.countItemsByShoppingList(any(ShoppingList.class))).thenReturn(5);
        when(shoppingListRepository.countPurchasedItemsByShoppingList(any(ShoppingList.class))).thenReturn(2);

        ShoppingListResponseDto result = shoppingListService.getShoppingListById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test List", result.getName());
        assertEquals(5, result.getTotalItems());
        assertEquals(2, result.getPurchasedItems());

        verify(shoppingListRepository).findById(1);
    }

    @Test
    void getShoppingListsByOwner_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(shoppingListRepository.findByOwnerOrderByCreationDateDesc(user)).thenReturn(Collections.singletonList(shoppingList));
        when(shoppingListRepository.countItemsByShoppingList(any(ShoppingList.class))).thenReturn(0);
        when(shoppingListRepository.countPurchasedItemsByShoppingList(any(ShoppingList.class))).thenReturn(0);

        List<ShoppingListResponseDto> result = shoppingListService.getShoppingListsByOwner(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test List", result.get(0).getName());

        verify(userRepository).findById(1);
        verify(shoppingListRepository).findByOwnerOrderByCreationDateDesc(user);
    }

    @Test
    void duplicateShoppingList_Success() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.existsByNameAndOwner("New List", user)).thenReturn(false);
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);
        when(shoppingListRepository.countItemsByShoppingList(any(ShoppingList.class))).thenReturn(0);
        when(shoppingListRepository.countPurchasedItemsByShoppingList(any(ShoppingList.class))).thenReturn(0);

        ShoppingListResponseDto result = shoppingListService.duplicateShoppingList(1, "New List");

        assertNotNull(result);
        verify(shoppingListRepository).findById(1);
        verify(shoppingListRepository).save(any(ShoppingList.class));
    }

    @Test
    void deleteShoppingList_Success() {
        when(shoppingListRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> shoppingListService.deleteShoppingList(1));

        verify(shoppingListRepository).existsById(1);
        verify(shoppingListRepository).deleteById(1);
    }
}