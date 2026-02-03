package com.company.eventbooking.batch.job;


import com.company.eventbooking.batch.model.BookingReportRow;
import com.company.eventbooking.entity.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@Slf4j
public class BookingCsvWriter {

    @Bean
    public FlatFileItemWriter<BookingReportRow> bookingWriter() {
        DelimitedLineAggregator<BookingReportRow> aggregator=new DelimitedLineAggregator<>();
        FlatFileItemWriter<BookingReportRow> writer=new FlatFileItemWriter<>(aggregator);
        writer.setName("bookingWriter");
        writer.setResource(new FileSystemResource("/reports/booking-report.csv"));
        writer.setHeaderCallback(w->w.write("BookingCode,UserEmail,EventName,TotalPrice,Status,CreatedAt")
        );
        aggregator.setDelimiter(",");
        BeanWrapperFieldExtractor<BookingReportRow> extractor=new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"bookingCode","userEmail","eventName","totalPrice","status","createdAt"});

        aggregator.setFieldExtractor(extractor);
        writer.setLineAggregator(aggregator);
        return writer;

    }
}
