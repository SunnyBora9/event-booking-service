package com.company.eventbooking.service;

import com.company.eventbooking.dto.EventSummaryResponse;
import com.company.eventbooking.dto.VenueRequest;
import com.company.eventbooking.dto.VenueResponse;
import com.company.eventbooking.entity.Venue;
import com.company.eventbooking.exception.ResourceNotFoundException;
import com.company.eventbooking.repository.EventRepository;
import com.company.eventbooking.repository.VenueRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public void createVenue(VenueRequest venueRequest) {
        if (venueRequest.name() == null || venueRequest.city().isBlank()) {
            throw new IllegalArgumentException("City name cannot be empty");
        }

        if (venueRepository.existsByNameAndCity(venueRequest.name(), venueRequest.city())) {
            throw new IllegalStateException("Venue already exists in this city");
        }

        if(venueRequest.capacity()<200){
            throw new IllegalArgumentException("Capacity cannot be less than 200");
        }

        try {
            Venue venue = Venue.builder()
                    .name(venueRequest.name())
                    .city(venueRequest.city())
                    .capacity(venueRequest.capacity())
                    .build();

            venueRepository.save(venue);
            venueRepository.flush();

        } catch (DataIntegrityViolationException e) {
            log.error("Concurrency conflict: Venue {} already created", venueRequest.name());
            throw new IllegalStateException("Someone else just created this venue. Please refresh.");
        }
    }

    public List<VenueResponse> listVenues() {
        return venueRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private VenueResponse toResponse(Venue venue) {
        // Convert the List of Event entities to EventSummaryResponse DTOs
        List<EventSummaryResponse> eventList = venue.getEvents().stream()
                .map(e -> new EventSummaryResponse(
                        e.getId(),
                        e.getEventCode(),
                        e.getName(),
                        e.getEventDate()
                ))
                .toList();

        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getCapacity(),
                eventList
        );
    }

    //update
    @Transactional
    public void updateVenue(Long id, VenueRequest request) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id " + id));

        if (request.capacity() == null || request.capacity() < 200) {
            throw new IllegalArgumentException("Capacity must be greater then 200.");
        }

        if(ObjectUtils.isEmpty(request.name()) || ObjectUtils.isEmpty(request.city()) || ObjectUtils.isEmpty(request.capacity())) {
            throw new IllegalArgumentException("Fields cannot be empty");
        }

        if (venueRepository.existsByNameAndCityAndIdNot(request.name(), request.city(), id)) {
            throw new IllegalStateException("Another venue with this name already exists in " + request.city());
        }

        try {

            venue.setName(request.name());
            venue.setCity(request.city());
            venue.setCapacity(request.capacity());

            venueRepository.saveAndFlush(venue);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Conflict: A venue with this name already exists in " + request.city());
        }
    }

    //delete
    public void deleteVenue(Long id) {
       if(!venueRepository.existsById(id)) {
           throw new ResourceNotFoundException("Venue not found with id "+ id);
       }
       if(!eventRepository.existsById(id)){
           throw new IllegalStateException("Deletion failed: This venue has active events scheduled.");
       }
       venueRepository.deleteById(id);
    }

}