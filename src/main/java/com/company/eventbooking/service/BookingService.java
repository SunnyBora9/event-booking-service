package com.company.eventbooking.service;

import com.company.eventbooking.dto.BookingRequest;
import com.company.eventbooking.dto.BookingResponse;
import com.company.eventbooking.entity.*;
import com.company.eventbooking.kafka.event.BookingEvent;
import com.company.eventbooking.kafka.producer.BookingEventProducer;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.repository.*;
import com.company.eventbooking.util.BookingIdGenerator;
import com.company.eventbooking.util.SeatPricingUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Autowired
    private IdempotencyRepository idempotencyRepository;

    @Autowired
    private BookingEventProducer bookingEventProducer;

    @Transactional
    public BookingResponse createBooking(String email, BookingRequest bookingRequest) {

        String fingerprint = email + ":" + bookingRequest.eventId() + ":" +
                bookingRequest.seatIds().stream().sorted().toList();
        String autoIdempotencyKey = org.apache.commons.codec.digest.DigestUtils.sha256Hex(fingerprint);

        Optional<IdempotencyKey> existing = idempotencyRepository.findByIdempotencyKey(autoIdempotencyKey);
        if (existing.isPresent()) {
            Booking b = existing.get().getBooking();
            log.info("Duplicate request detected for key: {}. Returning existing booking.", autoIdempotencyKey);
            return new BookingResponse(b.getBookingCode(), List.of("Duplicate request detected."), b.getTotalPrice());
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Event event = eventRepository.findById(bookingRequest.eventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        //CONCURRENCY FIX
        List<Seat> lockedSeats = seatRepository.lockSeatsForBooking(bookingRequest.seatIds());

        List<String> messages = new ArrayList<>();
        List<Seat> successfullyBookedSeats = new ArrayList<>();

        // 4. PROCESS SEATS INDIVIDUALLY
        for (Long seatId : bookingRequest.seatIds()) {
            // Check if seat exists in the locked list
            Optional<Seat> seatOpt = lockedSeats.stream()
                    .filter(s -> s.getId().equals(seatId))
                    .findFirst();

            if (seatOpt.isEmpty()) {
                messages.add("Seat " + seatId + " does not exist.");
                continue;
            }

            // Safe to check occupancy because we hold the database lock (FOR UPDATE)
            boolean alreadyTaken = bookingSeatRepository.existsByEventIdAndSeatId(event.getId(), seatId);

            if (alreadyTaken) {
                messages.add("Seat " + seatId + " is already taken for this event.");
            } else {
                successfullyBookedSeats.add(seatOpt.get());
                messages.add("Seat " + seatId + " booked successfully.");
            }
        }

        // 5. PERSIST IF THERE ARE SUCCESSFUL SEATS
        if (!successfullyBookedSeats.isEmpty()) {
            // PRICING LOGIC
            double total = successfullyBookedSeats.stream()
                    .mapToDouble(seat -> SeatPricingUtil.getPrice(seat.getSeatCategory())).sum();
            double discount = 0;

            // PROMO LOGIC
            if (bookingRequest.promoCode() != null && !bookingRequest.promoCode().isBlank()) {
                PromoCode promo = promoCodeRepository.findByCodeAndActiveTrue(bookingRequest.promoCode())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid or inactive promo code"));

                if (promo.getValidTill().isBefore(LocalDateTime.now())) {
                    throw new IllegalStateException("Promo code has expired");
                }
                discount = total * (promo.getDiscountPercentage() / 100.0);
            }

            // SAVE BOOKING
            Booking booking = Booking.builder()
                    .bookingCode(BookingIdGenerator.generateBookingId())
                    .user(user)
                    .event(event)
                    .totalPrice(total - discount)
                    .discountAmount(discount)
                    .status(BookingStatus.CONFIRMED)
                    .idempotencyKey(autoIdempotencyKey)
                    .createdAt(LocalDateTime.now())
                    .build();

            bookingRepository.save(booking);

            // SAVE BOOKING-SEAT RELATIONSHIPS
            for (Seat seat : successfullyBookedSeats) {
                bookingSeatRepository.save(BookingSeat.builder()
                        .booking(booking)
                        .event(event)
                        .seat(seat)
                        .build());
            }

            // SAVE IDEMPOTENCY RECORD
            idempotencyRepository.save(IdempotencyKey.builder()
                    .idempotencyKey(autoIdempotencyKey)
                    .booking(booking)
                    .createdAt(LocalDateTime.now())
                    .build());

            // KAFKA NOTIFICATION
            log.info("Booking {} created successfully for user {}", booking.getBookingCode(), email);
            bookingEventProducer.sendBookingEvent(new BookingEvent(
                    booking.getId(),
                    booking.getBookingCode(),
                    email,
                    event.getName(),
                    booking.getCreatedAt()
            ));

            return new BookingResponse(booking.getBookingCode(), messages, booking.getTotalPrice());
        }

        // 6. NO SEATS WERE BOOKED
        return new BookingResponse(null, messages, 0.0);
    }

}