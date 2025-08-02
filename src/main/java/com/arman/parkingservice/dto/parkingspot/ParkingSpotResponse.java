package com.arman.parkingservice.dto.parkingspot;

import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.persistence.entity.Community;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotResponse {
    private Long id;
    private String code;
    private CommunityResponse community;

    public ParkingSpotResponse(Long id, String code, Community community) {
        this.id = id;
        this.code = code;
        this.community = new CommunityResponse(community.getId(), community.getName());
    }
}
