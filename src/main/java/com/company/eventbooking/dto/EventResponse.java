package com.company.eventbooking.dto;


import java.time.LocalDateTime;
import java.util.List;
public record EventResponse(
        Long eventId,
        String eventCode,
        String eventName,
        LocalDateTime date,
        Long venueId,
        List<SeatResponse> availableSeats
) {
    public record SeatResponse(
            Long eventId,
            String seatNo,
            String category
    ) {}
}