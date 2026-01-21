package com.company.eventbooking.service;

import com.company.eventbooking.dto.BookingRequest;
import com.company.eventbooking.entity.*;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.repository.*;
import com.company.eventbooking.util.BookingIdGenerator;
import com.company.eventbooking.util.SeatPricingUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Transactional
    public Booking createBooking(String email,BookingRequest bookingRequest,String idempotencyKey) {

        Optional<IdempotencyKey> existing=idempotencyRepository.findByIdempotencyKey(idempotencyKey);

        if(existing.isPresent()){
            return existing.get().getBooking();
        }

        User user=userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("User not found"));

        Event event=eventRepository.findById(bookingRequest.eventId()).orElseThrow(()->new IllegalArgumentException("Event not found"));

        List<Seat> seats=seatRepository.lockSeatsForBooking(bookingRequest.seatIds());

        double total=seats.stream().mapToDouble(seat -> SeatPricingUtil.getPrice(seat.getSeatCategory())).sum();

        double discount=0;

        if(bookingRequest.promoCode()!=null){
            PromoCode promo=promoCodeRepository.findByCodeAndActiveTrue(bookingRequest.promoCode()).orElseThrow(()->new IllegalArgumentException(("Invalid promo code")));

            if(promo.getValidTill().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Invalid promo code");
            }

            discount=total*promo.getDiscountPercentage() /100;
        }


        Booking booking= Booking.builder()
                .bookingCode(BookingIdGenerator.generateBookingId())
                .user(user)
                .event(event)
                .totalPrice(total-discount)
                .discountAmount(discount)
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now()).build();


        bookingRepository.save(booking);

        for(Seat seat: seats) {
            bookingSeatRepository.save(BookingSeat.builder().booking(booking).event(event).seat(seat).build());
        }

        idempotencyRepository.save(IdempotencyKey.builder().idempotencyKey(idempotencyKey).booking(booking).createdAt(LocalDateTime.now()).build());

        log.info("Booking created, totalPrice={}", total);
        return booking;

    }
}