package com.arman.parkingservice.service;

import com.arman.parkingservice.dto.booking.BookingRequestDto;
import com.arman.parkingservice.dto.booking.BookingResponse;
import com.arman.parkingservice.enums.BookingStatus;
import com.arman.parkingservice.exception.ResourceAlreadyUsedException;
import com.arman.parkingservice.exception.ResourceNotFoundException;
import com.arman.parkingservice.mapper.BookingMapper;
import com.arman.parkingservice.persistence.entity.Booking;
import com.arman.parkingservice.persistence.entity.ParkingSpot;
import com.arman.parkingservice.persistence.entity.Resident;
import com.arman.parkingservice.persistence.repository.BookingRepository;
import com.arman.parkingservice.persistence.repository.ParkingSpotRepository;
import com.arman.parkingservice.persistence.repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ResidentRepository residentRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final BookingMapper bookingMapper;

    public BookingResponse addBooking(BookingRequestDto bookingRequestDto) {
        Resident resident = residentRepository.findById(bookingRequestDto.getResidentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resident with the following id not found: "
                                + bookingRequestDto.getResidentId())
                );

        ParkingSpot spot = parkingSpotRepository.findById(bookingRequestDto.getParkingSpotId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Parking spot with the following id not found: "
                                + bookingRequestDto.getParkingSpotId()));

        if (resident.getCommunity().getId().equals(spot.getCommunity().getId())) {
            checkOverlap(spot, bookingRequestDto.getStartTime(), bookingRequestDto.getEndTime());

            Booking booking = bookingMapper.mapRequestToBooking(bookingRequestDto, resident, spot);
            Booking savedBooking = bookingRepository.save(booking);


            return bookingMapper.mapToResponse(savedBooking);
        }
        throw new IllegalArgumentException(
                "Resident " + resident.getId() +
                        " is not part of community " +
                        spot.getCommunity().getId()
        );
    }

    private void checkOverlap(ParkingSpot spot, LocalDateTime start, LocalDateTime end) {
        boolean clash = bookingRepository.existsByParkingSpotAndBookingStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                spot,
                List.of(BookingStatus.RESERVED, BookingStatus.ACTIVE),
                end,
                start
        );
        if (clash) {
            throw new ResourceAlreadyUsedException(
                    "This parking spot is already booked between " +
                            start + " and " + end
            );
        }
    }
}

