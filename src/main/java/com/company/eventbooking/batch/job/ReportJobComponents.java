package com.company.eventbooking.batch.job;

import com.company.eventbooking.batch.model.BookingReportRow;
import com.company.eventbooking.batch.model.OccupancyReportRow;
import com.company.eventbooking.dto.OccupancyProjection;
import com.company.eventbooking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ReportJobComponents {

    private final EventRepository eventRepository;

    @Bean
    public RepositoryItemReader<OccupancyProjection> occupancyReader(EventRepository eventRepository) {
        return new RepositoryItemReaderBuilder<OccupancyProjection>()
                .name("occupancyReader")
                .repository(eventRepository)
                .methodName("getOccupancyData")
                .pageSize(10)
                .sorts(Map.of("eventName", org.springframework.data.domain.Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<OccupancyProjection, OccupancyReportRow> occupancyProcessor() {
        return p -> {
            double total = p.getTotalSeats() != null ? p.getTotalSeats() : 0.0;
            double booked = p.getBookedSeats() != null ? p.getBookedSeats() : 0.0;
            double pct = (total > 0) ? (booked / total) * 100 : 0.0;

            return new OccupancyReportRow(
                    p.getEventName(),
                    p.getTotalSeats(),
                    p.getBookedSeats(),
                    Math.round(pct * 100.0) / 100.0
            );
        };
    }

    @Bean
    public FlatFileItemWriter<OccupancyReportRow> occupancyCsvWriter() {
        return new FlatFileItemWriterBuilder<OccupancyReportRow>()
                .name("occupancyCsvWriter")
                .resource(new FileSystemResource("/reports/occupancy_report.csv"))
                // Use lineAggregator directly on the builder, not on .delimited()
                .lineAggregator(item ->
                        item.eventName() + "," +
                                item.totalSeats() + "," +
                                item.bookedSeats() + "," +
                                item.occupancyPercentage()
                )
                .headerCallback(w -> w.write("Event,TotalSeats,BookedSeats,OccupancyPercentage"))
                .build();
    }
}
