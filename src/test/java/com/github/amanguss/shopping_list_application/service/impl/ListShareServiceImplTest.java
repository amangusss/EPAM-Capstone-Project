package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.listShare.ListShareCreateDto;
import com.github.amanguss.shopping_list_application.dto.listShare.ListShareResponseDto;
import com.github.amanguss.shopping_list_application.entity.ListShare;
import com.github.amanguss.shopping_list_application.entity.ShoppingList;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.Permission;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
import com.github.amanguss.shopping_list_application.repository.ListShareRepository;
import com.github.amanguss.shopping_list_application.repository.ShoppingListRepository;
import com.github.amanguss.shopping_list_application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListShareServiceImplTest {

    @Mock
    private ListShareRepository listShareRepository;

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListShareServiceImpl listShareService;

    private User owner;
    private User sharedToUser;
    private ShoppingList shoppingList;
    private ListShare listShare;
    private ListShareCreateDto createDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setFirstName("Owner");
        owner.setLastName("User");

        sharedToUser = new User();
        sharedToUser.setId(2);
        sharedToUser.setFirstName("Shared");
        sharedToUser.setLastName("User");

        shoppingList = new ShoppingList();
        shoppingList.setId(1);
        shoppingList.setName("Test List");
        shoppingList.setOwner(owner);

        listShare = new ListShare();
        listShare.setId(1);
        listShare.setShoppingList(shoppingList);
        listShare.setSharedBy(owner);
        listShare.setSharedTo(sharedToUser);
        listShare.setPermission(Permission.VIEW);
        listShare.setSharedDate(LocalDateTime.now());
        listShare.setIsActive(true);

        createDto = new ListShareCreateDto();
        createDto.setShoppingListId(1);
        createDto.setSharedToUserId(2);
        createDto.setPermission(Permission.VIEW);
    }

    @Test
    void createShare_Success() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(userRepository.findById(2)).thenReturn(Optional.of(sharedToUser));
        when(listShareRepository.findByShoppingListAndSharedToAndIsActiveTrue(shoppingList, sharedToUser))
                .thenReturn(Optional.empty());
        when(listShareRepository.save(any(ListShare.class))).thenReturn(listShare);

        ListShareResponseDto result = listShareService.createShare(createDto, 1);

        assertNotNull(result);
        assertEquals(Permission.VIEW, result.getPermission());
        assertTrue(result.getIsActive());

        verify(shoppingListRepository).findById(1);
        verify(userRepository).findById(1);
        verify(userRepository).findById(2);
        verify(listShareRepository).save(any(ListShare.class));
    }

    @Test
    void createShare_NotOwner_ThrowsValidationException() {
        User notOwner = new User();
        notOwner.setId(3);
        shoppingList.setOwner(notOwner);

        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(userRepository.findById(2)).thenReturn(Optional.of(sharedToUser));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> listShareService.createShare(createDto, 1));

        assertEquals("Only the owner can share this list", exception.getMessage());
        verify(listShareRepository, never()).save(any(ListShare.class));
    }

    @Test
    void createShare_SelfShare_ThrowsValidationException() {
        createDto.setSharedToUserId(1); // Same as sharedBy

        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> listShareService.createShare(createDto, 1));

        assertEquals("Cannot share list with yourself", exception.getMessage());
        verify(listShareRepository, never()).save(any(ListShare.class));
    }

    @Test
    void createShare_AlreadyShared_ThrowsValidationException() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(userRepository.findById(2)).thenReturn(Optional.of(sharedToUser));
        when(listShareRepository.findByShoppingListAndSharedToAndIsActiveTrue(shoppingList, sharedToUser))
                .thenReturn(Optional.of(listShare));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> listShareService.createShare(createDto, 1));

        assertEquals("List is already shared with this user", exception.getMessage());
        verify(listShareRepository, never()).save(any(ListShare.class));
    }

    @Test
    void hasAccess_True() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(userRepository.findById(2)).thenReturn(Optional.of(sharedToUser));
        when(listShareRepository.hasAccess(shoppingList, sharedToUser)).thenReturn(true);

        boolean result = listShareService.hasAccess(1, 2);

        assertTrue(result);
        verify(listShareRepository).hasAccess(shoppingList, sharedToUser);
    }

    @Test
    void hasEditAccess_True() {
        when(shoppingListRepository.findById(1)).thenReturn(Optional.of(shoppingList));
        when(userRepository.findById(2)).thenReturn(Optional.of(sharedToUser));
        when(listShareRepository.hasEditAccess(shoppingList, sharedToUser)).thenReturn(true);

        boolean result = listShareService.hasEditAccess(1, 2);

        assertTrue(result);
        verify(listShareRepository).hasEditAccess(shoppingList, sharedToUser);
    }

    @Test
    void revokeShare_Success() {
        when(listShareRepository.findById(1)).thenReturn(Optional.of(listShare));

        assertDoesNotThrow(() -> listShareService.revokeShare(1));

        verify(listShareRepository).findById(1);
        verify(listShareRepository).save(listShare);
        assertFalse(listShare.getIsActive());
    }
}