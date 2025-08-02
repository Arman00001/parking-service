package com.arman.parkingservice.controller;

import com.arman.parkingservice.criteria.SearchCriteria;
import com.arman.parkingservice.dto.PageResponseDto;
import com.arman.parkingservice.dto.booking.BookingResponse;
import com.arman.parkingservice.dto.resident.ResidentCreateDto;
import com.arman.parkingservice.dto.resident.ResidentResponse;
import com.arman.parkingservice.enums.BookingPeriod;
import com.arman.parkingservice.service.BookingService;
import com.arman.parkingservice.service.ResidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/residents")
@RequiredArgsConstructor
public class ResidentController {
    private final ResidentService residentService;
    private final BookingService bookingService;


    /**
     * Create a new resident.
     */
    @PostMapping
    public ResponseEntity<ResidentResponse> addResident(@RequestBody @Valid ResidentCreateDto residentCreateDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(residentService.addResident(residentCreateDto));
    }

    /**
     * Retrieve a resident by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResidentResponse> getResidentById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(residentService.getResidentById(id));
    }

    /**
     * Retrieve a list of all bookings made by the resident
     */
    @GetMapping("/{residentId}/bookings")
    public PageResponseDto<BookingResponse> getAllBookingsByResident(
            @PathVariable("residentId") Long residentId,
            @RequestParam(defaultValue = "CURRENT") BookingPeriod period,
            SearchCriteria criteria
    ) {
        return bookingService.getAllBookingsByResident(residentId, period, criteria);
    }
}
