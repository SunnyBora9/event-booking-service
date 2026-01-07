package com.company.eventbooking.service;

import com.company.eventbooking.dto.EventRequest;
import com.company.eventbooking.dto.EventResponse;
import com.company.eventbooking.repository.EventRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService  {

    private final EventRepository eventRepository;

    public EventService(JdbcTemplate jdbcTemplate, EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public int createEvent(EventRequest request) {
        return eventRepository.create(request);
    }

    public List<EventResponse> listEvents() {
        return eventRepository.findAll();
    }

    public List<EventResponse> listEventsVenue(Long venueId) {
        return eventRepository.findByVenueId(venueId);
    }

    public int updateEvent(Long id,EventRequest request) {
        return eventRepository.update(id,request);
    }

    public int deleteEvent(Long id) {
        return eventRepository.delete(id);
    }

}