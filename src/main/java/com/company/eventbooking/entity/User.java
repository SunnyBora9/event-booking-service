package com.company.eventbooking.entity;

import com.company.eventbooking.util.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Convert(converter= EncryptedStringConverter.class)
    @Column(unique = true, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Builder.Default
    private String role="USERS";


}
