package com.github.amanguss.shopping_list_application.controller;

import com.github.amanguss.shopping_list_application.controller.mvc.WebAuthController;
import com.github.amanguss.shopping_list_application.dto.user.UserCreateDto;
import com.github.amanguss.shopping_list_application.dto.user.UserResponseDto;
import com.github.amanguss.shopping_list_application.entity.User;
import com.github.amanguss.shopping_list_application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebAuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpSession session;

    @InjectMocks
    private WebAuthController webAuthController;

    private User testUser;
    private UserResponseDto testUserResponseDto;
    private UserCreateDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");

        testUserResponseDto = new UserResponseDto();
        testUserResponseDto.setId(1);
        testUserResponseDto.setFirstName("John");
        testUserResponseDto.setLastName("Doe");
        testUserResponseDto.setEmail("john.doe@example.com");

        testUserDto = new UserCreateDto();
        testUserDto.setFirstName("John");
        testUserDto.setLastName("Doe");
        testUserDto.setEmail("john.doe@example.com");
        testUserDto.setPassword("password123");
    }

    @Test
    void loginPage_ShouldReturnLoginView() {
        String result = webAuthController.loginPage(model);

        assertEquals("login", result);
    }

    @Test
    void registerPage_ShouldReturnRegisterViewWithUserModel() {
        String result = webAuthController.registerPage(model);

        assertEquals("register", result);
        verify(model).addAttribute("user", any(UserCreateDto.class));
    }

    @Test
    void register_WithValidData_ShouldRedirectToLogin() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.createUser(any(UserCreateDto.class))).thenReturn(testUserResponseDto);

        String result = webAuthController.register(testUserDto, bindingResult, redirectAttributes, model);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("success", "Registration successful! Please login.");
    }

    @Test
    void register_WithExistingEmail_ShouldReturnRegisterView() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.existsByEmail(anyString())).thenReturn(true);

        String result = webAuthController.register(testUserDto, bindingResult, redirectAttributes, model);

        assertEquals("register", result);
        verify(bindingResult).rejectValue("email", "error.user", "Email already exists");
    }

    @Test
    void register_WithValidationErrors_ShouldReturnRegisterView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = webAuthController.register(testUserDto, bindingResult, redirectAttributes, model);

        assertEquals("register", result);
    }

    @Test
    void register_WithServiceException_ShouldReturnRegisterView() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.createUser(any(UserCreateDto.class))).thenThrow(new RuntimeException("Service error"));

        String result = webAuthController.register(testUserDto, bindingResult, redirectAttributes, model);

        assertEquals("register", result);
        verify(redirectAttributes).addFlashAttribute("error", "Failed to register: Service error");
    }

    @Test
    void login_WithValidCredentials_ShouldRedirectToDashboard() {
        when(userService.getUserByEmail(anyString())).thenReturn(testUserResponseDto);

        String result = webAuthController.login("john.doe@example.com", "password123", session, redirectAttributes, model);

        assertEquals("redirect:/dashboard", result);
        verify(session).setAttribute("userId", testUserResponseDto.getId());
        verify(session).setAttribute("userName", "John Doe");
    }

    @Test
    void login_WithInvalidCredentials_ShouldRedirectToLogin() {
        when(userService.getUserByEmail(anyString())).thenThrow(new RuntimeException("User not found"));

        String result = webAuthController.login("invalid@example.com", "wrongpassword", session, redirectAttributes, model);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("error", "Invalid credentials");
    }

    @Test
    void logout_ShouldInvalidateSessionAndRedirectToLogin() {
        String result = webAuthController.logout(session, model);

        assertEquals("redirect:/login", result);
        verify(session).invalidate();
    }

    @Test
    void forgotPasswordPage_ShouldReturnForgotPasswordView() {
        String result = webAuthController.forgotPasswordPage(model);

        assertEquals("forgot-password", result);
    }

    @Test
    void forgotPassword_WithExistingEmail_ShouldShowSuccessMessage() {
        when(userService.existsByEmail(anyString())).thenReturn(true);

        String result = webAuthController.forgotPassword("john.doe@example.com", redirectAttributes, model);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("success", "Password reset instructions sent to your email.");
    }

    @Test
    void forgotPassword_WithNonExistingEmail_ShouldShowErrorMessage() {
        when(userService.existsByEmail(anyString())).thenReturn(false);

        String result = webAuthController.forgotPassword("nonexistent@example.com", redirectAttributes, model);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("error", "Email not found.");
    }

    @Test
    void resetPasswordPage_WithValidToken_ShouldReturnResetPasswordView() {
        String result = webAuthController.resetPasswordPage("valid-token", model);

        assertEquals("reset-password", result);
        verify(model).addAttribute("token", "valid-token");
    }

    @Test
    void resetPasswordPage_WithoutToken_ShouldRedirectToForgotPassword() {
        String result = webAuthController.resetPasswordPage(null, model);

        assertEquals("redirect:/forgot-password", result);
    }

    @Test
    void resetPassword_WithMatchingPasswords_ShouldShowSuccessMessage() {
        String result = webAuthController.resetPassword("valid-token", "newpassword", "newpassword", redirectAttributes, model);

        assertEquals("redirect:/login", result);
        verify(redirectAttributes).addFlashAttribute("success", "Password reset successfully! Please login with your new password.");
    }

    @Test
    void resetPassword_WithNonMatchingPasswords_ShouldShowErrorMessage() {
        String result = webAuthController.resetPassword("valid-token", "newpassword", "differentpassword", redirectAttributes, model);

        assertEquals("redirect:/reset-password?token=valid-token", result);
        verify(redirectAttributes).addFlashAttribute("error", "Passwords do not match");
    }

    @Test
    void changePasswordPage_WithValidSession_ShouldReturnChangePasswordView() {
        when(session.getAttribute("userId")).thenReturn(1);
        when(userService.getUserById(anyInt())).thenReturn(testUserResponseDto);

        String result = webAuthController.changePasswordPage(session, model);

        assertEquals("change-password", result);
        verify(model).addAttribute("user", testUserResponseDto);
    }

    @Test
    void changePasswordPage_WithoutSession_ShouldRedirectToLogin() {
        when(session.getAttribute("userId")).thenReturn(null);

        String result = webAuthController.changePasswordPage(session, model);

        assertEquals("redirect:/login", result);
    }

    @Test
    void changePassword_WithMatchingPasswords_ShouldShowSuccessMessage() {
        when(session.getAttribute("userId")).thenReturn(1);

        String result = webAuthController.changePassword("currentpass", "newpass", "newpass", session, redirectAttributes, model);

        assertEquals("redirect:/profile", result);
        verify(redirectAttributes).addFlashAttribute("success", "Password changed successfully!");
    }

    @Test
    void changePassword_WithNonMatchingPasswords_ShouldShowErrorMessage() {
        when(session.getAttribute("userId")).thenReturn(1);

        String result = webAuthController.changePassword("currentpass", "newpass", "differentpass", session, redirectAttributes, model);

        assertEquals("redirect:/profile/change-password", result);
        verify(redirectAttributes).addFlashAttribute("error", "New passwords do not match");
    }
}