package com.company.eventbooking.controller;


import com.company.eventbooking.dto.EventRequest;
import com.company.eventbooking.dto.EventResponse;
import com.company.eventbooking.service.EventService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Test
    void createEvent_ShouldReturn201() throws BadRequestException {
        // GIVEN: Match the 4 fields: eventCode, name, eventDate, venueId
        EventRequest request = new EventRequest(
                "EVT-001",
                "Music Concert",
                LocalDateTime.of(2026, 5, 20, 10, 0), // Correct LocalDateTime object
                1L
        );

        willDoNothing().given(eventService).createEvent(request);

        // WHEN
        ResponseEntity<Void> response = eventController.createEvent(request);

        // THEN
        assertEquals(201, response.getStatusCode().value());
        then(eventService).should(times(1)).createEvent(request);
    }

    @Test
    void listEvents_ShouldReturnMapAnd200() {
        // GIVEN
        Map<String, List<EventResponse>> mockEvents = new HashMap<>();
        mockEvents.put("Mumbai", List.of(new EventResponse(1L, "Concert", "Mumbai",null,null,null)));
        given(eventService.listEvents()).willReturn(mockEvents);

        // WHEN
        ResponseEntity<Map<String, List<EventResponse>>> response = eventController.listEvents();

        // THEN
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        then(eventService).should().listEvents();
    }

    @Test
    void updateEvent_ShouldReturn200() throws BadRequestException {
        // GIVEN
        Long id = 1L;
        // Match the 4 fields: eventCode, name, eventDate, venueId
        EventRequest request = new EventRequest(
                "EVT-UPDATED-001",
                "Updated Concert",
                LocalDateTime.of(2026, 6, 20, 19, 0), // Use LocalDateTime
                1L
        );

        willDoNothing().given(eventService).updateEvent(id, request);

        // WHEN
        ResponseEntity<Void> response = eventController.updateEvent(id, request);

        // THEN
        assertEquals(200, response.getStatusCode().value());
        then(eventService).should().updateEvent(id, request);
    }

    @Test
    void deleteEvent_ShouldReturn204() {
        // GIVEN
        Long id = 1L;
        willDoNothing().given(eventService).deleteEvent(id);

        // WHEN
        ResponseEntity<Void> response = eventController.deleteEvent(id);

        // THEN
        assertEquals(204, response.getStatusCode().value());
        then(eventService).should().deleteEvent(id);
    }
}
