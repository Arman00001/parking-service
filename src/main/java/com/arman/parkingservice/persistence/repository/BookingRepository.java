package com.arman.parkingservice.persistence.repository;

import com.arman.parkingservice.enums.BookingStatus;
import com.arman.parkingservice.persistence.entity.Booking;
import com.arman.parkingservice.persistence.entity.ParkingSpot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    Page<Booking> findByResident_IdAndEndTimeBeforeAndBookingStatus(Long residentId, LocalDateTime endTimeBefore, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findByResident_idAndStartTimeAfterAndBookingStatus(Long residentId, LocalDateTime startTimeAfter, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findByResident_IdAndBookingStatus(Long residentId, BookingStatus bookingStatus, Pageable pageable);

    Page<Booking> findByResident_Id(Long residentId, Pageable pageable);

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.resident.id = :residentId
            AND b.startTime <= :now
            AND b.endTime >= :now
            AND b.bookingStatus IN :statuses
""")
    Page<Booking> findCurrentByResident(Long residentId, LocalDateTime now, List<BookingStatus> statuses, Pageable pageable);
}
