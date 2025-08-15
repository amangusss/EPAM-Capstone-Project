package com.github.amanguss.shopping_list_application.service.impl;

import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.entity.enums.AccountStatus;
import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.dto.user.UserResponseDto;
import com.github.amanguss.shopping_list_application.exception.ValidationException;
import com.github.amanguss.shopping_list_application.exception.ResourceNotFoundException;
import com.github.amanguss.shopping_list_application.service.UserService;
import com.github.amanguss.shopping_list_application.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("User with this email already exists");
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setRegistrationDate(LocalDateTime.now());
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setIsVerified(false);

        User saved = userRepository.save(user);
        return mapToResponseDto(saved);
    }

    @Override
    public UserResponseDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponseDto(user);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAllOrderByName()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDto> getUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Integer id, UserCreateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("User with this email already exists");
        }

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getDateOfBirth() != null) user.setDateOfBirth(dto.getDateOfBirth());

        User saved = userRepository.save(user);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserResponseDto activateUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setAccountStatus(AccountStatus.ACTIVE);
        User saved = userRepository.save(user);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public UserResponseDto deactivateUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setAccountStatus(AccountStatus.INACTIVE);
        User saved = userRepository.save(user);
        return mapToResponseDto(saved);
    }

    private UserResponseDto mapToResponseDto(User user) {
        Integer shoppingListCount = userRepository.countShoppingListsByUser(user);

        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                user.getRegistrationDate(),
                user.getLastLoginDate(),
                user.getAccountStatus(),
                user.getIsVerified(),
                shoppingListCount
        );
    }

    @Override
    public boolean verifyPassword(Integer userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getPassword().equals(password);
    }

    @Override
    @Transactional
    public void changePassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
