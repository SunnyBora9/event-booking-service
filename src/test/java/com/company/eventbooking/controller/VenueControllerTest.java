package com.company.eventbooking.controller;


import com.company.eventbooking.dto.EventSummaryResponse;
import com.company.eventbooking.dto.VenueRequest;
import com.company.eventbooking.dto.VenueResponse;
import com.company.eventbooking.service.VenueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class VenueControllerTest {

    @Mock
    private VenueService venueService;

    @InjectMocks
    private VenueController venueController;

    @Test
    void listVenues_ShouldReturnVenueListWithEvents() {
        // Given
        // Mocking the inner event list for the record
        List<EventSummaryResponse> mockEvents = List.of(
                new EventSummaryResponse(10L, "Rock Concert", "2026-12-25",null)
        );

        List<VenueResponse> mockVenues = List.of(
                new VenueResponse(1L, "Madison Square Garden", "New York", 20000, mockEvents),
                new VenueResponse(2L, "Wembley", "London", 90000, List.of())
        );

        given(venueService.listVenues()).willReturn(mockVenues);

        // When
        ResponseEntity<List<VenueResponse>> response = venueController.listVenues();

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());

        // Assert record components
        assertEquals("Madison Square Garden", response.getBody().get(0).venueName());
        assertEquals(1, response.getBody().get(0).events().size());
        assertEquals("Rock Concert", response.getBody().get(0).events().get(0).eventCode());

        then(venueService).should(times(1)).listVenues();
    }

    @Test
    void createVenue_ShouldReturnCreated() {
        VenueRequest request = new VenueRequest("Arena", "Chicago", 5000);
        willDoNothing().given(venueService).createVenue(request);

        ResponseEntity<String> response = venueController.createVenue(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Venue is created successfully", response.getBody());
        then(venueService).should().createVenue(request);
    }

    @Test
    void deleteVenue_ShouldReturnNotFound_WhenServiceThrowsException() {
        Long venueId = 99L;
        String errorMsg = "Venue not found";
        willThrow(new RuntimeException(errorMsg)).given(venueService).deleteVenue(venueId);

        ResponseEntity<String> response = venueController.deleteVenue(venueId);

        assertEquals(404, response.getStatusCode().value());
        assertEquals(errorMsg, response.getBody());
        then(venueService).should().deleteVenue(venueId);
    }
}