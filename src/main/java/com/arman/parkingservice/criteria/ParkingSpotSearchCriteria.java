package com.arman.parkingservice.criteria;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class ParkingSpotSearchCriteria extends SearchCriteria{
    private String code;

    private String sort;
    private String sortAscDesc;


    @Override
    public PageRequest buildPageRequest() {
        PageRequest pageRequest = super.buildPageRequest();
        String sortingParam = sort == null || sort.isBlank() ? "code" : sort;
        Sort.Direction direction = sortAscDesc==null || sortAscDesc.isBlank() ? Sort.Direction.ASC : Sort.Direction.fromString(sortAscDesc);

        return pageRequest.withSort(
                Sort.by(direction, sortingParam)
        );
    }

}
