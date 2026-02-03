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
    public String initiateRefund(Long bookingId) {
        // 1. Fetch the booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        // 2. Guard Clauses
        if (booking.getStatus() == BookingStatus.REFUNDED) {
            throw new IllegalStateException("Refund has already been completed for this booking.");
        }

        if (booking.getStatus() != BookingStatus.CANCELLED) {
            throw new IllegalStateException("Refunds can only be initiated for CANCELLED bookings.");
        }

        // 3. Create Refund Record
        double refundAmount = booking.getTotalPrice();
        Refund refund = Refund.builder()
                .booking(booking)
                .amount(refundAmount)
                .status(RefundStatus.INITIATED)
                .initiatedAt(LocalDateTime.now())
                .build();

        refundRepository.save(refund);

        // 4. Update Booking Fields (The "Audit" part)
        // We keep a record of what WAS paid, but zero out the active price
        booking.setStatus(BookingStatus.REFUNDED);
        booking.setTotalPrice(0.0); // Reset the active balance

        // Pro-Tip: If you have a 'updatedAt' or 'refundedAt' field in Booking, set it here
        // booking.setUpdatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        log.info("✅ REFUND SUCCESS: BookingId={}, RefundedAmount={}", bookingId, refundAmount);

        return "Refund of " + refundAmount + " initiated successfully. Booking status updated to REFUNDED.";
    }
}
