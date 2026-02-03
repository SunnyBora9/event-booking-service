package com.company.eventbooking.controller;


import org.junit.jupiter.api.extension.ExtendWith;

import com.company.eventbooking.service.CancellationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CancellationControllerTest {

    @Mock
    private CancellationService cancellationService;

    @InjectMocks
    private CancellationController cancellationController;

    @Test
    void cancel_ShouldReturnSuccessMessageAndCallService() {
        // GIVEN
        Long bookingId = 123L;
        String reason = "Plans changed";

        // Mocking a void method call in the service
        willDoNothing().given(cancellationService).requestCancellation(bookingId, reason);

        // WHEN
        ResponseEntity<String> response = cancellationController.cancel(bookingId, reason);

        // THEN
        // 1. Verify the HTTP status code is 200 OK
        assertEquals(200, response.getStatusCode().value());

        // 2. Verify the response body text
        assertEquals("Cancellation request submitted", response.getBody());

        // 3. Verify the service was actually called once with the correct parameters
        then(cancellationService).should(times(1)).requestCancellation(bookingId, reason);
    }
}