package com.company.eventbooking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="booking_seats",uniqueConstraints = {@UniqueConstraint(columnNames={"event_id","seat_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Booking booking;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Seat seat;

}