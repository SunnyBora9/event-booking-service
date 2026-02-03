package com.company.eventbooking.entity;

import com.company.eventbooking.model.enums.SeatCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "city"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;
    private Integer capacity;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();

    @PrePersist
    public void generateDefaultSeats() {
        if (this.seats == null || this.seats.isEmpty()) {
            List<String[]> defaultSeats = List.of(
                    new String[]{"A1", "PREMIUM"},
                    new String[]{"A2", "VIP"},
                    new String[]{"A3", "REGULAR"},
                    new String[]{"A4", "PREMIUM"},
                    new String[]{"A5", "VIP"},
                    new String[]{"A6", "REGULAR"}
            );

            for (String[] data : defaultSeats) {
                this.seats.add(Seat.builder()
                        .seatNo(data[0])
                        .seatCategory(SeatCategory.valueOf(data[1]))
                        .venue(this)
                        .build());
            }
        }
    }

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Event> events = new ArrayList<>();
}