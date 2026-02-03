package com.company.eventbooking.controller;


import com.company.eventbooking.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void uploadVenues_ShouldReturnSuccess() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "venues.csv", "text/csv", "name,city,capacity".getBytes());

        willDoNothing().given(adminService).uploadVenues(file);

        ResponseEntity<String> response = adminController.uploadVenues(file);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Venues uploaded successfully", response.getBody());

        then(adminService).should(times(1)).uploadVenues(file);
    }

    @Test
    void approveCancellation_ShouldReturnSuccess() throws IOException {
        Long bookingId = 1L;
        willDoNothing().given(adminService).approveCancellation(bookingId);

        ResponseEntity<String> response = adminController.approveCancellation(bookingId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Cancellation approved", response.getBody());
        then(adminService).should().approveCancellation(bookingId);
    }

    @Test
    void rescheduleEvent_ShouldReturnSuccess() throws IOException {
        Long eventId = 1L;
        String newDate = "2026-12-25T10:00:00";
        willDoNothing().given(adminService).rescheduleEvent(eventId, newDate);

        ResponseEntity<String> response = adminController.rescheduleEvent(eventId, newDate);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Event rescheduled successfully", response.getBody());
        then(adminService).should().rescheduleEvent(eventId, newDate);
    }

    @Test
    void uploadEvents_ShouldReturnSuccess() throws IOException {

        MockMultipartFile file = new MockMultipartFile(
                "file", "events.csv", "text/csv", "name,date,venueId".getBytes());

        willDoNothing().given(adminService).uploadEvents(file);

        ResponseEntity<String> response = adminController.uploadEvents(file);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Events uploaded successfully", response.getBody());

        then(adminService).should(times(1)).uploadEvents(file);
    }

    @Test
    void uploadEvents_ShouldReturnBadRequest_WhenFileIsEmpty() throws IOException {

        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "text/csv", new byte[0]);

        ResponseEntity<String> response = adminController.uploadEvents(emptyFile);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("CSV file is required", response.getBody());

        then(adminService).shouldHaveNoInteractions();
    }

    @Test
    void uploadVenues_ShouldThrowException_WhenFileIsEmpty() throws IOException {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "text/csv", new byte[0]);

        ResponseEntity<String> response = adminController.uploadVenues(emptyFile);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("CSV file is required", response.getBody());
        then(adminService).shouldHaveNoInteractions();
    }

}
