package com.arman.parkingservice.mapper;

import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.dto.resident.ResidentCreateDto;
import com.arman.parkingservice.dto.resident.ResidentResponse;
import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.entity.Resident;
import org.springframework.stereotype.Component;

@Component
public class ResidentMapper {
    public Resident mapCreateToResident(ResidentCreateDto residentCreateDto, Community community) {
        Resident resident = new Resident();
        resident.setFirstName(residentCreateDto.getFirstName());
        resident.setLastName(residentCreateDto.getLastName());
        resident.setEmail(residentCreateDto.getEmail());
        resident.setCommunity(community);

        return resident;
    }

    public ResidentResponse mapToResponse(Resident resident, CommunityResponse communityResponse) {
        ResidentResponse residentResponse = new ResidentResponse();
        residentResponse.setId(resident.getId());
        residentResponse.setFirstName(resident.getFirstName());
        residentResponse.setLastName(residentResponse.getLastName());
        residentResponse.setCommunity(communityResponse);

        return residentResponse;
    }
}
