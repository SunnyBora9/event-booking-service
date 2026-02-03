package com.company.eventbooking.dto;

import java.util.List;

public record VenueResponse( Long venueId,
                             String venueName,
                             String city,
                             Integer capacity,
                             List<EventSummaryResponse> events) {}
