package com.arman.parkingservice.service;

import com.arman.parkingservice.criteria.CommunitySearchCriteria;
import com.arman.parkingservice.dto.PageResponseDto;
import com.arman.parkingservice.dto.community.CommunityCreateDto;
import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.repository.CommunityRepository;
import com.arman.parkingservice.exception.ResourceNotFoundException;
import com.arman.parkingservice.mapper.CommunityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityMapper communityMapper;

    /**
     * Creates and persists a new Community.
     * <p>
     * Creates a new {@link Community} entity by the given data and saves it,
     * and returns a {@link CommunityResponse} representing the newly created community.
     *
     * @param communityCreateDto the DTO containing data for the new community
     * @return a {@link CommunityResponse} containing the saved community’s details
     */
    public CommunityResponse addCommunity(CommunityCreateDto communityCreateDto) {
        Community community = communityMapper.mapCreateToCommunity(communityCreateDto);

        Community savedCommunity = communityRepository.save(community);

        return communityMapper.mapToResponse(savedCommunity);
    }

    /**
     * Retrieves a Community by its unique identifier.
     * <p>
     * Looks up the {@link Community} entity by the given ID; if no community
     * is found, throws a {@link ResourceNotFoundException}. Maps the found
     * entity into a {@link CommunityResponse}.
     *
     * @param id the unique identifier of the community to retrieve
     * @return a {@link CommunityResponse} containing the community’s information
     * @throws ResourceNotFoundException if no community exists with the given ID
     */
    public CommunityResponse getCommunityById(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Community with the following id not found: " + id)
                );

        return communityMapper.mapToResponse(community);
    }

    /**
     * Searches for communities matching the provided criteria, returning
     * a paginated list of results.
     * <p>
     * Uses {@link CommunitySearchCriteria} to filter and sort, then executes
     * a paged query via the repository. Wraps the resulting {@link Page}
     * of {@link CommunityResponse} DTOs into a {@link PageResponseDto}.
     *
     * @param criteria the search and pagination parameters
     * @return a {@link PageResponseDto} containing the page of matching
     *         {@link CommunityResponse} objects and pagination metadata
     */
    public PageResponseDto<CommunityResponse> getCommunities(CommunitySearchCriteria criteria) {
        Page<CommunityResponse> page = communityRepository.findAllCriteria(criteria, criteria.buildPageRequest());

        return PageResponseDto.from(page);
    }
}
