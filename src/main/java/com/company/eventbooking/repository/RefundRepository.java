package com.company.eventbooking.repository;

import com.company.eventbooking.entity.Refund;
import com.company.eventbooking.model.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund,Long> {

    Optional<Refund> findByBookingId(Long bookingId);

    List<Refund> findByStatus(RefundStatus status);

    boolean existsByBookingId(Long bookingId);
}
