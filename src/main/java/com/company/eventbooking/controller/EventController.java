package com.company.eventbooking.controller;

import com.company.eventbooking.dto.EventRequest;
import com.company.eventbooking.dto.EventResponse;
import com.company.eventbooking.service.EventService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name="bearerAuth")
@RestController
@Slf4j
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Void> createEvent(@RequestBody EventRequest request) throws BadRequestException {
        log.info("Received request to create event: {}",request.name());

        eventService.createEvent(request);
        log.info("Event created successfully: {}",request.name());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> listEvents(){
        log.info("Received request to fetch all events");

        List<EventResponse> events=eventService.listEvents();
        log.info("All events fetched successfully: {}",events.size());

        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable Long id, @RequestBody EventRequest request) throws BadRequestException {
        log.info("Received request to update event: {}",request.name());

        eventService.updateEvent(id, request);
        log.info("Event updated successfully: {}",request.name());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id){
        log.info("Received request to delete event: {}",id.toString());

        eventService.deleteEvent(id);
        log.info("Event deleted successfully: {}",id.toString());

        return ResponseEntity.noContent().build();
    }

}
