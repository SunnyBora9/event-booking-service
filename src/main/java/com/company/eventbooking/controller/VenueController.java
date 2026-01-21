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
    public ResponseEntity<Void> createVenue(@RequestBody VenueRequest venueRequest) {
        log.info("Received request to create venue: {}",venueRequest.name());

        venueService.createVenue(venueRequest);
        log.info("Venue created successfully: {}",venueRequest.name());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<VenueResponse>> listVenues() {
        log.info("Received request to fetch all venues");

        List<VenueResponse> venues = venueService.listVenues();
        log.info("Venue listed successfully: {}",venues.size());

        return ResponseEntity.ok(venues);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateVenue(@PathVariable Long id, @RequestBody VenueRequest venue) {
        log.info("Received request to update venue: {}",venue.name());

        venueService.updateVenue(id, venue);
        log.info("Venue updated successfully: {}",venue.name());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        log.info("Received request to delete event: {}",id.toString());

        venueService.deleteVenue(id);
        log.info("Venue deleted successfully: {}",id.toString());

        return ResponseEntity.noContent().build();
    }

}