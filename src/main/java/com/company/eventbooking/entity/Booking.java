package com.company.eventbooking.entity;

import com.company.eventbooking.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingCode;

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    private double totalPrice;

    @Column(nullable = false)
    private double discountAmount;

    @Column(unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length=20)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
