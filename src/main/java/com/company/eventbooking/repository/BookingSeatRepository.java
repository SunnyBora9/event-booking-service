package com.company.eventbooking.repository;

import com.company.eventbooking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingSeatRepository extends JpaRepository<BookingSeat,Long> {
    boolean existsByEventIdAndSeatId(Long eventId,Long seatId);
}
