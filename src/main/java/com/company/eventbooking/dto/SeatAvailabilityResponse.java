package com.company.eventbooking.dto;

public record SeatAvailabilityResponse (
    Long seatId,
    String seatNumber,
    String category,
    boolean available)
{}
