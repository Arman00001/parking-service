package com.arman.parkingservice.persistence.repository;

import com.arman.parkingservice.enums.BookingStatus;
import com.arman.parkingservice.persistence.entity.Booking;
import com.arman.parkingservice.persistence.entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    Boolean existsByParkingSpotAndBookingStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            ParkingSpot parkingSpot,
            List<BookingStatus> bookingStatuses,
            LocalDateTime startTimeIsLessThan,
            LocalDateTime endTimeIsGreaterThan
    );
}
