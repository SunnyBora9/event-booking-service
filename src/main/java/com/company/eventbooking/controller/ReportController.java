package com.company.eventbooking.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final JobOperator jobLauncher;
    private final Job bookingReportJob;

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> generateBookingReport() throws Exception  {

        JobParameters params = new JobParametersBuilder().addLong("run_id",System.currentTimeMillis()).toJobParameters();
        JobExecution execution=jobLauncher.start(bookingReportJob,params);

        return ResponseEntity.ok("bookingReportJob executionId="+execution.getId());

    }
}
