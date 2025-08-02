package com.arman.parkingservice.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "parking_spot", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code", "community_id"})
})
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;
}
