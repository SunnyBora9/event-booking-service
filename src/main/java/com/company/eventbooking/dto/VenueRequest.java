package com.company.eventbooking.dto;

public record VenueRequest (
    String name,
    String city,
    Integer capacity
) {}
