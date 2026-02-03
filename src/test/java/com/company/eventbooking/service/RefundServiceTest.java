package com.company.eventbooking.service;


import com.company.eventbooking.entity.Booking;
import com.company.eventbooking.entity.Refund;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.model.enums.RefundStatus;
import com.company.eventbooking.repository.BookingRepository;
import com.company.eventbooking.repository.RefundRepository;
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
class RefundServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private RefundService refundService;

    @Test
    void initiateRefund_ShouldCreateRefund_WhenBookingIsCancelled() {
        // Given
        Long bookingId = 1L;
        Booking booking = Booking.builder()
                .id(bookingId)
                .totalPrice(150.0)
                .status(BookingStatus.CANCELLED)
                .build();

        given(bookingRepository.findById(bookingId)).willReturn(Optional.of(booking));

        // When
        refundService.initiateRefund(bookingId);

        // Then
        assertEquals(BookingStatus.REFUNDED, booking.getStatus());
        then(refundRepository).should().save(any(Refund.class));
        then(bookingRepository).should(never()).save(any()); // Assuming transactional updates the entity state
    }

    @Test
    void initiateRefund_ShouldThrowException_WhenBookingAlreadyRefunded() {
        // Given
        Long bookingId = 1L;
        Booking booking = Booking.builder()
                .status(BookingStatus.REFUNDED)
                .build();

        given(bookingRepository.findById(bookingId)).willReturn(Optional.of(booking));

        // When & Then
        assertThrows(IllegalStateException.class, () -> refundService.initiateRefund(bookingId));
        then(refundRepository).shouldHaveNoInteractions();
    }

    @Test
    void initiateRefund_ShouldThrowException_WhenBookingNotCancelled() {
        // Given
        Long bookingId = 1L;
        Booking booking = Booking.builder()
                .status(BookingStatus.CONFIRMED)
                .build();

        given(bookingRepository.findById(bookingId)).willReturn(Optional.of(booking));

        // When & Then
        assertThrows(IllegalStateException.class, () -> refundService.initiateRefund(bookingId));
        then(refundRepository).shouldHaveNoInteractions();
    }

    @Test
    void initiateRefund_ShouldThrowException_WhenBookingNotFound() {
        // Given
        given(bookingRepository.findById(anyLong())).willReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> refundService.initiateRefund(99L));
    }
}