package com.company.eventbooking.controller;


import com.company.eventbooking.dto.SeatAvailabilityResponse;
import com.company.eventbooking.service.SeatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SeatControllerTest {

    @Mock
    private SeatService seatService;

    @InjectMocks
    private SeatController seatController;

    @Test
    void getAvailableSeats_ShouldReturnList_WhenEventExists() {
        // Given
        String eventName = "Tech Summit";
        List<SeatAvailabilityResponse> mockResponse = List.of(
                new SeatAvailabilityResponse(1L, "A101", "VIP", true),
                new SeatAvailabilityResponse(2L, "B202", "General", false)
        );
        given(seatService.getSeatAvailabilityByName(eventName)).willReturn(mockResponse);

        // When
        ResponseEntity<List<SeatAvailabilityResponse>> response = seatController.getAvailableSeats(eventName);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());

        // Asserting record components
        assertEquals("A101", response.getBody().get(0).seatNumber());
        assertEquals("VIP", response.getBody().get(0).category());

        then(seatService).should(times(1)).getSeatAvailabilityByName(eventName);
    }

    @Test
    void getAvailableSeats_ShouldReturnEmptyList_WhenNoSeatsFound() {
        // Given
        String eventName = "Empty Venue";
        given(seatService.getSeatAvailabilityByName(eventName)).willReturn(List.of());

        // When
        ResponseEntity<List<SeatAvailabilityResponse>> response = seatController.getAvailableSeats(eventName);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().size());
        then(seatService).should().getSeatAvailabilityByName(eventName);
    }
}
