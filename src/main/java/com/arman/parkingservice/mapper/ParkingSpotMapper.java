package com.arman.parkingservice.mapper;

import com.arman.parkingservice.dto.parkingspot.ParkingSpotCreateDto;
import com.arman.parkingservice.dto.parkingspot.ParkingSpotResponse;
import com.arman.parkingservice.enums.ParkingSpotStatus;
import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.entity.ParkingSpot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParkingSpotMapper {

    private final CommunityMapper communityMapper;


    public ParkingSpot mapCreateToParkingSpot(ParkingSpotCreateDto parkingSpotCreateDto, Community community) {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setCode(parkingSpotCreateDto.getCode());
        parkingSpot.setCommunity(community);

        return parkingSpot;
    }

    public ParkingSpotResponse mapToResponse(ParkingSpot parkingSpot) {
        ParkingSpotResponse parkingSpotResponse = new ParkingSpotResponse();
        parkingSpotResponse.setId(parkingSpot.getId());
        parkingSpotResponse.setCode(parkingSpot.getCode());
        parkingSpotResponse.setCommunity(communityMapper.mapToResponse(parkingSpot.getCommunity()));

        return parkingSpotResponse;
    }
}
