package com.arman.parkingservice.controller;

import com.arman.parkingservice.dto.parkingspot.ParkingSpotCreateDto;
import com.arman.parkingservice.dto.parkingspot.ParkingSpotResponse;
import com.arman.parkingservice.service.ParkingSpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class ParkingSpotController {
    private final ParkingSpotService parkingSpotService;

    /**
     * Create a new parking spot.
     */
    @PostMapping
    public ResponseEntity<ParkingSpotResponse> addParkingSpot(
            @RequestBody @Valid ParkingSpotCreateDto parkingSpotCreateDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(parkingSpotService.addParkingSpot(parkingSpotCreateDto));
    }

    /**
     * Retrieve a single spot by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpotResponse> getParkingSpotById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(parkingSpotService.getParkingSpotById(id));
    }

}