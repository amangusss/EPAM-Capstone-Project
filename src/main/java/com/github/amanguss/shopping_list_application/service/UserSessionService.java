package com.github.amanguss.shopping_list_application.service;

import com.github.amanguss.shopping_list_application.dto.userSession.UserSessionCreateDto;
import com.github.amanguss.shopping_list_application.dto.userSession.UserSessionResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSessionService {

    UserSessionResponseDto createSession(Integer userId, UserSessionCreateDto dto);
    UserSessionResponseDto getSessionById(Integer id);
    UserSessionResponseDto getSessionByToken(String token);
    List<UserSessionResponseDto> getActiveSessionsByUser(Integer userId);
    List<UserSessionResponseDto> getAllSessionsByUser(Integer userId);
    UserSessionResponseDto updateLastActivity(String token);
    void logoutSession(String token);
    void logoutAllUserSessions(Integer userId);
    void deactivateExpiredSessions(LocalDateTime cutoffTime);
    Long countActiveSessionsByUser(Integer userId);
    List<Object[]> getBrowserStatistics();
}
