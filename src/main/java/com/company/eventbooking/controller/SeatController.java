package com.company.eventbooking.controller;

import com.company.eventbooking.dto.SeatAvailabilityResponse;
import com.company.eventbooking.service.SeatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<SeatAvailabilityResponse>> getAvailableSeats(@PathVariable Long eventId){
        return ResponseEntity.ok(seatService.getSeatAvailability(eventId));
    }

}