package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.SubscriptionAvailableDay;
import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.domain.enumeration.DaysOfWeekEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SubscriptionAvailableDay entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubscriptionAvailableDayRepository
    extends JpaRepository<SubscriptionAvailableDay, Long>, JpaSpecificationExecutor<SubscriptionAvailableDay> {
    Optional<SubscriptionAvailableDay> findByDaysOfWeekAndTimeSlots(DaysOfWeekEnum daysOfWeek, TimeSlots timeSlots);
}
