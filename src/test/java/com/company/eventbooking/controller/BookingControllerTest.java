package com.company.eventbooking.controller;


import com.company.eventbooking.dto.BookingRequest;
import com.company.eventbooking.dto.BookingResponse;
import com.company.eventbooking.entity.Booking;
import com.company.eventbooking.kafka.event.BookingEvent;
import com.company.eventbooking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void book_ShouldReturnBookingResponse_WhenSeatsAreAvailable() {
        // GIVEN
        String email = "user@test.com";
        BookingRequest request = new BookingRequest(1L, List.of(101L, 102L), "PROMO10");
        List<String> messages = List.of("Seat 101 booked successfully.", "Seat 102 booked successfully.");

        // Mock Authentication
        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn(email);

        // Mock BookingResponse DTO
        BookingResponse mockResponse = new BookingResponse("BK-12345", messages, 250.0);
        given(bookingService.createBooking(email, request)).willReturn(mockResponse);

        // WHEN
        ResponseEntity<BookingResponse> response = bookingController.book(request, auth);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals("BK-12345", response.getBody().bookingCode());
        assertEquals(2, response.getBody().messages().size());
        assertTrue(response.getBody().messages().get(0).contains("booked successfully"));

        then(bookingService).should().createBooking(email, request);
    }

    @Test
    void book_ShouldReturnBadRequest_WhenNoSeatsAreBooked() {
        // GIVEN
        String email = "user@test.com";
        BookingRequest request = new BookingRequest(1L, List.of(101L), null);
        List<String> messages = List.of("Seat 101 is already taken for this event.");

        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn(email);

        // Mock response where bookingCode is null (partial failure/all taken)
        BookingResponse failedResponse = new BookingResponse(null, messages, 0.0);
        given(bookingService.createBooking(email, request)).willReturn(failedResponse);

        // WHEN
        ResponseEntity<BookingResponse> response = bookingController.book(request, auth);

        // THEN
        assertEquals(400, response.getStatusCode().value());
        assertNull(response.getBody().bookingCode());
        assertTrue(response.getBody().messages().get(0).contains("already taken"));
    }

    @Test
    void sendFailedEvent_ShouldSendMalformedEventToKafka() {
        // GIVEN
        ArgumentCaptor<BookingEvent> eventCaptor = ArgumentCaptor.forClass(BookingEvent.class);

        // WHEN
        String result = bookingController.sendFailedEvent();

        // THEN
        assertEquals("Malformed message sent to 'booking-events'. Watch logs for retries and DLQ move.", result);

        // Verify Kafka interaction
        then(kafkaTemplate).should().send(eq("booking-events"), eventCaptor.capture());

        BookingEvent capturedEvent = eventCaptor.getValue();

        // FIX: Match ID to ID, and Code to Code
        assertEquals(999L, capturedEvent.bookingId());
        assertEquals("DLQ-TEST-001", capturedEvent.bookingCode());
        assertEquals(null, capturedEvent.useremail());
    }
}