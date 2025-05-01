package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.TimeSlots;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TimeSlots entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimeSlotsRepository extends JpaRepository<TimeSlots, Long> {}
