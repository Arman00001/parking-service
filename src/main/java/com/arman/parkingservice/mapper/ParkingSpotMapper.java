package com.arman.parkingservice.mapper;

import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.dto.parkingspot.ParkingSpotCreateDto;
import com.arman.parkingservice.dto.parkingspot.ParkingSpotResponse;
import com.arman.parkingservice.enums.ParkingSpotStatus;
import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.entity.ParkingSpot;
import org.springframework.stereotype.Component;

@Component
public class ParkingSpotMapper {

    public ParkingSpot mapCreateToParkingSpot(ParkingSpotCreateDto parkingSpotCreateDto, Community community) {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setCode(parkingSpotCreateDto.getCode());
        parkingSpot.setCommunity(community);
        parkingSpot.setStatus(ParkingSpotStatus.AVAILABLE);

        return parkingSpot;
    }

    public ParkingSpotResponse mapToResponse(ParkingSpot parkingSpot, CommunityResponse communityResponse) {
        ParkingSpotResponse parkingSpotResponse = new ParkingSpotResponse();
        parkingSpotResponse.setId(parkingSpot.getId());
        parkingSpotResponse.setCode(parkingSpot.getCode());
        parkingSpotResponse.setCommunity(communityResponse);
        parkingSpotResponse.setStatus(parkingSpot.getStatus());

        return parkingSpotResponse;
    }
}
