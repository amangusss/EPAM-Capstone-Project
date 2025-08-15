package com.github.amanguss.shopping_list_application.controller.api;

import com.github.amanguss.shopping_list_application.dto.userSession.UserSessionCreateDto;
import com.github.amanguss.shopping_list_application.dto.userSession.UserSessionResponseDto;
import com.github.amanguss.shopping_list_application.service.UserSessionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user-sessions")
@RequiredArgsConstructor
public class UserSessionController {

    private final UserSessionService userSessionService;

    @PostMapping
    public ResponseEntity<UserSessionResponseDto> createSession(@RequestParam Integer userId, @Valid @RequestBody UserSessionCreateDto dto) {
        UserSessionResponseDto created = userSessionService.createSession(userId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSessionResponseDto> getSessionById(@PathVariable Integer id) {
        UserSessionResponseDto session = userSessionService.getSessionById(id);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<UserSessionResponseDto> getSessionByToken(@PathVariable String token) {
        UserSessionResponseDto session = userSessionService.getSessionByToken(token);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/active/user/{userId}")
    public ResponseEntity<List<UserSessionResponseDto>> getActiveSessionsByUser(@PathVariable Integer userId) {
        List<UserSessionResponseDto> sessions = userSessionService.getActiveSessionsByUser(userId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/all/user/{userId}")
    public ResponseEntity<List<UserSessionResponseDto>> getAllSessionsByUser(@PathVariable Integer userId) {
        List<UserSessionResponseDto> sessions = userSessionService.getAllSessionsByUser(userId);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/update-activity")
    public ResponseEntity<UserSessionResponseDto> updateLastActivity(@RequestParam String token) {
        UserSessionResponseDto updated = userSessionService.updateLastActivity(token);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutSession(@RequestParam String token) {
        userSessionService.logoutSession(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout-all/user/{userId}")
    public ResponseEntity<Void> logoutAllUserSessions(@PathVariable Integer userId) {
        userSessionService.logoutAllUserSessions(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deactivate-expired")
    public ResponseEntity<Void> deactivateExpiredSessions(
            @RequestParam(defaultValue = "24") int hoursAgo) {

        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hoursAgo);
        userSessionService.deactivateExpiredSessions(cutoffTime);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> countActiveSessionsByUser(@PathVariable Integer userId) {
        Long count = userSessionService.countActiveSessionsByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/browser-statistics")
    public ResponseEntity<List<Object[]>> getBrowserStatistics() {
        List<Object[]> statistics = userSessionService.getBrowserStatistics();
        return ResponseEntity.ok(statistics);
    }
}