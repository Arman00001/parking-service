package com.arman.parkingservice.persistence.repository;

import com.arman.parkingservice.criteria.CommunitySearchCriteria;
import com.arman.parkingservice.dto.community.CommunityResponse;
import com.arman.parkingservice.persistence.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("""
        SELECT new com.arman.parkingservice.dto.community.CommunityResponse(
            c.id,
            c.name
        )
        FROM Community c
        WHERE :#{#criteria.name} IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%',:#{#criteria.name},'%'))
""")
    Page<CommunityResponse> findAllCriteria(CommunitySearchCriteria criteria, Pageable pageable);
}
