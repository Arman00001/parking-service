package com.arman.parkingservice.persistence.repository;

import com.arman.parkingservice.criteria.ParkingSpotSearchCriteria;
import com.arman.parkingservice.dto.parkingspot.ParkingSpotResponse;
import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.entity.ParkingSpot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    Optional<ParkingSpot> findByCommunityAndCode(Community community, String code);

    @Query("""
        SELECT new com.arman.parkingservice.dto.parkingspot.ParkingSpotResponse(
            p.id,
            p.code,
            p.community
        )
        FROM ParkingSpot p
        WHERE p.community.id = :communityId
            AND (:#{#criteria.code} IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%',:#{#criteria.code},'%')))
            AND p.id NOT IN (
                SELECT b.parkingSpot.id
                FROM Booking b
                WHERE b.bookingStatus IN ('RESERVED','ACTIVE')
                    AND b.startTime < :endTime
                    AND b.endTime > :startTime
        )
""")
    Page<ParkingSpotResponse> findAllAvailableByCommunityAndCriteria(
            Long communityId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            ParkingSpotSearchCriteria criteria,
            Pageable pageable);

    @Query("""
        SELECT new com.arman.parkingservice.dto.parkingspot.ParkingSpotResponse(
            p.id,
            p.code,
            p.community
        )
        FROM ParkingSpot p
        WHERE p.community.id = :communityId
            AND (:#{#criteria.code} IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%',:#{#criteria.code},'%')))
""")
    Page<ParkingSpotResponse> findAllByCommunityIdAndCriteria(Long communityId, ParkingSpotSearchCriteria criteria, Pageable pageable);
}
