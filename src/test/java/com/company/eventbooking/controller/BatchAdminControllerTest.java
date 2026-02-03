package com.company.eventbooking.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.http.ResponseEntity;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BatchAdminControllerTest {

    @Mock
    private JobOperator jobOperator;

    @InjectMocks
    private BatchAdminController batchAdminController;

    @Test
    void triggerJob_ShouldReturnExecutionIdAndCoverProperties() throws Exception {
        // GIVEN
        Long mockExecutionId = 123L;
        // ArgumentCaptor allows us to "catch" the Properties object to verify its contents
        ArgumentCaptor<Properties> propertiesCaptor = ArgumentCaptor.forClass(Properties.class);

        given(jobOperator.start(eq("dailyReportJob"), any(Properties.class)))
                .willReturn(mockExecutionId);

        // WHEN
        ResponseEntity<String> response = batchAdminController.triggerJob();

        // THEN
        // 1. Verify the HTTP Status and Response Body
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Job started! Execution ID: 123"));

        // 2. Verify the JobOperator was called with the correct Job Name
        // 3. Verify the Properties object was created and contains the "time" key
        then(jobOperator).should().start(eq("dailyReportJob"), propertiesCaptor.capture());

        Properties capturedProps = propertiesCaptor.getValue();
        assertTrue(capturedProps.containsKey("time"), "Properties must contain the 'time' parameter");

        // This ensures 100% coverage of the logic inside the method
    }
}