package com.arman.parkingservice.persistence.repository;

import com.arman.parkingservice.criteria.ResidentSearchCriteria;
import com.arman.parkingservice.dto.resident.ResidentResponse;
import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.entity.Resident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {

    Optional<Resident> findByCommunityAndEmail(Community community, String email);

    @Query("""
        SELECT new com.arman.parkingservice.dto.resident.ResidentResponse(
            r.id,
            r.firstName,
            r.lastName,
            r.community
        )
        FROM Resident r
        WHERE r.community.id = :communityId
        AND (:#{#criteria.firstName} IS NULL OR LOWER(r.firstName) LIKE LOWER(CONCAT('%',:#{#criteria.firstName},'%')))
        AND (:#{#criteria.lastName} IS NULL OR LOWER(r.lastName) LIKE LOWER(CONCAT('%',:#{#criteria.lastName},'%')))
""")
    Page<ResidentResponse> findAllByCommunityIdAndCriteria(Long communityId, ResidentSearchCriteria criteria,
                                                           Pageable pageable);
}
