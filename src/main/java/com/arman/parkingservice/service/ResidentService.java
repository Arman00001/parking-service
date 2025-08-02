package com.arman.parkingservice.service;

import com.arman.parkingservice.dto.PageResponseDto;
import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.dto.resident.ResidentCreateDto;
import com.arman.parkingservice.dto.resident.ResidentResponse;
import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.entity.Resident;
import com.arman.parkingservice.persistence.repository.CommunityRepository;
import com.arman.parkingservice.persistence.repository.ResidentRepository;
import com.arman.parkingservice.service.exception.ResourceAlreadyUsedException;
import com.arman.parkingservice.service.exception.ResourceNotFoundException;
import com.arman.parkingservice.service.mapper.CommunityMapper;
import com.arman.parkingservice.service.mapper.ResidentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResidentService {
    private final CommunityRepository communityRepository;
    private final ResidentMapper residentMapper;
    private final ResidentRepository residentRepository;
    private final CommunityMapper communityMapper;

    /**
     * Creates and persists a new Resident in the specified Community.
     * <p>
     * First, the Community with the given ID is looked up; if not found,
     * a {@link ResourceNotFoundException} is thrown. Then, we check whether
     * a Resident with the same email already exists in that Community; if so,
     * a {@link ResourceAlreadyUsedException} is thrown.
     *
     * @param residentCreateDto the data transfer object containing the new resident’s
     *                          first name, last name, email, and the community ID
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
        CommunityResponse communityResponse = communityMapper.mapToResponse(community);

        return residentMapper.mapToResponse(savedResident, communityResponse);
    }


    /**
     * Retrieves a resident by its unique identifier.
     * <p>
     * Looks up the {@link Resident} entity by the given ID; if no resident
     * is found, throws a {@link ResourceNotFoundException}.  Maps the
     * found entity into a {@link ResidentResponse}, including its
     * {@link CommunityResponse}.
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
        CommunityResponse communityResponse = communityMapper.mapToResponse(resident.getCommunity());

        return residentMapper.mapToResponse(resident, communityResponse);
    }


    public PageResponseDto<ResidentResponse> getResidents(Long communityId) {
        Page<ResidentResponse> page = residentRepository.findAllByCommunity_Id(communityId);

        return PageResponseDto.from(page);
    }
}
