package com.company.eventbooking.controller;


import com.company.eventbooking.service.RefundService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RefundControllerTest {

    @Mock
    private RefundService refundService;

    @InjectMocks
    private RefundController refundController;

    @Test
    void refund_ShouldReturnSuccessMessage() throws Exception {
        // GIVEN
        Long bookingId = 123L;
        // Logic: willDoNothing handles the success path for void methods
        willDoNothing().given(refundService).initiateRefund(bookingId);

        // WHEN
        ResponseEntity<String> response = refundController.refund(bookingId);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Refund initiated successfully", response.getBody());
        then(refundService).should(times(1)).initiateRefund(bookingId);
    }

    @Test
    void refund_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        // GIVEN
        Long bookingId = 123L;
        String errorMessage = "Booking not eligible for refund";
        // Logic: willThrow covers the catch block for 100% coverage
        willThrow(new RuntimeException(errorMessage)).given(refundService).initiateRefund(bookingId);

        // WHEN
        ResponseEntity<String> response = refundController.refund(bookingId);

        // THEN
        assertEquals(400, response.getStatusCode().value());
        assertEquals(errorMessage, response.getBody());
        then(refundService).should().initiateRefund(bookingId);
    }
}