package com.company.eventbooking.controller;


import com.company.eventbooking.service.CancellationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cancellations")
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
public class CancellationController {

    @Autowired
    private CancellationService cancellationService;

    @PostMapping("/{bookingId}")
    public ResponseEntity<String> cancel(@PathVariable Long bookingId, @RequestParam String reason) {
        cancellationService.requestCancellation(bookingId, reason);
        return ResponseEntity.ok("Cancellation request submitted");

    }
}