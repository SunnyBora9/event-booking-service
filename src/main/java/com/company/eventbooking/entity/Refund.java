package com.company.eventbooking.entity;

import com.company.eventbooking.model.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="refunds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Booking booking;

    private double amount;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    private LocalDateTime initiatedAt;

}
