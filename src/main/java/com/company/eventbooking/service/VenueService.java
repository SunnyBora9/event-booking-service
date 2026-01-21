package com.company.eventbooking.service;

import com.company.eventbooking.dto.VenueRequest;
import com.company.eventbooking.dto.VenueResponse;
import com.company.eventbooking.entity.Venue;
import com.company.eventbooking.exception.ResourceNotFoundException;
import com.company.eventbooking.repository.EventRepository;
import com.company.eventbooking.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    @Autowired
    private VenueRepository venueRepository;
    @Autowired
    private EventRepository eventRepository;

    public void createVenue(VenueRequest venueRequest) {
        Venue venue = new Venue();
        venue.setCapacity(venueRequest.capacity());
        venue.setName(venueRequest.name());
        venue.setCity(venueRequest.city());

        venueRepository.save(venue);
    }

    //read
    public List<VenueResponse> listVenues() {
        return venueRepository.findAll().stream().map(this::toResponse).toList();
    }

    //read by id
    public VenueResponse getVenueById(Long id) {
        Venue venue=venueRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Venue not found with id "+ id));
        return toResponse(venue);

    }

    //update
    public void updateVenue(Long id, VenueRequest request) {
        Venue venue=venueRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Venue not found with id "+ id));
        venue.setName(request.name());
        venue.setCity(request.city());
        venue.setCapacity(request.capacity());
        venueRepository.save(venue);
    }

    //delete
    public void deleteVenue(Long id) {
       if(!venueRepository.existsById(id)) {
           throw new ResourceNotFoundException("Venue not found with id "+ id);
       }
       if(eventRepository.existsById(id)){
           throw new IllegalStateException("Cannot delete venue because events are associated with it");
       }
       venueRepository.deleteById(id);
    }

    //mapper
    private VenueResponse toResponse(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getCapacity()
        );
    }
}