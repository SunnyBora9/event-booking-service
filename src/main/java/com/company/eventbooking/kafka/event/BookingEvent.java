package com.company.eventbooking.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record BookingEvent(Long bookingId, String bookingCode, String useremail, String eventName, LocalDateTime bookingTime) {
}
