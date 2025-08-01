package com.arman.parkingservice.controller;

import com.arman.parkingservice.dto.resident.ResidentCreateDto;
import com.arman.parkingservice.dto.resident.ResidentResponse;
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


    @PostMapping
    public ResponseEntity<ResidentResponse> addResident(@RequestBody @Valid ResidentCreateDto residentCreateDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(residentService.addResident(residentCreateDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResidentResponse> getResidentById(@PathVariable("id") Long id){
        return ResponseEntity.ok(residentService.getResidentById(id));
    }
}
