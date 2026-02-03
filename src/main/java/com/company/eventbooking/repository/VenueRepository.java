package com.company.eventbooking.repository;

import com.company.eventbooking.entity.Venue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue,Long> {
    boolean existsByNameAndCity(String name, String city);

    boolean existsByNameAndCityAndIdNot(@NotBlank(message = "Venue name is required") String name, @NotBlank(message = "City is required") String city, Long id);
}