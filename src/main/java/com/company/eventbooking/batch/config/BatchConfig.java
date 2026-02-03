package com.company.eventbooking.batch.config;

import com.company.eventbooking.batch.model.BookingReportRow;
import com.company.eventbooking.batch.model.OccupancyReportRow;
import com.company.eventbooking.dto.OccupancyProjection;
import com.company.eventbooking.entity.Booking;
import com.company.eventbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;

    @Bean
    public Job bookingReportJob(Step bookingReportStep) {
        return new JobBuilder("bookingReportJob",jobRepository).start(bookingReportStep).build();
    }

    @Bean
    public Step bookingReportStep(ItemReader<Booking> reader, ItemProcessor<Booking, BookingReportRow> processor, ItemWriter<BookingReportRow> writer) {
        return new StepBuilder("bookingReportStep",jobRepository)
                .<Booking,BookingReportRow> chunk(4)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job dailyReportJob(JobRepository jobRepository, Step bookingStep, Step occupancyStep) {
        return new JobBuilder("dailyReportJob", jobRepository)
                .start(bookingStep)
                .next(occupancyStep)
                .build();
    }

    @Bean
    public Step bookingStep(JobRepository jobRepository, PlatformTransactionManager tm,
                            JpaPagingItemReader<Booking> reader,
                            ItemProcessor<Booking, BookingReportRow> proc,
                            FlatFileItemWriter<BookingReportRow> writer) {
        return new StepBuilder("bookingStep", jobRepository)
                .<Booking, BookingReportRow>chunk(10)
                .reader(reader).processor(proc).writer(writer).build();
    }

    @Bean
    public Step occupancyStep(JobRepository jobRepository, PlatformTransactionManager tm,
                              RepositoryItemReader<OccupancyProjection> reader,
                              ItemProcessor<OccupancyProjection, OccupancyReportRow> proc,
                              FlatFileItemWriter<OccupancyReportRow> writer) {
        return new StepBuilder("occupancyStep", jobRepository)
                .<OccupancyProjection, OccupancyReportRow>chunk(10)
                .reader(reader).processor(proc).writer(writer).build();
    }

}