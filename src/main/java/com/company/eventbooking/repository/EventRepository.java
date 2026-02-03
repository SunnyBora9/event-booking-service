package com.company.eventbooking.repository;

import com.company.eventbooking.dto.OccupancyProjection;
import com.company.eventbooking.dto.VenueEventDTO;
import com.company.eventbooking.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = """

    SELECT v.id as venueId, v.name as venueName,
                                            e.id as eventId, e.event_code as eventCode,
                                            e.name as eventName, e.event_date as eventDate,
                                            s.id as seatId, s.seat_no as seatNo, s.seat_category as seatCategory
                                     FROM events e
                                     LEFT JOIN venues v ON e.venue_id = v.id
                                     LEFT JOIN seats s ON s.venue_id = v.id
                                     LEFT JOIN booking_seats bs ON bs.seat_id = s.id AND bs.event_id = e.id
                                     WHERE (bs.id IS NULL OR s.id IS NULL)
                                     ORDER BY v.name, e.event_date;
        """, nativeQuery = true)
    List<VenueEventDTO> findAllAvailableSeats();

    Optional<Event> findByNameIgnoreCase(String name);

    @Query("SELECT e.name as eventName, " +
            "(SELECT COUNT(s) FROM Seat s WHERE s.venue = e.venue) as totalSeats, " +
            "(SELECT COUNT(bs) FROM BookingSeat bs WHERE bs.event = e) as bookedSeats " +
            "FROM Event e")
    Page<OccupancyProjection> getOccupancyData(Pageable pageable);
}
