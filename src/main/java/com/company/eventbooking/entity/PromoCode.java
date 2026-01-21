package com.company.eventbooking.entity;

import com.company.eventbooking.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="promo_codes",
        indexes={@Index(name="idx_promo_code",columnList="code"),
                @Index(name="idx_promo_active",columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true,nullable=false)
    private String code;

    @Column(nullable=false)
    private int discountPercentage;

    @Column(nullable=false)
    private LocalDateTime validTill;

    @Column(nullable=false)
    private boolean active;

}