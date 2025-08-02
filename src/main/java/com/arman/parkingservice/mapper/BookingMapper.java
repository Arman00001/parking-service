package com.arman.parkingservice.mapper;

import com.arman.parkingservice.dto.booking.BookingRequestDto;
import com.arman.parkingservice.dto.booking.BookingResponse;
import com.arman.parkingservice.enums.BookingStatus;
import com.arman.parkingservice.persistence.entity.Booking;
import com.arman.parkingservice.persistence.entity.ParkingSpot;
import com.arman.parkingservice.persistence.entity.Resident;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ResidentMapper residentMapper;
    private final ParkingSpotMapper parkingSpotMapper;

    public Booking mapRequestToBooking(BookingRequestDto bookingRequestDto, Resident resident, ParkingSpot spot) {
        Booking booking = new Booking();

        booking.setResident(resident);
        booking.setParkingSpot(spot);
        booking.setStartTime(bookingRequestDto.getStartTime());
        booking.setEndTime(bookingRequestDto.getEndTime());
        booking.setBookingStatus(BookingStatus.RESERVED);

        return booking;
    }

    public BookingResponse mapToResponse(Booking savedBooking) {
        BookingResponse bookingResponse = new BookingResponse();

        bookingResponse.setBookingId(savedBooking.getId());
        bookingResponse.setResident(residentMapper.mapToResponse(savedBooking.getResident()));
        bookingResponse.setParkingSpot(parkingSpotMapper.mapToResponse(savedBooking.getParkingSpot()));
        bookingResponse.setStartTime(savedBooking.getStartTime());
        bookingResponse.setEndTime(savedBooking.getEndTime());
        bookingResponse.setActualStartTime(savedBooking.getActualStartTime());
        bookingResponse.setActualEndTime(savedBooking.getActualEndTime());
        bookingResponse.setBookingStatus(savedBooking.getBookingStatus());

        return bookingResponse;
    }
}
