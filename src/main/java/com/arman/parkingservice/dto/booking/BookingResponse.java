package com.arman.parkingservice.dto.booking;

import com.arman.parkingservice.dto.parkingspot.ParkingSpotResponse;
import com.arman.parkingservice.dto.resident.ResidentResponse;
import com.arman.parkingservice.enums.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponse {
    private Long bookingId;

    private ResidentResponse resident;

    private ParkingSpotResponse parkingSpot;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BookingStatus bookingStatus;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;
}
