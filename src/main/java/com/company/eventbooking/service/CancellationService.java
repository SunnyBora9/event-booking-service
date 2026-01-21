package com.company.eventbooking.service;

import com.company.eventbooking.entity.Booking;
import com.company.eventbooking.entity.CancellationRequest;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.repository.BookingRepository;
import com.company.eventbooking.repository.BookingSeatRepository;
import com.company.eventbooking.repository.CancellationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancellationService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private CancellationRepository  cancellationRepository;

    @Transactional
    public void requestCancellation(Long bookingId,String reason) {

        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()-> new IllegalArgumentException("Booking not found"));

        if(booking.getStatus()!= BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking not confirmed");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        cancellationRepository.save(CancellationRequest.builder().booking(booking).reason(reason).requestAt(LocalDateTime.now()).build());

        bookingSeatRepository.deleteById(bookingId);


        log.info("requestCancellation");
    }
}
