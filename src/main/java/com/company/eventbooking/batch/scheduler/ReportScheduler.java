package com.company.eventbooking.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportScheduler {

    private final JobOperator jobLauncher; // This is the standard interface
    private final Job dailyReportJob;

    @Scheduled(cron = "0 0 0 * * *") // Midnight
    public void runJob() {
        try {
            log.info("Starting Daily Report Job at {}", LocalDateTime.now());

            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            // jobLauncher.run is the standard method in v5.x and v6.x
            jobLauncher.start(dailyReportJob, params);

        } catch (Exception e) {
            log.error("Error while running Daily Report Job: {}", e.getMessage());
        }
    }
}