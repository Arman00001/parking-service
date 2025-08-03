package com.arman.parkingservice.service;

import com.arman.parkingservice.criteria.SearchCriteria;
import com.arman.parkingservice.dto.PageResponseDto;
import com.arman.parkingservice.dto.booking.BookingRequestDto;
import com.arman.parkingservice.dto.booking.BookingResponse;
import com.arman.parkingservice.enums.BookingPeriod;
import com.arman.parkingservice.enums.BookingStatus;
import com.arman.parkingservice.exception.*;
import com.arman.parkingservice.mapper.BookingMapper;
import com.arman.parkingservice.persistence.entity.Booking;
import com.arman.parkingservice.persistence.entity.ParkingSpot;
import com.arman.parkingservice.persistence.entity.Resident;
import com.arman.parkingservice.persistence.repository.BookingRepository;
import com.arman.parkingservice.persistence.repository.ParkingSpotRepository;
import com.arman.parkingservice.persistence.repository.ResidentRepository;
import com.arman.parkingservice.exception.BookingEndedException;
import jakarta.transaction.Transactional;
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

    /**
     * Creates a new booking for the given resident and parking spot
     * <p>
     * Validates the resident and parking spot, checks for collisions
     * and if successful, creates a booking.
     * </p>
     *
     * @param bookingRequestDto DTO containing detailed information about the booking
     * @return The saved {@link BookingResponse} with booking details
     * @throws BookingCommunityMismatchException if resident is not from the community
     * @throws InvalidBookingPeriodException     if given incorrect time range or
     * @throws ResourceNotFoundException    if given incorrect IDs'
     * @throws ResourceAlreadyUsedException if the given time slot has bookings
     */
    public BookingResponse addBooking(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStartTime().isAfter(bookingRequestDto.getEndTime())
                || bookingRequestDto.getStartTime().isEqual(bookingRequestDto.getEndTime())) {
            throw new InvalidBookingPeriodException("Start time cannot equal or come after end time");
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
        throw new BookingCommunityMismatchException(
                "Resident " + resident.getId() +
                        " is not part of community " +
                        spot.getCommunity().getId()
        );
    }

    /**
     * Fetches a book by its unique identifier
     *
     * @param id the booking's ID
     * @return {@link BookingResponse} with booking details
     * @throws ResourceNotFoundException if no {@link Booking} found with the given ID
     */
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking with the following id not found: " + id)
                );

        return bookingMapper.mapToResponse(booking);
    }

    /**
     * Retrieves a paginated list of bookings by the resident, given the period
     * <p>
     * Depending on {@code period}, returns past(COMPLETED), current(RESERVED or ACTIVE),
     * future(RESERVED), cancelled or all bookings. Automatically updates every booking's status
     * if needed.
     * </p>
     *
     * @param residentId the resident's ID
     * @param period     the given period
     * @param criteria   given criteria for pagination
     * @return {@link PageResponseDto}&lt;{@link BookingResponse}&gt; containing the
     * requested page of bookings and pagination metadata
     */
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


    /**
     * Marks a RESERVED booking as ACTIVE (parked), if called during its window
     * <p>
     * Validates booking's existence and it being in state RESERVED, also checking the current time
     * being between startTime and endTime. Updates status to ACTIVE and records actualStartTime
     * </p>
     *
     * @param id the booking's ID
     * @return The updated {@link BookingResponse}
     * @throws ResourceNotFoundException    if no Booking is found with the given ID
     * @throws ResourceAlreadyUsedException if the booking is not in RESERVED state
     * @throws BookingNotStartedException        if called before startTime
     * @throws BookingExpiredException  if called after endTime
     */
    @Transactional
    public BookingResponse park(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking with the following id not found: " + id)
                );
        if (!booking.getBookingStatus().equals(BookingStatus.RESERVED)) {
            throw new BookingNotReservedException("Booking " + id + " cannot be parked because its status is "
                    + booking.getBookingStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        validateWithinBookingWindow(now, booking);

        booking.setBookingStatus(BookingStatus.ACTIVE);
        booking.setActualStartTime(now);
        Booking savedBook = bookingRepository.save(booking);

        return bookingMapper.mapToResponse(savedBook);
    }

    /**
     * Completes an ACTIVE booking if called during it's time window
     * <p>
     * Validates booking's existence and current time being in given timeframe.
     * Updates status to COMPLETED and records actualEndTime.
     * </p>
     *
     * @param id the booking's ID
     * @return Updated {@link BookingResponse}
     * @throws ResourceNotFoundException if no Booking is found with the given ID
     * @throws BookingNotActiveException     if booking is not ACTIVE, or called
     *                                   before startTime or after endTime
     */
    @Transactional
    public BookingResponse release(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking with the following id not found: " + id)
                );
        if (!booking.getBookingStatus().equals(BookingStatus.ACTIVE)) {
            throw new BookingNotActiveException("Booking " + id + " cannot be released because its status is "
                    + booking.getBookingStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        validateWithinBookingWindow(now, booking);

        booking.setBookingStatus(BookingStatus.COMPLETED);
        booking.setActualEndTime(now);
        Booking savedBook = bookingRepository.save(booking);

        return bookingMapper.mapToResponse(savedBook);
    }

    /**
     * Cancels a RESERVED booking.
     * <p>
     * Validates that the booking exists and is in state RESERVED. Updates status to CANCELLED.
     *
     * @param id the booking’s ID
     * @return The updated {@link BookingResponse}
     * @throws ResourceNotFoundException if no Booking is found with the given ID
     * @throws BookingEndedException     if booking is already CANCELLED or not in RESERVED state
     */
    public BookingResponse cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking with the following id not found: " + id)
                );
        if (booking.getBookingStatus().equals(BookingStatus.CANCELLED)) {
            throw new BookingEndedException("Booking " + id + " is already cancelled");
        }

        if (!booking.getBookingStatus().equals(BookingStatus.RESERVED)) {
            throw new BookingNotReservedException("Booking " + id + " cannot be cancelled because its status is "
                    + booking.getBookingStatus());
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking savedBook = bookingRepository.save(booking);

        return bookingMapper.mapToResponse(savedBook);
    }

    private void validateWithinBookingWindow(LocalDateTime now, Booking booking) {
        if (now.isBefore(booking.getStartTime())) {
            throw new BookingNotStartedException("The booking cannot be accessed, as the period did not start yet");
        } else if (now.isAfter(booking.getEndTime())) {
            booking.setBookingStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            throw new BookingExpiredException("The booking cannot be accessed, as the period has ended");
        }
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

