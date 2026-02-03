package com.company.eventbooking.batch.model;

public record OccupancyReportRow(String eventName, Long totalSeats, Long bookedSeats, Double occupancyPercentage) {}

