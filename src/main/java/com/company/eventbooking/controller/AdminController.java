package com.company.eventbooking.controller;

import com.company.eventbooking.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping(value="/venues/upload",consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadVenues(@RequestParam("file") MultipartFile file) throws IOException {
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("CSV file is required");
        }
        adminService.uploadVenues(file);
        return ResponseEntity.ok("Venues uploaded successfully");
    }

    @PostMapping(value="/events/upload",consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadEvents(@RequestParam MultipartFile file) throws IOException {
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("CSV file is required");
        }
        adminService.uploadEvents(file);
        return ResponseEntity.ok("Events uploaded successfully");
    }

    @PostMapping("/cancellations/{bookingId}/approve")
    public ResponseEntity<String> approveCancellation(@PathVariable Long bookingId) throws IOException {
        adminService.approveCancellation(bookingId);
        return ResponseEntity.ok("Cancellation approved");
    }

    @PutMapping("/events/{eventId}/reschedule")
    public ResponseEntity<String> rescheduleEvent(@PathVariable Long eventId, @RequestParam String newDate) throws IOException {
        adminService.rescheduleEvent(eventId, newDate);
        return ResponseEntity.ok("Event rescheduled successfully");

    }
}