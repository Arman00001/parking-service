package com.arman.parkingservice.dto.resident;

import com.arman.parkingservice.dto.community.CommunityResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResidentResponse {
    private Long id;

    private String firstName;

    private String lastName;

    private CommunityResponse community;
}
