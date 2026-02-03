package com.company.eventbooking.controller;

import com.company.eventbooking.dto.BookingRequest;
import com.company.eventbooking.dto.BookingResponse;
import com.company.eventbooking.kafka.event.BookingEvent;
import com.company.eventbooking.service.BookingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
@Slf4j
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping
    public ResponseEntity<BookingResponse> book(@RequestBody BookingRequest bookingRequest, Authentication authentication) {
        BookingResponse response = bookingService.createBooking(authentication.getName(), bookingRequest);

        if (response.bookingCode() == null) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/simulate")
    public Map<String, Object> simulateConcurrentBooking(@RequestBody BookingRequest bookingRequest, Authentication authentication) {
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    // Call your service
                    BookingResponse response = bookingService.createBooking(authentication.getName(), bookingRequest);

                    // Logic to check if this was a fresh booking or a duplicate retrieval
                    boolean isDuplicate = response.messages().stream()
                            .anyMatch(m -> m.contains("Duplicate request detected."));

                    if (isDuplicate) {
                        return "Thread-" + threadId + " -> 🛡️ IDEMPOTENCY APPLIED (Retrieved: " + response.bookingCode() + ")";
                    } else {
                        return "Thread-" + threadId + " -> 🥇 ORIGINAL WINNER (Created: " + response.bookingCode() + ")";
                    }
                } catch (Exception e) {
                    // This handles the 'Duplicate entry' DB errors shown in your logs
                    return "❌ BLOCKED BY DB CONSTRAINT (Race Condition Defended)";
                }
            }, executor));
        }

        // Wait and gather results
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<String> resultsList = futures.stream().map(CompletableFuture::join).toList();
        executor.shutdown();

        // Final Structured Response for Swagger
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("summary", "Simulated 10 concurrent hits for User: " + authentication.getName());
        report.put("duration_ms", System.currentTimeMillis() - startTime);
        report.put("leaderboard", resultsList);

        return report;
    }

    @PostMapping("/simulate-multiple")
    public Map<String, Object> simulateTwoUsers(
            @RequestBody BookingRequest bookingRequest,
            Authentication currentAuth,
            @RequestParam String secondUserEmail
    ) {
        int numberOfThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        futures.add(CompletableFuture.supplyAsync(() -> {
            try {
                // For the first thread, we use the existing authentication context
                BookingResponse response = bookingService.createBooking(currentAuth.getName(), bookingRequest);
                return formatResult("Thread-Current (" + currentAuth.getName() + ")", response);
            } catch (Exception e) {
                return "Thread-Current -> ❌ BLOCKED: " + e.getMessage();
            }
        }, executor));

        futures.add(CompletableFuture.supplyAsync(() -> {
            try {

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        secondUserEmail, null, currentAuth.getAuthorities());
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);

                BookingResponse response = bookingService.createBooking(secondUserEmail, bookingRequest);

                SecurityContextHolder.clearContext();

                return formatResult("Thread-Secondary (" + secondUserEmail + ")", response);
            } catch (Exception e) {
                return "Thread-Secondary -> ❌ BLOCKED: " + e.getMessage();
            }
        }, executor));

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<String> resultsList = futures.stream().map(CompletableFuture::join).toList();
        executor.shutdown();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("summary", "Dual-User Battle Simulation");
        report.put("duration_ms", System.currentTimeMillis() - startTime);
        report.put("leaderboard", resultsList);

        return report;
    }

    private String formatResult(String threadName, BookingResponse response) {
        boolean isDuplicate = response.messages().stream()
                .anyMatch(m -> m.contains("Duplicate request detected."));

        if (isDuplicate) {
            return threadName + " -> 🛡️ IDEMPOTENCY/TAKEN (Booking: " + response.bookingCode() + ")";
        } else if (response.bookingCode() == null) {
            return threadName + " -> ❌ FAILED (All seats taken)";
        } else {
            return threadName + " -> 🥇 ORIGINAL WINNER (Created: " + response.bookingCode() + ")";
        }
    }


    @PostMapping("/send-failed-event")
    public String sendFailedEvent() {
        BookingEvent failedEvent = new BookingEvent(
                999L, "DLQ-TEST-001", null, "Test Event", LocalDateTime.now());

        kafkaTemplate.send("booking-events", failedEvent);
        return "Malformed message sent to 'booking-events'. Watch logs for retries and DLQ move.";
    }
}