package com.company.eventbooking.service;


import com.company.eventbooking.config.JwtUtil;
import com.company.eventbooking.dto.AuthResponse;
import com.company.eventbooking.dto.LoginRequest;
import com.company.eventbooking.dto.RegisterRequest;
import com.company.eventbooking.entity.User;
import com.company.eventbooking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldSaveUser_WhenEmailIsNew() {
        // Given
        RegisterRequest request = new RegisterRequest("test@example.com", "password123");
        given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());
        given(passwordEncoder.encode(request.password())).willReturn("hashed_password");

        // When
        authService.register(request);

        // Then
        then(userRepository).should().save(any(User.class));
        then(passwordEncoder).should().encode("password123");
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        RegisterRequest request = new RegisterRequest("exists@example.com", "password123");
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(new User()));

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> authService.register(request));
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        // Given
        LoginRequest request = new LoginRequest("user@example.com", "correct_pass");
        User user = User.builder().email("user@example.com").password("hashed_pass").build();

        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(true);
        given(jwtUtil.generateToken(user.getEmail())).willReturn("mock_jwt_token");

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("mock_jwt_token", response.token());
        then(jwtUtil).should().generateToken(user.getEmail());
    }

    @Test
    void login_ShouldThrowBadCredentials_WhenPasswordIsWrong() {
        // Given
        LoginRequest request = new LoginRequest("user@example.com", "wrong_pass");
        User user = User.builder().email("user@example.com").password("hashed_pass").build();

        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        then(jwtUtil).shouldHaveNoInteractions();
    }
}