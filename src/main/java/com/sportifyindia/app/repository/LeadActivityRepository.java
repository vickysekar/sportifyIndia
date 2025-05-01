package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.LeadActivity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LeadActivity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LeadActivityRepository extends JpaRepository<LeadActivity, Long> {}
