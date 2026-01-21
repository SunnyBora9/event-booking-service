package com.company.eventbooking.repository;

import com.company.eventbooking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRepository extends JpaRepository<Event,Long> {


}
