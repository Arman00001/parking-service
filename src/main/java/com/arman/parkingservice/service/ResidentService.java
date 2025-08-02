package com.arman.parkingservice.service;

import com.arman.parkingservice.criteria.ResidentSearchCriteria;
import com.arman.parkingservice.dto.PageResponseDto;
import com.arman.parkingservice.dto.resident.ResidentCreateDto;
import com.arman.parkingservice.dto.resident.ResidentResponse;
import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.entity.Resident;
import com.arman.parkingservice.persistence.repository.CommunityRepository;
import com.arman.parkingservice.persistence.repository.ResidentRepository;
import com.arman.parkingservice.exception.ResourceAlreadyUsedException;
import com.arman.parkingservice.exception.ResourceNotFoundException;
import com.arman.parkingservice.mapper.ResidentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResidentService {
    private final CommunityRepository communityRepository;
    private final ResidentMapper residentMapper;
    private final ResidentRepository residentRepository;

    /**
     * Creates and persists a new Resident in the specified Community.
     * <p>
     * First, the Community with the given ID is looked up; if not found,
     * a {@link ResourceNotFoundException} is thrown. Then, we check whether
     * a Resident with the same email already exists in that Community; if so,
     * a {@link ResourceAlreadyUsedException} is thrown.
     *
     * @param residentCreateDto the data transfer object containing the new resident’s data
     * @return a {@link ResidentResponse} containing the saved resident’s details
     * @throws ResourceNotFoundException    if no Community exists with the given ID
     * @throws ResourceAlreadyUsedException if a Resident with the same email already
     *                                      exists in that Community
     */
    public ResidentResponse addResident(ResidentCreateDto residentCreateDto) {
        Community community = communityRepository.findById(residentCreateDto.getCommunityId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Community with the following id not found: " + residentCreateDto.getCommunityId()
                ));

        residentRepository
                .findByCommunityAndEmail(community, residentCreateDto.getEmail())
                .ifPresent(r -> {
                    throw new ResourceAlreadyUsedException(
                            "A resident with email " + residentCreateDto.getEmail() +
                                    " already exists in community " + residentCreateDto.getCommunityId());
                });

        Resident resident = residentMapper.mapCreateToResident(residentCreateDto, community);
        Resident savedResident = residentRepository.save(resident);

        return residentMapper.mapToResponse(savedResident);
    }


    /**
     * Retrieves a resident by its unique identifier.
     * <p>
     * Looks up the {@link Resident} entity by the given ID; if no resident
     * is found, throws a {@link ResourceNotFoundException}.  Maps the
     * found entity into a {@link ResidentResponse}
     *
     * @param id the unique identifier of the resident
     * @return a {@link ResidentResponse} containing the resident’s details
     * @throws ResourceNotFoundException if no resident exists with the given ID
     */
    public ResidentResponse getResidentById(Long id) {
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resident with the following id not found: " + id)
                );

        return residentMapper.mapToResponse(resident);
    }

    /**
     * Retrieves a paginated list of residents for a given community, applying
     * optional search and sorting criteria.
     * <p>
     * Delegates to the repository’s {@code findAllByCommunityIdAndCriteria} method,
     * passing along the community ID, and the {@link ResidentSearchCriteria}.
     * Wraps the resulting {@link Page} of {@link ResidentResponse} DTOs into a {@link PageResponseDto}.
     *
     * @param communityId the ID of the community whose residents should be fetched
     * @param criteria    the search criteria containing pagination (page/size),
     *                    sorting (field and direction), and optional filters
     * @return a {@link PageResponseDto} containing the requested page of
     * {@link ResidentResponse} objects and pagination metadata
     */
    public PageResponseDto<ResidentResponse> getResidentsByCommunity(Long communityId, ResidentSearchCriteria criteria) {
        Page<ResidentResponse> page = residentRepository
                .findAllByCommunityIdAndCriteria(communityId, criteria, criteria.buildPageRequest());

        return PageResponseDto.from(page);
    }
}
