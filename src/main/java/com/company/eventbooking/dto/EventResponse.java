package com.company.eventbooking.dto;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String eventCode,
        String name,
        LocalDateTime eventDate,
        Long venueId
) {}