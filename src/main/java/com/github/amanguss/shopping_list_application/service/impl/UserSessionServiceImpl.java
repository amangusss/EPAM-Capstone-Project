package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.dto.userSession.UserSessionCreateDto;
import com.github.amanguss.shopping_list_application.dto.userSession.UserSessionResponseDto;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.UserSession;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.repository.UserRepository;
import com.github.amanguss.shopping_list_application.repository.UserSessionRepository;
import com.github.amanguss.shopping_list_application.service.UserSessionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository userSessionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserSessionResponseDto createSession(Integer userId, UserSessionCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserSession session = new UserSession();
        session.setSessionToken(UUID.randomUUID().toString());
        session.setLoginTime(LocalDateTime.now());
        session.setLastActivityTime(LocalDateTime.now());
        session.setIpAddress(dto.getIpAddress());
        session.setUserAgent(dto.getUserAgent());
        session.setIsActive(true);
        session.setUser(user);

        UserSession saved = userSessionRepository.save(session);
        return mapToResponseDto(saved);
    }

    @Override
    public UserSessionResponseDto getSessionById(Integer id) {
        UserSession session = userSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        return mapToResponseDto(session);
    }

    @Override
    public UserSessionResponseDto getSessionByToken(String token) {
        UserSession session = userSessionRepository.findBySessionTokenAndIsActiveTrue(token)
                .orElseThrow(() -> new ResourceNotFoundException("Active session not found"));
        return mapToResponseDto(session);
    }

    @Override
    public List<UserSessionResponseDto> getActiveSessionsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userSessionRepository.findByUserAndIsActiveTrueOrderByLoginTimeDesc(user)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSessionResponseDto> getAllSessionsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userSessionRepository.findByUserOrderByLoginTimeDesc(user)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserSessionResponseDto updateLastActivity(String token) {
        UserSession session = userSessionRepository.findBySessionTokenAndIsActiveTrue(token)
                .orElseThrow(() -> new ResourceNotFoundException("Active session not found"));

        session.setLastActivityTime(LocalDateTime.now());
        session.setLastModifiedDate(LocalDateTime.now());
        UserSession saved = userSessionRepository.save(session);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public void logoutSession(String token) {
        UserSession session = userSessionRepository.findBySessionTokenAndIsActiveTrue(token)
                .orElseThrow(() -> new ResourceNotFoundException("Active session not found"));

        session.setIsActive(false);
        session.setLogoutTime(LocalDateTime.now());
        userSessionRepository.save(session);
    }

    @Override
    @Transactional
    public void logoutAllUserSessions(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userSessionRepository.deactivateAllUserSessions(user, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deactivateExpiredSessions(LocalDateTime cutoffTime) {
        userSessionRepository.deactivateExpiredSessions(cutoffTime, LocalDateTime.now());
    }

    @Override
    public Long countActiveSessionsByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userSessionRepository.countByUserAndIsActiveTrue(user);
    }

    @Override
    public List<Object[]> getBrowserStatistics() {
        return userSessionRepository.getBrowserStatistics();
    }

    private UserSessionResponseDto mapToResponseDto(UserSession session) {
        return new UserSessionResponseDto(
                session.getId(),
                session.getSessionToken(),
                session.getLoginTime(),
                session.getLogoutTime(),
                session.getLastActivityTime(),
                session.getLastModifiedDate(),
                session.getIpAddress(),
                session.getUserAgent(),
                session.getIsActive(),
                session.getUser().getFirstName() + " " + session.getUser().getLastName(),
                session.getUser().getId()
        );
    }
}