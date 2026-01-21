package com.company.eventbooking.dto;

import java.util.List;

public record BookingRequest(
    Long eventId,
    List<Long> seatIds,
    String promoCode
){}
