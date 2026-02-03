package com.company.eventbooking.service;

import com.company.eventbooking.entity.Booking;
import com.company.eventbooking.entity.CancellationRequest;
import com.company.eventbooking.entity.Event;
import com.company.eventbooking.entity.Venue;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CancellationRepository cancelationRepository;

    @Autowired
    private RefundService refundService;

    //Upload venues CSV
//    @Transactional
//    public void uploadVenues(MultipartFile file) throws IOException {
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(file.getInputStream()))) {
//            br.readLine();
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] data = line.split(",");
//                Venue venue = Venue.builder().name(data[0]).city(data[1]).capacity(Integer.parseInt(data[2])).build();
//                venueRepository.save(venue);
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to upload Venue CSV", e);
//        }
//
//    }

    @Transactional
    public void uploadVenues(MultipartFile file) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                String city = data[1];
                int capacity = Integer.parseInt(data[2]);

                // Check for duplicates before saving
                if (venueRepository.existsByNameAndCity(name, city)) {
                    log.warn("Skipping duplicate venue: {} in {}", name, city);
                    continue;
                }

                Venue venue = Venue.builder()
                        .name(name)
                        .city(city)
                        .capacity(capacity)
                        .build();
                venueRepository.save(venue);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload Venue CSV", e);
        }
    }

    //Upload events csv
    @Transactional
    public void uploadEvents(MultipartFile file) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Venue venue = venueRepository.findById(Long.parseLong(data[3])).orElseThrow(() -> new IllegalArgumentException("Venue not found"));

                Event event = Event.builder().eventCode(data[0]).name(data[1]).eventDate(LocalDateTime.parse(data[2])).venue(venue).build();
                eventRepository.save(event);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload Events CSV", e);
        }
    }

    //Approve cancellation(admin-controlled)
    @Transactional
    public void approveCancellation(Long bookingId){
        CancellationRequest request=cancelationRepository.findByBookingId(bookingId).orElseThrow(()->new IllegalArgumentException("Cancellation not found"));

        Booking booking=request.getBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        refundService.initiateRefund(bookingId);
    }

    //Reschedule Event
    @Transactional
    public void rescheduleEvent(Long eventId,String newDate){
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));

        event.setEventDate(LocalDateTime.parse(newDate));
        eventRepository.save(event);
    }


}
