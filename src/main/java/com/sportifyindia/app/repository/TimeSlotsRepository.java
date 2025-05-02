package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.TimeSlots;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TimeSlots entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TimeSlotsRepository extends JpaRepository<TimeSlots, Long>, JpaSpecificationExecutor<TimeSlots> {
    Optional<TimeSlots> findByStartTimeAndEndTime(Instant startTime, Instant endTime);
}
