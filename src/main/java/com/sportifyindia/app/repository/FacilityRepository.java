package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.Facility;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Facility entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    @Query("select facility from Facility facility where facility.user.login = ?#{principal.username}")
    Page<Facility> findByUserIsCurrentUser(Pageable pageable);
}
