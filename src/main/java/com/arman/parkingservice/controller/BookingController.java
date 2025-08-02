package com.arman.parkingservice.controller;

import com.arman.parkingservice.dto.booking.BookingRequestDto;
import com.arman.parkingservice.dto.booking.BookingResponse;
import com.arman.parkingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    /**
     * Create a new booking
     */
    @PostMapping
    public ResponseEntity<BookingResponse> addBooking(@RequestBody @Valid BookingRequestDto bookingRequestDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.addBooking(bookingRequestDto));
    }

    /**
     * Retrieve a single booking by its ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable("id") Long id){
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    /**
     * Activate a reservation (park the car) when the resident arrives
     */
    @PutMapping("/{id}/park")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<BookingResponse> park(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookingService.park(id));
    }


}
