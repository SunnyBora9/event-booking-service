package com.company.eventbooking.service;


import com.company.eventbooking.entity.*;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private VenueRepository venueRepository;
    @Mock private EventRepository eventRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private CancellationRepository cancelationRepository;
    @Mock private RefundService refundService;

    @InjectMocks
    private AdminService adminService;

    @Test
    void uploadVenues_ShouldSaveVenues_WhenCSVIsValid() throws IOException {
        // Given
        String csvContent = "name,city,capacity\nGrand Arena,London,5000";
        MockMultipartFile file = new MockMultipartFile("file", "v.csv", "text/csv", csvContent.getBytes());

        // When
        adminService.uploadVenues(file);

        // Then
        then(venueRepository).should(atLeastOnce()).save(any(Venue.class));
    }

    @Test
    void uploadEvents_ShouldSaveEvents_WhenVenueExists() throws IOException {
        // Given
        String csvContent = "code,name,date,venueId\nE1,Rock,2026-12-25T10:00:00,1";
        MockMultipartFile file = new MockMultipartFile("file", "e.csv", "text/csv", csvContent.getBytes());
        Venue mockVenue = Venue.builder().id(1L).name("Arena").build();

        given(venueRepository.findById(1L)).willReturn(Optional.of(mockVenue));

        // When
        adminService.uploadEvents(file);

        // Then
        then(eventRepository).should().save(any(Event.class));
    }

    @Test
    void approveCancellation_ShouldUpdateStatusAndInitiateRefund() {
        // Given
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.REFUNDED);

        CancellationRequest request = new CancellationRequest();
        request.setBooking(booking);

        given(cancelationRepository.findByBookingId(bookingId)).willReturn(Optional.of(request));

        // When
        adminService.approveCancellation(bookingId);

        // Then
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        then(bookingRepository).should().save(booking);
        then(refundService).should().initiateRefund(bookingId);
    }

    @Test
    void rescheduleEvent_ShouldUpdateDate_WhenEventExists() {
        // Given
        Long eventId = 1L;
        String newDate = "2026-12-30T18:00:00";
        Event event = Event.builder().id(eventId).eventDate(LocalDateTime.now()).build();

        given(eventRepository.findById(eventId)).willReturn(Optional.of(event));

        // When
        adminService.rescheduleEvent(eventId, newDate);

        // Then
        assertEquals(LocalDateTime.parse(newDate), event.getEventDate());
        then(eventRepository).should().save(event);
    }

    @Test
    void rescheduleEvent_ShouldThrowException_WhenEventNotFound() {
        // Given
        given(eventRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                adminService.rescheduleEvent(1L, "2026-12-30T18:00:00")
        );
    }
}