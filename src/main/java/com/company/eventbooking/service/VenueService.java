package com.company.eventbooking.service;

import com.company.eventbooking.dto.VenueRequest;
import com.company.eventbooking.dto.VenueResponse;
import com.company.eventbooking.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public int createVenue(VenueRequest venueRequest) {
        return venueRepository.save(venueRequest);
    }

    public List<VenueResponse> listVenues() {
        return venueRepository.update();
    }

    public int updateVenue(Long id, VenueRequest venue) {
        return venueRepository.update(id, venue);
    }

    public int deleteVenue(Long id) {
        return venueRepository.delete(id);
    }

}