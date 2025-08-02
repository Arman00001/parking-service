package com.arman.parkingservice.service;

import com.arman.parkingservice.criteria.ParkingSpotSearchCriteria;
import com.arman.parkingservice.dto.PageResponseDto;
import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.dto.parkingspot.ParkingSpotCreateDto;
import com.arman.parkingservice.dto.parkingspot.ParkingSpotResponse;
import com.arman.parkingservice.exception.ResourceAlreadyUsedException;
import com.arman.parkingservice.exception.ResourceNotFoundException;
import com.arman.parkingservice.mapper.CommunityMapper;
import com.arman.parkingservice.mapper.ParkingSpotMapper;
import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.entity.ParkingSpot;
import com.arman.parkingservice.persistence.repository.CommunityRepository;
import com.arman.parkingservice.persistence.repository.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ParkingSpotService {
    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingSpotMapper parkingSpotMapper;
    private final CommunityMapper communityMapper;
    private final CommunityRepository communityRepository;

    /**
     * Creates and persists a new ParkingSpot in the specified Community.
     * <p>
     * Maps the incoming {@link ParkingSpotCreateDto} to a {@link ParkingSpot} entity, saves it,
     * and returns a {@link ParkingSpotResponse}.
     *
     * @param parkingSpotCreateDto the DTO containing the new spot’s code and
     *                             the ID of the community it belongs to
     * @return a {@link ParkingSpotResponse} representing the saved spot,
     *         including nested {@link CommunityResponse}
     * @throws ResourceNotFoundException    if no Community exists with the given ID
     * @throws ResourceAlreadyUsedException if a spot with the same code already
     *                                      exists in that Community
     */
    public ParkingSpotResponse addParkingSpot(ParkingSpotCreateDto parkingSpotCreateDto) {
        Community community = communityRepository.findById(parkingSpotCreateDto.getCommunityId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Community with the following id not found: " + parkingSpotCreateDto.getCommunityId()
                ));

        parkingSpotRepository.findByCommunityAndCode(community, parkingSpotCreateDto.getCode())
                .ifPresent(r -> {
                    throw new ResourceAlreadyUsedException(
                            "A resident with email " + parkingSpotCreateDto.getCode() +
                                    " already exists in community " + parkingSpotCreateDto.getCommunityId());
                });

        ParkingSpot parkingSpot = parkingSpotMapper.mapCreateToParkingSpot(parkingSpotCreateDto, community);
        ParkingSpot savedSpot = parkingSpotRepository.save(parkingSpot);
        CommunityResponse communityResponse = communityMapper.mapToResponse(savedSpot.getCommunity());

        return parkingSpotMapper.mapToResponse(savedSpot, communityResponse);
    }

    /**
     * Retrieves a ParkingSpot by its unique identifier.
     * <p>
     * Looks up the {@link ParkingSpot} entity by ID; if not found,
     * throws {@link ResourceNotFoundException}. Maps the found entity into
     * a {@link ParkingSpotResponse}, including its associated community.
     *
     * @param id the unique identifier of the parking spot to retrieve
     * @return a {@link ParkingSpotResponse} containing the spot’s code,
     *         status, and community information
     * @throws ResourceNotFoundException if no ParkingSpot exists with the given ID
     *
     */
    public ParkingSpotResponse getParkingSpotById(Long id) {
        ParkingSpot parkingSpot = parkingSpotRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Parking spot with the following id not found: " + id)
                );
        CommunityResponse communityResponse = communityMapper.mapToResponse(parkingSpot.getCommunity());

        return parkingSpotMapper.mapToResponse(parkingSpot, communityResponse);
    }

    public PageResponseDto<ParkingSpotResponse> getAllParkingSpotsByCommunity(Long communityId, ParkingSpotSearchCriteria criteria) {
        Page<ParkingSpotResponse> page = parkingSpotRepository
                .findAllByCommunityIdAndCriteria(communityId,criteria,criteria.buildPageRequest());

        return PageResponseDto.from(page);
    }

    public PageResponseDto<ParkingSpotResponse> getAvailableSpots(
            Long communityId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            ParkingSpotSearchCriteria criteria) {
        Page<ParkingSpotResponse> page = parkingSpotRepository
                .findAllAvailableByCommunityAndCriteria(communityId, startTime, endTime, criteria, criteria.buildPageRequest());

        return PageResponseDto.from(page);

    }

}