package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.SubscriptionAvailableDay;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SubscriptionAvailableDay entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubscriptionAvailableDayRepository extends JpaRepository<SubscriptionAvailableDay, Long> {}
