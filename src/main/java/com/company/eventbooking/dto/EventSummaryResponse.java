package com.company.eventbooking.dto;

import java.time.LocalDateTime;

public record EventSummaryResponse(
        Long eventId,
        String eventCode,
        String eventName,
        LocalDateTime eventDate
) {}