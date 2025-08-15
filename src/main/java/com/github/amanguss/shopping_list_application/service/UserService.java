package com.github.amanguss.shopping_list_application.service;

import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.dto.user.UserResponseDto;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserCreateDto dto);
    UserResponseDto getUserById(Integer id);
    UserResponseDto getUserByEmail(String email);
    List<UserResponseDto> getAllUsers();
    List<UserResponseDto> getUsersByName(String name);
    UserResponseDto updateUser(Integer id, UserCreateDto dto);
    void deleteUser(Integer id);
    boolean existsByEmail(String email);
    UserResponseDto activateUser(Integer id);
    UserResponseDto deactivateUser(Integer id);
    boolean verifyPassword(Integer userId, String password);
    void changePassword(Integer userId, String newPassword);
    void resetPassword(String email, String newPassword);
}