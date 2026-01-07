package com.company.eventbooking.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Venue {
    private Long id;
    private String name;
    private String city;
    private Integer capacity;
}
