package com.company.eventbooking.controller;

import com.company.eventbooking.dto.VenueRequest;
import com.company.eventbooking.dto.VenueResponse;
import com.company.eventbooking.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    public ResponseEntity<Void> createVenue(@RequestBody VenueRequest venueRequest) {
        int rows = venueService.createVenue(venueRequest);
        return rows > 0 ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping
    public ResponseEntity<List<VenueResponse>> listVenues() {
        List<VenueResponse> venues = venueService.listVenues();
        return ResponseEntity.ok(venues);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateVenue(@PathVariable Long id, @RequestBody VenueRequest venue) {
        int rows = venueService.updateVenue(id, venue);
        return rows > 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        int rows = venueService.deleteVenue(id);
        return rows > 0 ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}