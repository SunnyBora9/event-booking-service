package com.company.eventbooking.entity;

import com.company.eventbooking.model.enums.SeatCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="seats")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNo;

    @Enumerated(EnumType.STRING)
    private SeatCategory seatCategory;

    @ManyToOne
    @JoinColumn(name="venue_id",nullable = false)
    private Venue venue;
}
