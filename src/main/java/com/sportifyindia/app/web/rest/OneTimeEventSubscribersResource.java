package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.domain.OneTimeEventSubscribers;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.domain.enumeration.SubscriptionStatusEnum;
import com.sportifyindia.app.repository.OneTimeEventSubscribersRepository;
import com.sportifyindia.app.service.OneTimeEventSubscribersService;
import com.sportifyindia.app.service.dto.EventSubscriberDTO;
import com.sportifyindia.app.service.dto.OneTimeEventSubscribersDTO;
import com.sportifyindia.app.web.rest.errors.BadRequestAlertException;
import com.sportifyindia.app.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing event registrations.
 */
@RestController
@RequestMapping("/api/event-registrations")
public class OneTimeEventSubscribersResource {

    private final Logger log = LoggerFactory.getLogger(OneTimeEventSubscribersResource.class);

    private static final String ENTITY_NAME = "eventRegistration";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OneTimeEventSubscribersService oneTimeEventSubscribersService;

    private final OneTimeEventSubscribersRepository oneTimeEventSubscribersRepository;

    public OneTimeEventSubscribersResource(
        OneTimeEventSubscribersService oneTimeEventSubscribersService,
        OneTimeEventSubscribersRepository oneTimeEventSubscribersRepository
    ) {
        this.oneTimeEventSubscribersService = oneTimeEventSubscribersService;
        this.oneTimeEventSubscribersRepository = oneTimeEventSubscribersRepository;
    }

    /**
     * Register a user for an event.
     *
     * @param eventId the ID of the event
     * @param userId the ID of the user
     * @param paidAmount the amount paid for registration
     * @return the created subscription
     */
    @PostMapping("/events/{eventId}/register/{userId}")
    public ResponseEntity<OneTimeEventSubscribersDTO> registerForEvent(
        @PathVariable Long eventId,
        @PathVariable Long userId,
        @RequestParam BigDecimal paidAmount
    ) {
        log.debug("REST request to register user {} for event {} with amount {}", userId, eventId, paidAmount);
        try {
            OneTimeEventSubscribersDTO result = oneTimeEventSubscribersService.registerForEvent(eventId, userId, paidAmount);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update the status of a subscription.
     *
     * @param subscriptionId the ID of the subscription
     * @param newStatus the new status
     * @return the updated subscription
     */
    @PutMapping("/{subscriptionId}/status")
    public ResponseEntity<OneTimeEventSubscribersDTO> updateSubscriptionStatus(
        @PathVariable Long subscriptionId,
        @RequestParam SubscriptionStatusEnum newStatus
    ) {
        log.debug("REST request to update subscription {} status to {}", subscriptionId, newStatus);
        try {
            OneTimeEventSubscribersDTO result = oneTimeEventSubscribersService.updateSubscriptionStatus(subscriptionId, newStatus);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * {@code GET  /users/:userId/subscriptions} : get all subscriptions for a user.
     *
     * @param userId the ID of the user
     * @param status the status to filter by (optional)
     * @param date the date to filter by (optional)
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subscriptions in body
     */
    @GetMapping("/users/{userId}/subscriptions")
    public ResponseEntity<List<OneTimeEventSubscribersDTO>> getUserSubscriptions(
        @PathVariable Long userId,
        @RequestParam(required = false) SubscriptionStatusEnum status,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date,
        Pageable pageable
    ) {
        log.debug("REST request to get subscriptions for user {} with status {} and date {}", userId, status, date);
        Page<OneTimeEventSubscribersDTO> page = oneTimeEventSubscribersService.getUserSubscriptions(userId, status, date, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * Get all users subscribed to a specific event.
     *
     * @param eventId the ID of the event
     * @param pageable the pagination information
     * @return the list of subscribers
     */
    @GetMapping("/events/{eventId}/subscribers")
    public ResponseEntity<Page<EventSubscriberDTO>> getEventSubscribers(@PathVariable Long eventId, Pageable pageable) {
        log.debug("REST request to get subscribers for event {}", eventId);
        Page<EventSubscriberDTO> page = oneTimeEventSubscribersService.getEventSubscribers(eventId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page);
    }
}
