package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.OneTimeEventSubscribers;
import com.sportifyindia.app.domain.enumeration.SubscriptionStatusEnum;
import com.sportifyindia.app.service.dto.EventSubscriberDTO;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OneTimeEventSubscribers entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OneTimeEventSubscribersRepository extends JpaRepository<OneTimeEventSubscribers, Long> {
    @Query(
        "select oneTimeEventSubscribers from OneTimeEventSubscribers oneTimeEventSubscribers where oneTimeEventSubscribers.user.login = ?#{authentication.name}"
    )
    List<OneTimeEventSubscribers> findByUserIsCurrentUser();

    /**
     * Check if a user is registered for an event.
     *
     * @param eventId the ID of the event
     * @param userId the ID of the user
     * @return true if the user is registered for the event
     */
    boolean existsByOneTimeEventIdAndUserId(Long eventId, Long userId);

    /**
     * Find all subscriptions for a user.
     *
     * @param userId the ID of the user
     * @param pageable the pagination information
     * @return the page of subscriptions
     */
    Page<OneTimeEventSubscribers> findByUserId(Long userId, Pageable pageable);

    /**
     * Count the number of subscribers for an event.
     *
     * @param eventId the ID of the event
     * @return the number of subscribers
     */
    int countByOneTimeEventId(Long eventId);

    /**
     * Find all subscribers for an event.
     *
     * @param eventId the ID of the event
     * @param pageable the pagination information
     * @return the page of subscribers
     */
    Page<OneTimeEventSubscribers> findByOneTimeEventId(Long eventId, Pageable pageable);

    /**
     * Find all subscribers for an event with only required user fields.
     *
     * @param eventId the ID of the event
     * @param pageable the pagination information
     * @return the page of subscribers with minimal user data
     */
    @Query(
        "SELECT new com.sportifyindia.app.service.dto.EventSubscriberDTO(" +
        "u.id, u.firstName, u.lastName, u.email, u.imageUrl) " +
        "FROM OneTimeEventSubscribers s " +
        "JOIN s.user u " +
        "WHERE s.oneTimeEvent.id = :eventId"
    )
    Page<EventSubscriberDTO> findSubscribersByEventId(@Param("eventId") Long eventId, Pageable pageable);

    /**
     * Find all subscriptions for a user with optional status and date filters.
     *
     * @param userId the ID of the user
     * @param status the status to filter by (optional)
     * @param date the date to filter by (optional)
     * @param pageable the pagination information
     * @return the page of subscriptions
     */
    @Query(
        "SELECT s FROM OneTimeEventSubscribers s " +
        "WHERE s.user.id = :userId " +
        "AND (:status IS NULL OR s.status = :status) " +
        "AND (:date IS NULL OR s.eventDate >= :date) " +
        "ORDER BY s.eventDate DESC"
    )
    Page<OneTimeEventSubscribers> findByUserIdAndStatusAndEventDateGreaterThanEqual(
        @Param("userId") Long userId,
        @Param("status") SubscriptionStatusEnum status,
        @Param("date") LocalDate date,
        Pageable pageable
    );
}
