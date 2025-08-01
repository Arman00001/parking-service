package com.arman.parkingservice.dto.community;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityCreateDto {
    @NotBlank
    private String name;
}
