package com.arman.parkingservice.persistence.entity;

import com.arman.parkingservice.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @ManyToOne(optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private ParkingSpot parkingSpot;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus;

    @Column(name = "actual_start_date")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_date")
    private LocalDateTime actualEndTime;
}
