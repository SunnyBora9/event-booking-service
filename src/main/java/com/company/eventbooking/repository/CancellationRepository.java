package com.company.eventbooking.repository;

import com.company.eventbooking.entity.CancellationRequest;
import com.company.eventbooking.model.enums.BookingStatus;
import com.company.eventbooking.service.CancellationService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CancellationRepository extends JpaRepository<CancellationRequest,Long> {

}
