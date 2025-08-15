package com.github.amanguss.shopping_list_application.controller.api;

import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.dto.user.UserResponseDto;
import com.github.amanguss.shopping_list_application.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        UserResponseDto created = userService.createUser(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Integer id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        UserResponseDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> getUsersByName(@RequestParam String name) {
        List<UserResponseDto> users = userService.getUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer id, @Valid @RequestBody UserCreateDto dto) {
        UserResponseDto updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<UserResponseDto> activateUser(@PathVariable Integer id) {
        UserResponseDto user = userService.activateUser(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<UserResponseDto> deactivateUser(@PathVariable Integer id) {
        UserResponseDto user = userService.deactivateUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}