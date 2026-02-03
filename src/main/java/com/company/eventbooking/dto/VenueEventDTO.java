package com.company.eventbooking.dto;

import java.time.LocalDateTime;

public interface VenueEventDTO {
    Long getVenueId();
    String getVenueName();
    Long getEventId();
    String getEventCode();
    String getEventName();
    LocalDateTime getEventDate();
    Long getSeatId();
    String getSeatNo();
    String getSeatCategory();
}
