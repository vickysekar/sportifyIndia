package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.UtilityBookings;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UtilityBookings entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UtilityBookingsRepository extends JpaRepository<UtilityBookings, Long> {}
