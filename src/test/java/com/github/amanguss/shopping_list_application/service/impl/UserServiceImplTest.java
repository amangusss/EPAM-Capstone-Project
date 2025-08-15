package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.dto.user.UserResponseDto;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserCreateDto userCreateDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setRegistrationDate(LocalDateTime.now());
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setIsVerified(false);

        userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName("John");
        userCreateDto.setLastName("Doe");
        userCreateDto.setEmail("john.doe@example.com");
        userCreateDto.setPassword("password123");
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.countShoppingListsByUser(any(User.class))).thenReturn(0);

        UserResponseDto result = userService.createUser(userCreateDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals(AccountStatus.ACTIVE, result.getAccountStatus());
        assertFalse(result.getIsVerified());

        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsValidationException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.createUser(userCreateDto));

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.countShoppingListsByUser(any(User.class))).thenReturn(5);

        UserResponseDto result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals(5, result.getShoppingListCount());

        verify(userRepository).findById(1);
    }

    @Test
    void getUserById_UserNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(1));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(1);
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(1));

        verify(userRepository).existsById(1);
        verify(userRepository).deleteById(1);
    }

    @Test
    void existsByEmail_True() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("john.doe@example.com");

        assertTrue(result);
        verify(userRepository).existsByEmail("john.doe@example.com");
    }
}