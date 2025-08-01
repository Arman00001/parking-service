package com.arman.parkingservice.service.mapper;

import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.persistence.entity.Community;
import org.springframework.stereotype.Component;

@Component
public class CommunityMapper {
    public CommunityResponse mapToResponse(Community community) {
        CommunityResponse communityResponse = new CommunityResponse();
        communityResponse.setId(community.getId());
        communityResponse.setName(community.getName());

        return communityResponse;
    }
}
