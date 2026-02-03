package com.company.eventbooking.batch.model;

import java.time.LocalDateTime;

public record BookingReportRow (
    String bookingCode,
    String userEmail,
    String eventName,
    double totalPrice,
    String status,
    LocalDateTime createdAt
){}
