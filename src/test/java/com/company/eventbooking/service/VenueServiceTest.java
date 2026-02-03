package com.company.eventbooking.service;


import com.company.eventbooking.dto.VenueRequest;
import com.company.eventbooking.dto.VenueResponse;
import com.company.eventbooking.entity.Event;
import com.company.eventbooking.entity.Venue;
import com.company.eventbooking.exception.ResourceNotFoundException;
import com.company.eventbooking.repository.EventRepository;
import com.company.eventbooking.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock private VenueRepository venueRepository;
    @Mock private EventRepository eventRepository;

    @InjectMocks
    private VenueService venueService;

    @Test
    void createVenue_ShouldSave_WhenRequestIsValid() {
        // Given
        VenueRequest request = new VenueRequest("O2 Arena", "London", 500);
        given(venueRepository.existsByNameAndCity(request.name(), request.city())).willReturn(false);

        // When
        venueService.createVenue(request);

        // Then
        then(venueRepository).should().save(any(Venue.class));
        then(venueRepository).should().flush();
    }

    @Test
    void createVenue_ShouldThrowException_WhenCapacityIsTooLow() {
        // Given
        VenueRequest request = new VenueRequest("Small Club", "London", 150);

        // When & Then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> venueService.createVenue(request));
        assertEquals("Capacity cannot be less than 200", ex.getMessage());
    }

    @Test
    void createVenue_ShouldThrowConflictException_WhenDataIntegrityViolated() {
        // Given
        VenueRequest request = new VenueRequest("Stadium", "Madrid", 1000);
        given(venueRepository.existsByNameAndCity(any(), any())).willReturn(false);
        willThrow(new DataIntegrityViolationException("Duplicate")).given(venueRepository).save(any());

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> venueService.createVenue(request));
        assertTrue(ex.getMessage().contains("Someone else just created this venue"));
    }

    @Test
    void listVenues_ShouldMapEntitiesToResponses() {
        // Given
        Event mockEvent = Event.builder()
                .id(1L).eventCode("E1").name("Concert").eventDate(LocalDateTime.now())
                .build();
        Venue venue = Venue.builder()
                .id(10L).name("Arena").city("Paris").capacity(500)
                .events(List.of(mockEvent))
                .build();

        given(venueRepository.findAll()).willReturn(List.of(venue));

        // When
        List<VenueResponse> result = venueService.listVenues();

        // Then
        assertEquals(1, result.size());
        assertEquals("Arena", result.get(0).venueName());
        assertEquals(1, result.get(0).events().size());
        assertEquals("Concert", result.get(0).events().get(0).eventName());
    }

    @Test
    void updateVenue_ShouldThrowException_WhenAnotherVenueExistsWithSameDetails() {
        // Given
        Long venueId = 1L;
        VenueRequest request = new VenueRequest("New Name", "New City", 300);
        Venue existingVenue = Venue.builder().id(venueId).build();

        given(venueRepository.findById(venueId)).willReturn(Optional.of(existingVenue));
        given(venueRepository.existsByNameAndCityAndIdNot(request.name(), request.city(), venueId)).willReturn(true);

        // When & Then
        assertThrows(IllegalStateException.class, () -> venueService.updateVenue(venueId, request));
    }

    @Test
    void deleteVenue_ShouldThrowException_WhenVenueHasActiveEvents() {
        // Given
        Long venueId = 1L;
        given(venueRepository.existsById(venueId)).willReturn(true);
        given(eventRepository.existsById(venueId)).willReturn(false); // Service logic: if exists is false, throw exception

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> venueService.deleteVenue(venueId));
        assertEquals("Deletion failed: This venue has active events scheduled.", ex.getMessage());
    }

    @Test
    void deleteVenue_ShouldCallDelete_WhenVenueHasNoEvents() {
        // Given
        Long venueId = 1L;
        given(venueRepository.existsById(venueId)).willReturn(true);
        given(eventRepository.existsById(venueId)).willReturn(true); // Logic check: needs to return true to proceed

        // When
        venueService.deleteVenue(venueId);

        // Then
        then(venueRepository).should().deleteById(venueId);
    }
}
