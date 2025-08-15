package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.userSession.UserSessionCreateDto;
import com.github.amanguss.shopping_list_application.dto.userSession.UserSessionResponseDto;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.UserSession;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.repository.UserRepository;
import com.github.amanguss.shopping_list_application.repository.UserSessionRepository;
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
class UserSessionServiceImplTest {

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSessionServiceImpl userSessionService;

    private User user;
    private UserSession userSession;
    private UserSessionCreateDto createDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");

        userSession = new UserSession();
        userSession.setId(1);
        userSession.setSessionToken("test-token-123");
        userSession.setLoginTime(LocalDateTime.now());
        userSession.setLastActivityTime(LocalDateTime.now());
        userSession.setIpAddress("192.168.1.1");
        userSession.setUserAgent("Mozilla/5.0");
        userSession.setIsActive(true);
        userSession.setUser(user);

        createDto = new UserSessionCreateDto();
        createDto.setIpAddress("192.168.1.1");
        createDto.setUserAgent("Mozilla/5.0");
    }

    @Test
    void createSession_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userSessionRepository.save(any(UserSession.class))).thenReturn(userSession);

        UserSessionResponseDto result = userSessionService.createSession(1, createDto);

        assertNotNull(result);
        assertEquals("192.168.1.1", result.getIpAddress());
        assertEquals("Mozilla/5.0", result.getUserAgent());
        assertTrue(result.getIsActive());

        verify(userRepository).findById(1);
        verify(userSessionRepository).save(any(UserSession.class));
    }

    @Test
    void createSession_UserNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userSessionService.createSession(1, createDto));

        assertEquals("User not found", exception.getMessage());
        verify(userSessionRepository, never()).save(any(UserSession.class));
    }

    @Test
    void getSessionByToken_Success() {
        when(userSessionRepository.findBySessionTokenAndIsActiveTrue("test-token-123"))
                .thenReturn(Optional.of(userSession));

        UserSessionResponseDto result = userSessionService.getSessionByToken("test-token-123");

        assertNotNull(result);
        assertEquals("test-token-123", result.getSessionToken());
        assertTrue(result.getIsActive());

        verify(userSessionRepository).findBySessionTokenAndIsActiveTrue("test-token-123");
    }

    @Test
    void getSessionByToken_NotFound_ThrowsResourceNotFoundException() {
        when(userSessionRepository.findBySessionTokenAndIsActiveTrue("invalid-token"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userSessionService.getSessionByToken("invalid-token"));

        assertEquals("Active session not found", exception.getMessage());
    }

    @Test
    void updateLastActivity_Success() {
        when(userSessionRepository.findBySessionTokenAndIsActiveTrue("test-token-123"))
                .thenReturn(Optional.of(userSession));
        when(userSessionRepository.save(userSession)).thenReturn(userSession);

        UserSessionResponseDto result = userSessionService.updateLastActivity("test-token-123");

        assertNotNull(result);
        verify(userSessionRepository).findBySessionTokenAndIsActiveTrue("test-token-123");
        verify(userSessionRepository).save(userSession);
    }

    @Test
    void logoutSession_Success() {
        when(userSessionRepository.findBySessionTokenAndIsActiveTrue("test-token-123"))
                .thenReturn(Optional.of(userSession));

        assertDoesNotThrow(() -> userSessionService.logoutSession("test-token-123"));

        verify(userSessionRepository).findBySessionTokenAndIsActiveTrue("test-token-123");
        verify(userSessionRepository).save(userSession);
        assertFalse(userSession.getIsActive());
        assertNotNull(userSession.getLogoutTime());
    }

    @Test
    void logoutAllUserSessions_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userSessionService.logoutAllUserSessions(1));

        verify(userRepository).findById(1);
        verify(userSessionRepository).deactivateAllUserSessions(eq(user), any(LocalDateTime.class));
    }

    @Test
    void countActiveSessionsByUser_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userSessionRepository.countByUserAndIsActiveTrue(user)).thenReturn(3L);

        Long result = userSessionService.countActiveSessionsByUser(1);

        assertEquals(3L, result);
        verify(userRepository).findById(1);
        verify(userSessionRepository).countByUserAndIsActiveTrue(user);
    }
}
