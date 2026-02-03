package com.company.eventbooking.kafka.event;

import java.time.LocalDateTime;

public record EventNotification(
        Long eventId,
        String eventName,
        String message,
        String type,
        LocalDateTime timestamp
) {}