package com.company.eventbooking.controller;

import com.company.eventbooking.dto.VenueRequest;
import com.company.eventbooking.dto.VenueResponse;
import com.company.eventbooking.service.VenueService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name="bearerAuth")
@RestController
@Slf4j
@RequestMapping("/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @PostMapping
    public ResponseEntity<String> createVenue(@RequestBody VenueRequest venueRequest) {
        log.info("Creating venue: {}", venueRequest.name());

        try{
        venueService.createVenue(venueRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Venue is created successfully");
        } catch(Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getLocalizedMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<VenueResponse>> listVenues() {
        log.info("Received request to fetch all venues");

        List<VenueResponse> venues = venueService.listVenues();
        log.info("Venue listed successfully: {}",venues.size());

        return ResponseEntity.ok(venues);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVenue(@PathVariable Long id, @RequestBody VenueRequest venue) {
        log.info("Received request to update venue: {}", venue.name());

        try{
            venueService.updateVenue(id, venue);
            log.info("Venue {} updated successfully", venue.name());
            return ResponseEntity.ok("Venue is updated successfully");
        } catch (Exception e){
            return ResponseEntity.ok(e.getLocalizedMessage());

        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVenue(@PathVariable Long id) {
        log.info("Request to delete venue: {}", id);
        try {
            venueService.deleteVenue(id);
            log.info("Venue {} deleted successfully", id);
            return ResponseEntity.ok("Venue is deleted successfully");
        } catch(Exception exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getLocalizedMessage());
        }
    }

}