package com.arman.parkingservice.controller;

import com.arman.parkingservice.criteria.CommunitySearchCriteria;
import com.arman.parkingservice.criteria.ParkingSpotSearchCriteria;
import com.arman.parkingservice.criteria.ResidentSearchCriteria;
import com.arman.parkingservice.dto.PageResponseDto;
import com.arman.parkingservice.dto.community.CommunityCreateDto;
import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.dto.parkingspot.ParkingSpotResponse;
import com.arman.parkingservice.dto.resident.ResidentResponse;
import com.arman.parkingservice.service.CommunityService;
import com.arman.parkingservice.service.ResidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {
    private final ResidentService residentService;
    private final CommunityService communityService;
    private final ParkingSpotService parkingSpotService;


    @GetMapping
    public PageResponseDto<CommunityResponse> getCommunities(CommunitySearchCriteria criteria) {
        return communityService.getCommunities(criteria);
    }

    /**
     * Create a new community.
     */
    @PostMapping
    public ResponseEntity<CommunityResponse> addCommunity(
            @RequestBody @Valid CommunityCreateDto communityCreateDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(communityService.addCommunity(communityCreateDto));
    }

    @GetMapping("/{communityId}/residents")
    public PageResponseDto<ResidentResponse> getResidents(
            @PathVariable("communityId") Long communityId) {
        return residentService.getResidents(communityId);
    }
}
