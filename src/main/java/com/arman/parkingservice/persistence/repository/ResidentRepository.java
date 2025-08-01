package com.arman.parkingservice.persistence.repository;

import com.arman.parkingservice.persistence.entity.Community;
import com.arman.parkingservice.persistence.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {

    Optional<Resident> findByCommunityAndEmail(Community community, String email);
}
