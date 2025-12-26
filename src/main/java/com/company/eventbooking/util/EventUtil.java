package com.company.eventbooking.util;

public class EventUtil<T> {

    public String normalizeEventData(T input) {
        if(input==null){
            throw new IllegalArgumentException("Event data cannot be null");
        }

        return input.toString().trim().toUpperCase().replaceAll("[^A-Z0-9]", "").replaceAll("\\s+", "_");
    }

    public String generateEventData(T eventName) {
       String normalizedName = normalizeEventData(eventName);
       return normalizedName + "_" + System.currentTimeMillis();
    }
}