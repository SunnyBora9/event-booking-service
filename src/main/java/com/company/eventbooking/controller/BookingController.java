package com.company.eventbooking.controller;

import com.company.eventbooking.dto.BookingRequest;
import com.company.eventbooking.entity.Booking;
import com.company.eventbooking.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<String> book(@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestBody BookingRequest bookingRequest, Authentication authentication){


        Booking booking = bookingService.createBooking(authentication.getName(),bookingRequest,idempotencyKey);

        return ResponseEntity.ok("Booking successfully created"+" "+booking.getBookingCode());
    }
}