package com.company.eventbooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record VenueRequest(
        @NotBlank(message = "Venue name is required")
        String name,

        @NotBlank(message = "City is required")
        String city,

        @Min(value = 200, message = "Capacity must be at least 200")
        Integer capacity
) {}
