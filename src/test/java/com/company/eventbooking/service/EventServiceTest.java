package com.company.eventbooking.service;


import com.company.eventbooking.dto.EventRequest;
import com.company.eventbooking.dto.EventResponse;
import com.company.eventbooking.dto.VenueEventDTO;
import com.company.eventbooking.entity.Event;
import com.company.eventbooking.entity.Venue;
import com.company.eventbooking.exception.ResourceNotFoundException;
import com.company.eventbooking.model.enums.SeatCategory;
import com.company.eventbooking.repository.EventRepository;
import com.company.eventbooking.repository.VenueRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private VenueRepository venueRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void createEvent_ShouldSaveEvent_WhenVenueExists() throws BadRequestException {
        // Given - Record constructor (eventCode, name, eventDate, venueId)
        EventRequest request = new EventRequest("E-001", "Jazz Night", LocalDateTime.now(), 1L);
        Venue venue = Venue.builder().id(1L).name("Blue Note").build();

        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));

        // When
        eventService.createEvent(request);

        // Then
        then(eventRepository).should().save(any(Event.class));
        then(venueRepository).should().findById(request.venueId());
    }

    @Test
    void createEvent_ShouldThrowBadRequest_WhenVenueDoesNotExist() {
        // Given
        EventRequest request = new EventRequest("E-999", "Ghost Event", LocalDateTime.now(), 99L);
        given(venueRepository.findById(99L)).willReturn(Optional.empty());

        // When & Then
        assertThrows(BadRequestException.class, () -> eventService.createEvent(request));
        then(eventRepository).shouldHaveNoInteractions();
    }

    @Test
    void listEvents_ShouldReturnGroupedMapByVenue() {
        // Given - Using a mock for the projection/DTO
        VenueEventDTO dto = mock(VenueEventDTO.class);
        given(dto.getVenueName()).willReturn("Arena 1");
        given(dto.getEventId()).willReturn(10L);
        given(dto.getEventName()).willReturn("Rock Concert");
        given(dto.getSeatId()).willReturn(101L);
        given(dto.getSeatCategory()).willReturn(String.valueOf(SeatCategory.VIP));

        given(eventRepository.findAllAvailableSeats()).willReturn(List.of(dto));

        // When
        Map<String, List<EventResponse>> result = eventService.listEvents();

        // Then
        assertTrue(result.containsKey("Arena 1"));
        assertEquals(1, result.get("Arena 1").size());
        assertEquals("Rock Concert", result.get("Arena 1").get(0).eventName());
        then(eventRepository).should().findAllAvailableSeats();
    }

    @Test
    void updateEvent_ShouldUpdate_WhenEventAndVenueExist() throws BadRequestException {
        // Given
        Long eventId = 1L;
        Event existingEvent = Event.builder().id(eventId).name("Old Name").build();
        Venue newVenue = Venue.builder().id(2L).name("New Venue").build();
        EventRequest request = new EventRequest("UPD-01", "New Name", LocalDateTime.now(), 2L);

        given(eventRepository.findById(eventId)).willReturn(Optional.of(existingEvent));
        given(venueRepository.findById(2L)).willReturn(Optional.of(newVenue));

        // When
        eventService.updateEvent(eventId, request);

        // Then
        assertEquals("New Name", existingEvent.getName());
        assertEquals("UPD-01", existingEvent.getEventCode());
        then(eventRepository).should().save(existingEvent);
    }

    @Test
    void deleteEvent_ShouldThrowResourceNotFound_WhenIdInvalid() {
        // Given
        Long id = 55L;
        given(eventRepository.existsById(id)).willReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(id));
        then(eventRepository).should(never()).deleteById(anyLong());
    }

    @Test
    void deleteEvent_ShouldCallDelete_WhenIdExists() {
        // Given
        Long id = 1L;
        given(eventRepository.existsById(id)).willReturn(true);

        // When
        eventService.deleteEvent(id);

        // Then
        then(eventRepository).should().deleteById(id);
    }
}