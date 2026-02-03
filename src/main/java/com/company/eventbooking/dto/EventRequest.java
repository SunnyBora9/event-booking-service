package com.company.eventbooking.dto;

import java.time.LocalDateTime;

public record EventRequest (
    String eventCode,
    String name,
    LocalDateTime eventDate,
    Long venueId
){}