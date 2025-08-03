package com.arman.parkingservice.service;

import com.arman.parkingservice.criteria.SearchCriteria;
import com.arman.parkingservice.dto.PageResponseDto;
import com.arman.parkingservice.dto.booking.BookingRequestDto;
import com.arman.parkingservice.dto.booking.BookingResponse;
import com.arman.parkingservice.enums.BookingPeriod;
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
import org.springframework.data.domain.Page;
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
        if (bookingRequestDto.getStartTime().isAfter(bookingRequestDto.getEndTime())
                || bookingRequestDto.getStartTime().isEqual(bookingRequestDto.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot equal or come after end time");
        }

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

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking with the following id not found: " + id)
                );

        return bookingMapper.mapToResponse(booking);
    }

    public PageResponseDto<BookingResponse> getAllBookingsByResident(
            Long residentId,
            BookingPeriod period,
            SearchCriteria criteria
    ) {
        LocalDateTime now = LocalDateTime.now();

        Page<Booking> pageBooking = switch (period) {
            case PAST -> bookingRepository
                    .findByResident_IdAndEndTimeBeforeAndBookingStatus(
                            residentId,
                            now,
                            BookingStatus.COMPLETED,
                            criteria.buildPageRequest()
                    );

            case CURRENT -> bookingRepository
                    .findCurrentByResident(
                            residentId,
                            now,
                            List.of(BookingStatus.RESERVED, BookingStatus.ACTIVE),
                            criteria.buildPageRequest()
                    );
            case FUTURE -> bookingRepository
                    .findByResident_idAndStartTimeAfterAndBookingStatus(
                            residentId,
                            now,
                            BookingStatus.RESERVED,
                            criteria.buildPageRequest()
                    );
            case CANCELLED -> bookingRepository
                    .findByResident_IdAndBookingStatus(
                            residentId,
                            BookingStatus.CANCELLED,
                            criteria.buildPageRequest()
                    );
            case ALL -> bookingRepository
                    .findByResident_Id(residentId, criteria.buildPageRequest());
        };

        return PageResponseDto.from(pageBooking.map(booking -> {
            if (booking.getBookingStatus().equals(BookingStatus.CANCELLED)) return booking;
            else if (now.isAfter(booking.getEndTime())) {
                if (booking.getBookingStatus().equals(BookingStatus.RESERVED)) {
                    booking.setBookingStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(booking);
                } else if (booking.getBookingStatus().equals(BookingStatus.ACTIVE)) {
                    booking.setBookingStatus(BookingStatus.COMPLETED);
                    bookingRepository.save(booking);
                }
            }
            return booking;
        }).map(bookingMapper::mapToResponse));
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

    public BookingResponse park(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking with the following id not found: " + id)
                );
        if (!booking.getBookingStatus().equals(BookingStatus.RESERVED)) {
            throw new ResourceAlreadyUsedException("Booking " + id + " cannot be parked because its status is "
                    + booking.getBookingStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(booking.getStartTime())) {
            throw new IllegalArgumentException("The booking cannot be accessed, as the period did not start yet");
        } else if (now.isAfter(booking.getEndTime())) {
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            throw new IllegalArgumentException("The booking cannot be accessed, as the period did has ended");
        }

        booking.setBookingStatus(BookingStatus.ACTIVE);
        Booking savedBook = bookingRepository.save(booking);

        return bookingMapper.mapToResponse(savedBook);
    }

}

