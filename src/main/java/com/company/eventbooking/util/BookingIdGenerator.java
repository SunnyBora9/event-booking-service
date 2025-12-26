package com.company.eventbooking.util;

import java.time.Instant;

public class BookingIdGenerator {
    private static long sequence=0;

    private BookingIdGenerator() {}

    public static synchronized String generateBookingId() {
        long timeStamp = Instant.now().toEpochMilli();
        sequence++;

        return "BOOK"+timeStamp+"-"+sequence;
    }
}
