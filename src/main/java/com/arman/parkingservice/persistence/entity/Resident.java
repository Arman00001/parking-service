package com.arman.parkingservice.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "resident", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "community_id"})
})
public class Resident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne(optional = false)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
}