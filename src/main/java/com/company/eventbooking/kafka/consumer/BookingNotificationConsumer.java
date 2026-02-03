package com.company.eventbooking.kafka.consumer;

import com.company.eventbooking.kafka.event.BookingEvent;
import com.company.eventbooking.kafka.event.EventNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingNotificationConsumer {

    @KafkaListener(
            topics = "booking-events",
            groupId = "notification-group-v7",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleBookingEvent(BookingEvent event) {
        log.info("Processing booking notification for code: {}", event.bookingCode());

        if (event.useremail() == null || event.useremail().isBlank()) {
            log.error("Validation failed: Email is missing for booking {}", event.bookingCode());
            throw new RuntimeException("Email is null or empty, cannot send notification!");
        }

        log.info("✅ BOOKING CONFIRMATION SENT -> Email={}, Event={}, Code={}",
                event.useremail(), event.eventName(), event.bookingCode());
    }

    @KafkaListener(
            topics = "event-notifications",
            groupId = "event-update-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleEventNotification(EventNotification notification) {
        log.info("Processing {} for event: {}", notification.type(), notification.eventName());

        if (notification.type().equals("UPDATE")) {
            log.info("📢 EVENT UPDATE: {} - Message: {}", notification.eventName(), notification.message());
        } else if (notification.type().equals("REMINDER")) {
            log.info("⏰ EVENT REMINDER: {} - It's time to get ready!", notification.eventName());
        }

        log.info("✅ Notification sent for event ID: {}", notification.eventId());
    }

    @KafkaListener(
            topics = "booking-events-dlt",
            groupId = "dlq-monitoring-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleDLQ(
            BookingEvent failedEvent,
            @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String errorMessage,
            @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic,
            @Header(KafkaHeaders.DLT_ORIGINAL_OFFSET) byte[] offset) {

        log.error("🛑 DEAD LETTER QUEUE ALERT 🛑");
        log.error("Original Topic: {}", originalTopic);
        log.error("Failed Booking Code: {}", failedEvent.bookingCode());
        log.error("Failure Reason: {}", errorMessage);

    }

    @KafkaListener(topics = "event-notifications-dlt", groupId = "dlq-group")
    public void handleNotificationDLQ(EventNotification notification, @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String error) {
        log.error("🛑 Notification DLT: Event {} ({}) failed: {}",
                notification.eventName(), notification.type(), error);
    }
}
