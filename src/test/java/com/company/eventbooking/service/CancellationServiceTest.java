package com.company.eventbooking.service;


import com.company.eventbooking.entity.Booking;
import com.company.eventbooking.entity.CancellationRequest;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.repository.BookingRepository;
import com.company.eventbooking.repository.BookingSeatRepository;
import com.company.eventbooking.repository.CancellationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CancellationServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @Mock
    private CancellationRepository cancellationRepository;

    @InjectMocks
    private CancellationService cancellationService;

    @Test
    void requestCancellation_ShouldUpdateStatusAndSaveRequest_WhenBookingIsConfirmed() {
        // Given
        Long bookingId = 1L;
        String reason = "Change of plans";
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.CONFIRMED)
                .build();

        given(bookingRepository.findById(bookingId)).willReturn(Optional.of(booking));

        // When
        cancellationService.requestCancellation(bookingId, reason);

        // Then
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        then(cancellationRepository).should().save(any(CancellationRequest.class));
        then(bookingSeatRepository).should().deleteById(bookingId);
    }

    @Test
    void requestCancellation_ShouldThrowException_WhenBookingNotFound() {
        // Given
        Long bookingId = 99L;
        given(bookingRepository.findById(bookingId)).willReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                cancellationService.requestCancellation(bookingId, "No booking")
        );
        then(cancellationRepository).shouldHaveNoInteractions();
    }

    @Test
    void requestCancellation_ShouldThrowException_WhenBookingIsNotConfirmed() {
        // Given
        Long bookingId = 1L;
        Booking booking = Booking.builder()
                .id(bookingId)
                .status(BookingStatus.CANCELLED) // Not CONFIRMED
                .build();

        given(bookingRepository.findById(bookingId)).willReturn(Optional.of(booking));

        // When & Then
        assertThrows(IllegalStateException.class, () ->
                cancellationService.requestCancellation(bookingId, "Already pending")
        );

        // Verify state didn't change and no deletion occurred
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        then(bookingSeatRepository).should(never()).deleteById(anyLong());
    }
}