package com.company.eventbooking.service;

import com.company.eventbooking.dto.EventRequest;
import com.company.eventbooking.dto.EventResponse;
import com.company.eventbooking.entity.Event;
import com.company.eventbooking.entity.Venue;
import com.company.eventbooking.exception.ResourceNotFoundException;
import com.company.eventbooking.repository.EventRepository;
import com.company.eventbooking.repository.VenueRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    //create
    public void createEvent(EventRequest request) throws BadRequestException {
        Venue venue = venueRepository.findById(request.venueId()).orElseThrow(() -> new BadRequestException("Venue does not exist"));
        Event event = Event.builder()
                .eventCode(generateEventCode(request.name()))
                .name(request.name())
                .eventDate(request.eventDate())
                .venue(venue)
                .build();
        eventRepository.save(event);
    }

    //read
    public List<EventResponse> listEvents() {
        return eventRepository.findAll().stream().map(this::toResponse).toList();
    }

    //update
    public void updateEvent(Long id, EventRequest request) throws BadRequestException {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        Venue venue = venueRepository.findById(request.venueId()).orElseThrow(() -> new BadRequestException("Venue does not exist"));

        event.setEventCode(request.eventCode());
        event.setName(request.name());
        event.setEventDate(request.eventDate());
        event.setVenue(venue);

        eventRepository.save(event);
    }

    //delete
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    //Mapper
    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getEventCode(),
                event.getName(),
                event.getEventDate(),
                event.getVenue().getId()
        );
    }

    /**
     * Generates a unique event code
     *
     * @param name event name
     * @return generated event code
     */
    private String generateEventCode(String name) {
        return name.toUpperCase().replace(" ","_")
                +"_"+System.currentTimeMillis();
    }

}