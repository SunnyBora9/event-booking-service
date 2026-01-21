package com.company.eventbooking.util;

import com.company.eventbooking.model.enums.SeatCategory;

public class SeatPricingUtil {

    public static double getPrice(SeatCategory seatCategory) {
        return switch (seatCategory) {
            case REGULAR -> 300;
            case PREMIUM -> 600;
            case VIP -> 1000;
        };
    }
}
