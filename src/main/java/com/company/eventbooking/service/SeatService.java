package com.company.eventbooking.service;

import com.company.eventbooking.dto.SeatAvailabilityResponse;
import com.company.eventbooking.entity.Event;
import com.company.eventbooking.entity.Seat;
import com.company.eventbooking.repository.BookingSeatRepository;
import com.company.eventbooking.repository.EventRepository;
import com.company.eventbooking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

     @Autowired
    private EventRepository eventRepository;

     @Autowired
     private SeatRepository seatRepository;

     @Autowired
    private BookingSeatRepository bookingSeatRepository;

    public List<SeatAvailabilityResponse> getSeatAvailabilityByName(String eventName) {
        // 1. Find event by name
        Event event = eventRepository.findByNameIgnoreCase(eventName)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventName));

        // 2. Get all seats for the venue
        List<Seat> allSeats = seatRepository.findByVenueId(event.getVenue().getId());

        // 3. FIX: Ensure you are filtering by the specific Event ID in the repository query
        // Also, added a null check and strict Long mapping
        Set<Long> bookedSeatIds = bookingSeatRepository.findAllByEventId(event.getId())
                .stream()
                .filter(bs -> bs.getSeat() != null)
                .map(bs -> bs.getSeat().getId())
                .collect(Collectors.toSet());

        // 4. Map to response
        return allSeats.stream()
                .map(seat -> new SeatAvailabilityResponse(
                        seat.getId(),
                        seat.getSeatNo(),
                        seat.getSeatCategory().name(),
                        !bookedSeatIds.contains(seat.getId())
                ))
                .toList();
    }
}