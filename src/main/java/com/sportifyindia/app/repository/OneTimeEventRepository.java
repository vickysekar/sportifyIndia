package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.OneTimeEvent;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OneTimeEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OneTimeEventRepository extends JpaRepository<OneTimeEvent, Long> {}
