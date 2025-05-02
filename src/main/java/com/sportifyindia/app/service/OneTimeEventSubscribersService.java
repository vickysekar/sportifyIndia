package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.OneTimeEvent;
import com.sportifyindia.app.domain.OneTimeEventSubscribers;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.domain.enumeration.EventStatusEnum;
import com.sportifyindia.app.domain.enumeration.SubscriptionStatusEnum;
import com.sportifyindia.app.repository.OneTimeEventSubscribersRepository;
import com.sportifyindia.app.repository.UserRepository;
import com.sportifyindia.app.repository.search.OneTimeEventSubscribersSearchRepository;
import com.sportifyindia.app.service.dto.EventSubscriberDTO;
import com.sportifyindia.app.service.dto.OneTimeEventDTO;
import com.sportifyindia.app.service.dto.OneTimeEventSubscribersDTO;
import com.sportifyindia.app.service.mapper.OneTimeEventSubscribersMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.OneTimeEventSubscribers}.
 */
@Service
@Transactional
public class OneTimeEventSubscribersService {

    private final Logger log = LoggerFactory.getLogger(OneTimeEventSubscribersService.class);

    private final OneTimeEventSubscribersRepository oneTimeEventSubscribersRepository;
    private final OneTimeEventSubscribersMapper oneTimeEventSubscribersMapper;
    private final OneTimeEventSubscribersSearchRepository oneTimeEventSubscribersSearchRepository;
    private final OneTimeEventService oneTimeEventService;
    private final UserRepository userRepository;

    public OneTimeEventSubscribersService(
        OneTimeEventSubscribersRepository oneTimeEventSubscribersRepository,
        OneTimeEventSubscribersMapper oneTimeEventSubscribersMapper,
        OneTimeEventSubscribersSearchRepository oneTimeEventSubscribersSearchRepository,
        OneTimeEventService oneTimeEventService,
        UserRepository userRepository
    ) {
        this.oneTimeEventSubscribersRepository = oneTimeEventSubscribersRepository;
        this.oneTimeEventSubscribersMapper = oneTimeEventSubscribersMapper;
        this.oneTimeEventSubscribersSearchRepository = oneTimeEventSubscribersSearchRepository;
        this.oneTimeEventService = oneTimeEventService;
        this.userRepository = userRepository;
    }

    /**
     * Register a user for an event.
     * Validates event availability and user eligibility before registration.
     *
     * @param eventId the ID of the event
     * @param userId the ID of the user
     * @param paidAmount the amount paid for registration
     * @return the created subscription
     * @throws IllegalArgumentException if registration is not possible
     */
    @Transactional
    public OneTimeEventSubscribersDTO registerForEvent(Long eventId, Long userId, BigDecimal paidAmount) {
        log.debug("Request to register user {} for event {} with amount {}", userId, eventId, paidAmount);

        // Get event once
        OneTimeEventDTO eventDTO = oneTimeEventService.findOne(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));

        // Check event availability
        if (eventDTO.getStatus() != EventStatusEnum.SCHEDULED) {
            throw new IllegalArgumentException("Event is not in SCHEDULED status");
        }
        if (eventDTO.getRegistrationDeadline() != null && Instant.now().isAfter(eventDTO.getRegistrationDeadline())) {
            throw new IllegalArgumentException("Registration deadline has passed");
        }

        // Check if user is already registered
        if (isUserRegisteredForEvent(eventId, userId)) {
            throw new IllegalArgumentException("User is already registered for this event");
        }

        // Check if event has reached capacity
        int currentSubscribers = oneTimeEventSubscribersRepository.countByOneTimeEventId(eventId);
        if (currentSubscribers >= eventDTO.getMaxCapacity()) {
            throw new IllegalArgumentException("Event has reached maximum capacity");
        }

        OneTimeEvent event = new OneTimeEvent()
            .id(eventDTO.getId())
            .eventName(eventDTO.getEventName())
            .eventDesc(eventDTO.getEventDesc())
            .entryFee(eventDTO.getEntryFee())
            .maxCapacity(eventDTO.getMaxCapacity())
            .eventDate(eventDTO.getEventDate())
            .startTime(eventDTO.getStartTime())
            .endTime(eventDTO.getEndTime())
            .status(eventDTO.getStatus());

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create subscription
        OneTimeEventSubscribers subscription = new OneTimeEventSubscribers()
            .paidAmount(paidAmount)
            .status(SubscriptionStatusEnum.PENDING)
            .oneTimeEvent(event)
            .user(user)
            .eventDate(eventDTO.getEventDate().atZone(ZoneId.systemDefault()).toLocalDate());

        // Save subscription
        subscription = oneTimeEventSubscribersRepository.save(subscription);
        oneTimeEventSubscribersSearchRepository.index(subscription);

        log.debug("Successfully registered user {} for event {}", userId, eventId);
        return oneTimeEventSubscribersMapper.toDto(subscription);
    }

    /**
     * Check if a user is already registered for an event.
     *
     * @param eventId the ID of the event
     * @param userId the ID of the user
     * @return true if the user is registered for the event
     */
    @Transactional(readOnly = true)
    public boolean isUserRegisteredForEvent(Long eventId, Long userId) {
        log.debug("Checking if user {} is registered for event {}", userId, eventId);
        return oneTimeEventSubscribersRepository.existsByOneTimeEventIdAndUserId(eventId, userId);
    }

    /**
     * Update the status of a subscription.
     *
     * @param subscriptionId the ID of the subscription
     * @param newStatus the new status
     * @return the updated subscription
     * @throws IllegalArgumentException if the subscription is not found
     */
    @Transactional
    public OneTimeEventSubscribersDTO updateSubscriptionStatus(Long subscriptionId, SubscriptionStatusEnum newStatus) {
        log.debug("Request to update subscription {} status to {}", subscriptionId, newStatus);

        OneTimeEventSubscribers subscription = oneTimeEventSubscribersRepository
            .findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.setStatus(newStatus);
        subscription = oneTimeEventSubscribersRepository.save(subscription);
        oneTimeEventSubscribersSearchRepository.index(subscription);

        log.debug("Successfully updated subscription {} status to {}", subscriptionId, newStatus);
        return oneTimeEventSubscribersMapper.toDto(subscription);
    }

    /**
     * Get all subscriptions for a user with optional status and date filters.
     * If no date is provided, uses current date as default.
     *
     * @param userId the ID of the user
     * @param status the status to filter by (optional)
     * @param date the date to filter by (optional, defaults to current date)
     * @param pageable the pagination information
     * @return the list of subscriptions
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventSubscribersDTO> getUserSubscriptions(
        Long userId,
        SubscriptionStatusEnum status,
        LocalDate date,
        Pageable pageable
    ) {
        log.debug("Request to get subscriptions for user {} with status {} and date {}", userId, status, date);
        // If date is not provided, use current date
        LocalDate filterDate = date != null ? date : LocalDate.now();
        return oneTimeEventSubscribersRepository
            .findByUserIdAndStatusAndEventDateGreaterThanEqual(userId, status, filterDate, pageable)
            .map(oneTimeEventSubscribersMapper::toDto);
    }

    /**
     * Get all users subscribed to a specific event.
     *
     * @param eventId the ID of the event
     * @param pageable the pagination information
     * @return the page of subscribers
     */
    @Transactional(readOnly = true)
    public Page<EventSubscriberDTO> getEventSubscribers(Long eventId, Pageable pageable) {
        log.debug("Request to get subscribers for event {}", eventId);
        return oneTimeEventSubscribersRepository.findSubscribersByEventId(eventId, pageable);
    }
}
