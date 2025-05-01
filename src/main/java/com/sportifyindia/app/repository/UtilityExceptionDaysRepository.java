package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.UtilityExceptionDays;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UtilityExceptionDays entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UtilityExceptionDaysRepository extends JpaRepository<UtilityExceptionDays, Long> {}
