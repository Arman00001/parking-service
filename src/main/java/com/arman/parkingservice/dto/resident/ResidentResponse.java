package com.arman.parkingservice.dto.resident;

import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.persistence.entity.Community;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResidentResponse {
    private Long id;

    private String firstName;

    private String lastName;

    private CommunityResponse community;

    public ResidentResponse(Long id, String firstName, String lastName, Community community) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.community = new CommunityResponse(community.getId(), community.getName());
    }
}
