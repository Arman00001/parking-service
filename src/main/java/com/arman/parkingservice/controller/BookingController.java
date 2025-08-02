package com.arman.parkingservice.controller;

import com.arman.parkingservice.dto.booking.BookingRequestDto;
import com.arman.parkingservice.dto.booking.BookingResponse;
import com.arman.parkingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> addBooking(@RequestBody @Valid BookingRequestDto bookingRequestDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.addBooking(bookingRequestDto));
    }
}
