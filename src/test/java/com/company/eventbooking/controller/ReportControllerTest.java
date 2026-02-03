package com.company.eventbooking.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private JobOperator jobLauncher;

    @Mock
    private Job bookingReportJob;

    @InjectMocks
    private ReportController reportController;

    @Test
    void generateBookingReport_ShouldReturnExecutionId() throws Exception {

        JobExecution mockExecution = mock(JobExecution.class);
        given(mockExecution.getId()).willReturn(12345L);

        given(jobLauncher.start(eq(bookingReportJob), any(JobParameters.class)))
                .willReturn(mockExecution);

        ResponseEntity<String> response = reportController.generateBookingReport();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("bookingReportJob executionId=12345"));

        then(jobLauncher).should().start(eq(bookingReportJob), any(JobParameters.class));
    }
}