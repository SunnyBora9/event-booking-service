package com.company.eventbooking.service;

import com.company.eventbooking.entity.Booking;
import com.company.eventbooking.entity.Refund;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.model.enums.RefundStatus;
import com.company.eventbooking.repository.BookingRepository;
import com.company.eventbooking.repository.RefundRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefundService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Transactional
    public void initiateRefund(Long bookingId) {

        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()-> new IllegalArgumentException("Booking not found"));

        if(booking.getStatus()== BookingStatus.REFUNDED) {
            throw new IllegalStateException("Refund already initiated");
        }

        if(booking.getStatus()!= BookingStatus.CANCELLED) {
            throw new IllegalStateException("Refund not allowed");
        }

        refundRepository.save(Refund.builder().booking(booking).amount(booking.getTotalPrice()).status(RefundStatus.INITIATED).initiatedAt(LocalDateTime.now()).build());

        booking.setStatus(BookingStatus.REFUNDED);
        log.info("Refund initiated for bookingId={}, amount={}", bookingId, booking.getTotalPrice());

    }
}
