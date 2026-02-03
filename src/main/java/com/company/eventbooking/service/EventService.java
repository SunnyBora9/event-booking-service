package com.company.eventbooking.service;

import com.company.eventbooking.dto.EventRequest;
import com.company.eventbooking.dto.EventResponse;
import com.company.eventbooking.dto.VenueEventDTO;
import com.company.eventbooking.entity.Event;
import com.company.eventbooking.entity.Venue;
import com.company.eventbooking.exception.ResourceNotFoundException;
import com.company.eventbooking.kafka.event.EventNotification;
import com.company.eventbooking.kafka.producer.BookingEventProducer;
import com.company.eventbooking.repository.EventRepository;
import com.company.eventbooking.repository.VenueRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private BookingEventProducer bookingEventProducer;

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

    public Map<String, List<EventResponse>> listEvents() {
        List<VenueEventDTO> data = eventRepository.findAllAvailableSeats();

        // 1. Group by Venue Name
        return data.stream().collect(Collectors.groupingBy(
                VenueEventDTO::getVenueName,
                Collectors.collectingAndThen(
                        Collectors.groupingBy(VenueEventDTO::getEventId),
                        eventMap -> eventMap.values().stream()
                                .map(this::mapToEventResponse)
                                .toList()
                )
        ));
    }

    private EventResponse mapToEventResponse(List<VenueEventDTO> eventGroup) {
        VenueEventDTO first = eventGroup.get(0);
        List<EventResponse.SeatResponse> seats = eventGroup.stream()
                .map(p -> new EventResponse.SeatResponse(p.getSeatId(), p.getSeatNo(), p.getSeatCategory()))
                .toList();

        return new EventResponse(
                first.getEventId(), first.getEventCode(), first.getEventName(),
                first.getEventDate(), first.getVenueId(), seats
        );
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

        bookingEventProducer.sendEventNotification(new EventNotification(
                event.getId(),
                event.getName(),
                "The event details have been updated. Check the new schedule.",
                "UPDATE",
                LocalDateTime.now()
        ));
    }

    public void triggerReminder(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        bookingEventProducer.sendEventNotification(new EventNotification(
                event.getId(),
                event.getName(),
                "Reminder: Your event is happening soon!",
                "REMINDER",
                LocalDateTime.now()
        ));
    }

    //delete
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found");
        }
        eventRepository.deleteById(id);
    }

//    private EventResponse mapDtoToResponse(VenueEventDTO dto) {
//        return new EventResponse(
//                dto.getEventId(),
//                dto.getEventCode(),
//                dto.getEventName(),
//                dto.getEventDate(),
//                dto.getVenueId()
//        );
//    }

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