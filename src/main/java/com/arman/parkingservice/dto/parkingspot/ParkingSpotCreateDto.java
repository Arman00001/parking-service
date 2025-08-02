package com.arman.parkingservice.dto.parkingspot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSpotCreateDto {
    @NotBlank
    private String code;

    @NotNull
    private Long communityId;

}
