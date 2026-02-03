package com.company.eventbooking.kafka.producer;

import com.company.eventbooking.kafka.event.BookingEvent;
import com.company.eventbooking.kafka.event.EventNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String BOOKING_TOPIC = "booking-events";
    private static final String NOTIFICATION_TOPIC = "event-notifications";

    public void sendBookingEvent(BookingEvent bookingEvent) {
        kafkaTemplate.send(BOOKING_TOPIC, bookingEvent.bookingCode(), bookingEvent);
        log.info("Sent booking event for code: {}", bookingEvent.bookingCode());
    }

    public void sendEventNotification(EventNotification notification) {
        kafkaTemplate.send(NOTIFICATION_TOPIC, String.valueOf(notification.eventId()), notification);
        log.info("Sent {} notification for event: {}", notification.type(), notification.eventName());
    }


}
