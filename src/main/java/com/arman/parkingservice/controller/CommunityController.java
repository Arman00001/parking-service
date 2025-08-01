package com.arman.parkingservice.controller;

import com.arman.parkingservice.dto.PageResponseDto;
import com.arman.parkingservice.dto.resident.ResidentResponse;
import com.arman.parkingservice.service.CommunityService;
import com.arman.parkingservice.service.ResidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {
    private final ResidentService residentService;
    private CommunityService communityService;

    @GetMapping("/{communityId}/residents")
    public PageResponseDto<ResidentResponse> getResidents(
            @PathVariable("communityId") Long communityId) {
        return residentService.getResidents(communityId);
    }
}
