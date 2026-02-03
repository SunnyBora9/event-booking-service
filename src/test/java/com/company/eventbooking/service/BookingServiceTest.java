package com.company.eventbooking.service;


import com.company.eventbooking.dto.BookingRequest;
import com.company.eventbooking.dto.BookingResponse;
import com.company.eventbooking.entity.*;
import com.company.eventbooking.kafka.event.BookingEvent;
import com.company.eventbooking.kafka.producer.BookingEventProducer;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.model.enums.SeatCategory;
import com.company.eventbooking.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private BookingSeatRepository bookingSeatRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private PromoCodeRepository promoCodeRepository;
    @Mock private IdempotencyRepository idempotencyRepository;
    @Mock private BookingEventProducer bookingEventProducer;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_ShouldApplyVipPricing_WhenSeatIsVip() {
        // Given
        String email = "vip@example.com";
        BookingRequest request = new BookingRequest(1L, List.of(100L), null);

        User user = User.builder().email(email).build();
        Event event = Event.builder().id(1L).name("Opera").build();

        Seat vipSeat = Seat.builder()
                .id(100L)
                .seatCategory(SeatCategory.VIP)
                .build();

        given(idempotencyRepository.findByIdempotencyKey(anyString())).willReturn(Optional.empty());
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(eventRepository.findById(1L)).willReturn(Optional.of(event));

        // Updated: Your logic now checks individual seats, so mock the specific repository calls used in the loop
        given(bookingSeatRepository.existsByEventIdAndSeatId(1L, 100L)).willReturn(false);
        given(seatRepository.findById(100L)).willReturn(Optional.of(vipSeat));

        // When
        BookingResponse result = bookingService.createBooking(email, request);

        // Then
        assertNotNull(result);
        assertNotNull(result.bookingCode());
        // Verify total price is calculated via SeatPricingUtil
        assertTrue(result.finalPrice() > 0);
        // Verify specific seat message
        assertTrue(result.messages().contains("Seat 100 booked successfully."));

        then(bookingRepository).should().save(any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrowException_WhenPromoCodeIsExpired() {
        // Given
        String email = "user@test.com";
        Long eventId = 1L;
        Long seatId = 101L;
        BookingRequest request = new BookingRequest(eventId, List.of(seatId), "EXPIRED50");

        PromoCode expiredPromo = PromoCode.builder()
                .code("EXPIRED50")
                .discountPercentage(50)
                .validTill(LocalDateTime.now().minusDays(1)) // Expired yesterday
                .active(true)
                .build();

        Seat regSeat = Seat.builder().id(seatId).seatCategory(SeatCategory.REGULAR).build();

        given(idempotencyRepository.findByIdempotencyKey(anyString())).willReturn(Optional.empty());
        given(userRepository.findByEmail(email)).willReturn(Optional.of(new User()));
        given(eventRepository.findById(eventId)).willReturn(Optional.of(Event.builder().id(eventId).build()));

        // Mock the new iterative seat check logic
        given(bookingSeatRepository.existsByEventIdAndSeatId(eventId, seatId)).willReturn(false);
        given(seatRepository.findById(seatId)).willReturn(Optional.of(regSeat));

        // Mock promo code fetch
        given(promoCodeRepository.findByCodeAndActiveTrue("EXPIRED50")).willReturn(Optional.of(expiredPromo));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                bookingService.createBooking(email, request)
        );

        assertEquals("Promo code has expired", exception.getMessage());
        then(bookingRepository).shouldHaveNoInteractions(); // Ensure nothing was saved
    }

    @Test
    void createBooking_ShouldHandleMultipleSeatCategories() {
        // Given
        String email = "mixed@example.com";
        Long eventId = 1L;
        BookingRequest request = new BookingRequest(eventId, List.of(101L, 102L), null);

        Seat regSeat = Seat.builder().id(101L).seatCategory(SeatCategory.REGULAR).build();
        Seat premSeat = Seat.builder().id(102L).seatCategory(SeatCategory.PREMIUM).build();

        given(idempotencyRepository.findByIdempotencyKey(anyString())).willReturn(Optional.empty());
        given(userRepository.findByEmail(email)).willReturn(Optional.of(new User()));
        given(eventRepository.findById(eventId)).willReturn(Optional.of(Event.builder().id(eventId).build()));

        // Mock individual checks for Seat 101
        given(bookingSeatRepository.existsByEventIdAndSeatId(eventId, 101L)).willReturn(false);
        given(seatRepository.findById(101L)).willReturn(Optional.of(regSeat));

        // Mock individual checks for Seat 102
        given(bookingSeatRepository.existsByEventIdAndSeatId(eventId, 102L)).willReturn(false);
        given(seatRepository.findById(102L)).willReturn(Optional.of(premSeat));

        // When
        BookingResponse result = bookingService.createBooking(email, request);

        // Then
        assertEquals(2, result.messages().size());
        assertTrue(result.finalPrice() > 0);
        then(bookingSeatRepository).should(times(2)).save(any(BookingSeat.class));
        then(bookingEventProducer).should().sendBookingEvent(any(com.company.eventbooking.kafka.event.BookingEvent.class));
    }
}
