package com.company.eventbooking.service;


import com.company.eventbooking.dto.SeatAvailabilityResponse;
import com.company.eventbooking.entity.*;
import com.company.eventbooking.model.enums.SeatCategory;
import com.company.eventbooking.repository.BookingSeatRepository;
import com.company.eventbooking.repository.EventRepository;
import com.company.eventbooking.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private BookingSeatRepository bookingSeatRepository;

    @InjectMocks
    private SeatService seatService;

    @Test
    void getSeatAvailabilityByName_ShouldReturnAvailability_WhenEventExists() {

        String eventName = "Jazz Night";
        Venue venue = Venue.builder().id(1L).name("Blue Note").build();
        Event event = Event.builder().id(10L).name(eventName).venue(venue).build();

        Seat seat1 = Seat.builder().id(101L).seatNo("A1").seatCategory(SeatCategory.VIP).build();
        Seat seat2 = Seat.builder().id(102L).seatNo("A2").seatCategory(SeatCategory.REGULAR).build();

        BookingSeat bookedSeat = BookingSeat.builder().seat(seat1).build();

        given(eventRepository.findByNameIgnoreCase(eventName)).willReturn(Optional.of(event));
        given(seatRepository.findByVenueId(1L)).willReturn(List.of(seat1, seat2));
        given(bookingSeatRepository.findAllByEventId(10L)).willReturn(List.of(bookedSeat));

        List<SeatAvailabilityResponse> result = seatService.getSeatAvailabilityByName(eventName);

        assertEquals(2, result.size());

        SeatAvailabilityResponse res1 = result.stream().filter(r -> r.seatNumber().equals("A1")).findFirst().get();
        assertFalse(res1.available());

        SeatAvailabilityResponse res2 = result.stream().filter(r -> r.seatNumber().equals("A2")).findFirst().get();
        assertTrue(res2.available());

        then(eventRepository).should().findByNameIgnoreCase(eventName);
    }

    @Test
    void getSeatAvailabilityByName_ShouldThrowException_WhenEventNotFound() {
        // Given
        String eventName = "NonExistent";
        given(eventRepository.findByNameIgnoreCase(eventName)).willReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                seatService.getSeatAvailabilityByName(eventName)
        );
    }

    @Test
    void getSeatAvailabilityByName_ShouldReturnAllAvailable_WhenNoSeatsBooked() {
        // Given
        String eventName = "Empty Event";
        Venue venue = Venue.builder().id(1L).build();
        Event event = Event.builder().id(10L).venue(venue).build();
        Seat seat = Seat.builder().id(101L).seatNo("B1").seatCategory(SeatCategory.REGULAR).build();

        given(eventRepository.findByNameIgnoreCase(eventName)).willReturn(Optional.of(event));
        given(seatRepository.findByVenueId(1L)).willReturn(List.of(seat));
        given(bookingSeatRepository.findAllByEventId(10L)).willReturn(List.of());

        // When
        List<SeatAvailabilityResponse> result = seatService.getSeatAvailabilityByName(eventName);

        // Then
        assertTrue(result.get(0).available());
    }
}
