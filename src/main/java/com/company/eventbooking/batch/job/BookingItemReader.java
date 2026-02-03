package com.company.eventbooking.batch.job;


import com.company.eventbooking.entity.Booking;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class BookingItemReader {

    @Bean
    public JpaPagingItemReader<Booking> bookingReader(EntityManagerFactory entityManagerFactory) {
        // Only fetch records created since yesterday
        LocalDateTime dailyThreshold = LocalDateTime.now().minusDays(1);

        return new JpaPagingItemReaderBuilder<Booking>()
                .name("bookingReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT b FROM Booking b WHERE b.createdAt >= :threshold")
                .parameterValues(Map.of("threshold", dailyThreshold))
                .pageSize(10)
                .build();
    }
}
