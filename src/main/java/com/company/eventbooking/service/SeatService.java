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

@Service
@RequiredArgsConstructor
public class SeatService {

     @Autowired
    private EventRepository eventRepository;

     @Autowired
     private SeatRepository seatRepository;

     @Autowired
    private BookingSeatRepository bookingSeatRepository;

     public List<SeatAvailabilityResponse> getSeatAvailability(Long eventId){
         Event event=eventRepository.findById(eventId).orElseThrow(()-> new IllegalArgumentException("Event not found"));

         List<Seat> seats=seatRepository.findByVenueId(event.getVenue().getId());

         return seats.stream().map(seat->new SeatAvailabilityResponse(seat.getId(),seat.getSeatNo(),seat.getSeatCategory().name(),!bookingSeatRepository.existsByEventIdAndSeatId(eventId,seat.getId()))).toList();
     }
}