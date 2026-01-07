package com.company.eventbooking.controller;

import com.company.eventbooking.dto.EventRequest;
import com.company.eventbooking.dto.EventResponse;
import com.company.eventbooking.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService){
        this.eventService=eventService;
    }

    @PostMapping
    public ResponseEntity<Void> createEvent(@RequestBody EventRequest request){
        eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> listEvents(){
        return ResponseEntity.ok(eventService.listEvents());

    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable Long id, @RequestBody EventRequest request){
        eventService.updateEvent(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id){
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
