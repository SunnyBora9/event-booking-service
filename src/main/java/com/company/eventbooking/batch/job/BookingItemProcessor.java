package com.company.eventbooking.batch.job;

import com.company.eventbooking.batch.model.BookingReportRow;
import com.company.eventbooking.entity.Booking;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingItemProcessor implements ItemProcessor<Booking, BookingReportRow> {

    @Override
    public BookingReportRow process(Booking booking) throws Exception {
       return new BookingReportRow(
               booking.getBookingCode(),
               booking.getUser().getEmail(),
               booking.getEvent().getName(),
               booking.getTotalPrice(),
               booking.getStatus().name(),
               booking.getCreatedAt()
       );
    }
}
