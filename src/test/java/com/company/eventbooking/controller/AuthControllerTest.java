package com.company.eventbooking.controller;


import com.company.eventbooking.dto.AuthResponse;
import com.company.eventbooking.dto.LoginRequest;
import com.company.eventbooking.dto.RegisterRequest;
import com.company.eventbooking.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_ShouldReturn201AndCallService() {
        // GIVEN: Full coverage of RegisterRequest fields
        RegisterRequest request = new RegisterRequest("user@test.com", "password123");
        willDoNothing().given(authService).register(request);

        // WHEN
        ResponseEntity<String> response = authController.register(request);

        // THEN
        assertEquals(201, response.getStatusCode().value());
        then(authService).should(times(1)).register(request);
    }

    @Test
    void login_ShouldReturnTokenAndCallService() {
        // GIVEN: Full coverage of LoginRequest fields
        LoginRequest request = new LoginRequest("user@test.com", "password123");
        AuthResponse mockResponse = new AuthResponse("mock-jwt-token");
        given(authService.login(request)).willReturn(mockResponse);

        // WHEN
        ResponseEntity<AuthResponse> response = authController.login(request);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("mock-jwt-token", response.getBody().token());
        then(authService).should(times(1)).login(request);
    }
}