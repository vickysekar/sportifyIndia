package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.domain.UtilityAvailableDays;
import com.sportifyindia.app.domain.enumeration.DaysOfWeekEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UtilityAvailableDays entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UtilityAvailableDaysRepository extends JpaRepository<UtilityAvailableDays, Long> {
    Optional<UtilityAvailableDays> findByDaysOfWeekAndTimeSlots(DaysOfWeekEnum daysOfWeek, TimeSlots timeSlots);
    List<UtilityAvailableDays> findByUtilityId(Long utilityId);
}
