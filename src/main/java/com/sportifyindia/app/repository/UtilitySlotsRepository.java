package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.UtilitySlots;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UtilitySlots entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UtilitySlotsRepository extends JpaRepository<UtilitySlots, Long> {}
