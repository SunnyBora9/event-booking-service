package com.company.eventbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="idempotency_keys",uniqueConstraints={@UniqueConstraint(columnNames = "idempotencyKey")})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length=100)
    private String idempotencyKey;

    @OneToOne
    private Booking booking;

    @Column
    private LocalDateTime createdAt;

}