package com.company.eventbooking.dto;

import java.util.List;

public record BookingResponse(
        String bookingCode,
        List<String> messages,
        double finalPrice
) {}