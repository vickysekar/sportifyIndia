package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.OneTimeEvent;
import com.sportifyindia.app.domain.enumeration.EventStatusEnum;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OneTimeEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OneTimeEventRepository extends JpaRepository<OneTimeEvent, Long> {
    Page<OneTimeEvent> findByFacilityId(Long facilityId, Pageable pageable);

    Page<OneTimeEvent> findByFacilityIdAndStatus(Long facilityId, EventStatusEnum status, Pageable pageable);

    /**
     * Find all one-time events for a facility with event date strictly greater than the given date.
     *
     * @param facilityId the ID of the facility
     * @param date the date to filter events
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<OneTimeEvent> findByFacilityIdAndEventDateGreaterThan(Long facilityId, LocalDate date, Pageable pageable);

    /**
     * Find all one-time events for a facility with given status and event date strictly greater than the given date.
     *
     * @param facilityId the ID of the facility
     * @param status the status of the events
     * @param date the date to filter events
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<OneTimeEvent> findByFacilityIdAndStatusAndEventDateGreaterThan(
        Long facilityId,
        EventStatusEnum status,
        LocalDate date,
        Pageable pageable
    );

    /**
     * Find all one-time events that have a date before the given date and match the specified status.
     *
     * @param date the date to compare against
     * @param status the status of the events
     * @return the list of entities
     */
    List<OneTimeEvent> findByEventDateBeforeAndStatus(LocalDate date, EventStatusEnum status);
}
