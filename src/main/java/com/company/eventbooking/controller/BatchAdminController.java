package com.company.eventbooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
public class BatchAdminController {
    private final JobOperator jobOperator;

    @PostMapping("/run-reports")
    public ResponseEntity<String> triggerJob() throws Exception {
        Properties props = new Properties();
        props.setProperty("time", String.valueOf(System.currentTimeMillis()));
        Long executionId = jobOperator.start("dailyReportJob", props);
        return ResponseEntity.ok("Job started! Execution ID: " + executionId);
    }
}
