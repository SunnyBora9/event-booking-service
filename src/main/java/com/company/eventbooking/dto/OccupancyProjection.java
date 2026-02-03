package com.company.eventbooking.dto;

public interface OccupancyProjection {
    String getEventName();
    Long getTotalSeats();
    Long getBookedSeats();
}
