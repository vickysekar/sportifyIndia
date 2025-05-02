package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.OneTimeEvent;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.domain.enumeration.EventStatusEnum;
import com.sportifyindia.app.repository.FacilityRepository;
import com.sportifyindia.app.repository.OneTimeEventRepository;
import com.sportifyindia.app.repository.UserRepository;
import com.sportifyindia.app.repository.search.OneTimeEventSearchRepository;
import com.sportifyindia.app.security.SecurityUtils;
import com.sportifyindia.app.service.dto.OneTimeEventDTO;
import com.sportifyindia.app.service.mapper.OneTimeEventMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.OneTimeEvent}.
 */
@Service
@Transactional
public class OneTimeEventService {

    private final Logger log = LoggerFactory.getLogger(OneTimeEventService.class);

    private final OneTimeEventRepository oneTimeEventRepository;
    private final OneTimeEventMapper oneTimeEventMapper;
    private final OneTimeEventSearchRepository oneTimeEventSearchRepository;
    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;

    public OneTimeEventService(
        OneTimeEventRepository oneTimeEventRepository,
        OneTimeEventMapper oneTimeEventMapper,
        OneTimeEventSearchRepository oneTimeEventSearchRepository,
        FacilityRepository facilityRepository,
        UserRepository userRepository
    ) {
        this.oneTimeEventRepository = oneTimeEventRepository;
        this.oneTimeEventMapper = oneTimeEventMapper;
        this.oneTimeEventSearchRepository = oneTimeEventSearchRepository;
        this.facilityRepository = facilityRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a oneTimeEvent.
     *
     * @param oneTimeEventDTO the entity to save.
     * @return the persisted entity.
     */
    public OneTimeEventDTO save(OneTimeEventDTO oneTimeEventDTO) {
        log.debug("Request to save OneTimeEvent : {}", oneTimeEventDTO);
        OneTimeEvent oneTimeEvent = oneTimeEventMapper.toEntity(oneTimeEventDTO);
        oneTimeEvent = oneTimeEventRepository.save(oneTimeEvent);
        OneTimeEventDTO result = oneTimeEventMapper.toDto(oneTimeEvent);
        oneTimeEventSearchRepository.index(oneTimeEvent);
        return result;
    }

    /**
     * Create a oneTimeEvent for a specific facility.
     * Only ROLE_OWNER and ROLE_ADMIN can create events.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param facilityId the ID of the facility
     * @param oneTimeEventDTO the entity to save.
     * @return the persisted entity.
     */
    public OneTimeEventDTO createOneTimeEventForFacility(Long facilityId, OneTimeEventDTO oneTimeEventDTO) {
        log.debug("Request to create OneTimeEvent for Facility {}: {}", facilityId, oneTimeEventDTO);

        // Check if user has access to the facility
        checkUserHasAccess(facilityId);

        // Find the facility
        Facility facility = facilityRepository
            .findById(facilityId)
            .orElseThrow(() -> new IllegalArgumentException("Facility not found with id: " + facilityId));

        OneTimeEvent oneTimeEvent = oneTimeEventMapper.toEntity(oneTimeEventDTO);
        oneTimeEvent.setFacility(facility);

        OneTimeEvent savedOneTimeEvent = oneTimeEventRepository.save(oneTimeEvent);
        OneTimeEventDTO result = oneTimeEventMapper.toDto(savedOneTimeEvent);
        oneTimeEventSearchRepository.index(savedOneTimeEvent);

        return result;
    }

    /**
     * Update a oneTimeEvent.
     * Only ROLE_OWNER and ROLE_ADMIN can update events.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param oneTimeEventDTO the entity to save.
     * @return the persisted entity.
     */
    public OneTimeEventDTO update(OneTimeEventDTO oneTimeEventDTO) {
        log.debug("Request to update OneTimeEvent : {}", oneTimeEventDTO);

        // Get the event to check facility ownership
        OneTimeEvent oneTimeEvent = oneTimeEventRepository
            .findById(oneTimeEventDTO.getId())
            .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + oneTimeEventDTO.getId()));

        // Check if user has access to the facility
        checkUserHasAccess(oneTimeEvent.getFacility().getId());

        OneTimeEvent updatedOneTimeEvent = oneTimeEventMapper.toEntity(oneTimeEventDTO);
        updatedOneTimeEvent = oneTimeEventRepository.save(updatedOneTimeEvent);
        OneTimeEventDTO result = oneTimeEventMapper.toDto(updatedOneTimeEvent);
        oneTimeEventSearchRepository.index(updatedOneTimeEvent);

        return result;
    }

    /**
     * Partially update a oneTimeEvent.
     *
     * @param oneTimeEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OneTimeEventDTO> partialUpdate(OneTimeEventDTO oneTimeEventDTO) {
        log.debug("Request to partially update OneTimeEvent : {}", oneTimeEventDTO);

        return oneTimeEventRepository
            .findById(oneTimeEventDTO.getId())
            .map(existingOneTimeEvent -> {
                oneTimeEventMapper.partialUpdate(existingOneTimeEvent, oneTimeEventDTO);

                return existingOneTimeEvent;
            })
            .map(oneTimeEventRepository::save)
            .map(savedOneTimeEvent -> {
                oneTimeEventSearchRepository.index(savedOneTimeEvent);
                return savedOneTimeEvent;
            })
            .map(oneTimeEventMapper::toDto);
    }

    /**
     * Get all the oneTimeEvents by facility ID.
     *
     * @param facilityId the ID of the facility.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventDTO> findAllByFacilityId(Long facilityId, Pageable pageable) {
        log.debug("Request to get all OneTimeEvents for facility : {}", facilityId);
        return oneTimeEventRepository.findByFacilityId(facilityId, pageable).map(oneTimeEventMapper::toDto);
    }

    /**
     * Get all the oneTimeEvents by facility ID and status.
     *
     * @param facilityId the ID of the facility.
     * @param status the status of the events.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventDTO> findAllByFacilityIdAndStatus(Long facilityId, EventStatusEnum status, Pageable pageable) {
        log.debug("Request to get all OneTimeEvents for facility {} and status {}", facilityId, status);
        return oneTimeEventRepository.findByFacilityIdAndStatus(facilityId, status, pageable).map(oneTimeEventMapper::toDto);
    }

    /**
     * Get all the oneTimeEvents by facility ID and date.
     * If no date is provided, uses current date.
     * Returns events with dates strictly greater than the given date.
     *
     * @param facilityId the ID of the facility.
     * @param date the date to filter events (optional).
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventDTO> findAllByFacilityIdAndDate(Long facilityId, LocalDate date, Pageable pageable) {
        log.debug("Request to get all OneTimeEvents for facility {} and date {}", facilityId, date);
        LocalDate filterDate = date != null ? date : LocalDate.now();
        return oneTimeEventRepository
            .findByFacilityIdAndEventDateGreaterThan(facilityId, filterDate, pageable)
            .map(oneTimeEventMapper::toDto);
    }

    /**
     * Get all the oneTimeEvents by facility ID, status and date.
     * If no date is provided, uses current date.
     * Returns events with dates strictly greater than the given date.
     *
     * @param facilityId the ID of the facility.
     * @param status the status of the events.
     * @param date the date to filter events (optional).
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventDTO> findAllByFacilityIdAndStatusAndDate(
        Long facilityId,
        EventStatusEnum status,
        LocalDate date,
        Pageable pageable
    ) {
        log.debug("Request to get all OneTimeEvents for facility {}, status {} and date {}", facilityId, status, date);
        LocalDate filterDate = date != null ? date : LocalDate.now();
        return oneTimeEventRepository
            .findByFacilityIdAndStatusAndEventDateGreaterThan(facilityId, status, filterDate, pageable)
            .map(oneTimeEventMapper::toDto);
    }

    /**
     * Get one oneTimeEvent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OneTimeEventDTO> findOne(Long id) {
        log.debug("Request to get OneTimeEvent : {}", id);
        return oneTimeEventRepository.findById(id).map(oneTimeEventMapper::toDto);
    }

    /**
     * Delete the oneTimeEvent by id.
     * Only ROLE_OWNER and ROLE_ADMIN can delete events.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OneTimeEvent : {}", id);

        // Get the event to check facility ownership
        OneTimeEvent oneTimeEvent = oneTimeEventRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));

        // Check if user has access to the facility
        checkUserHasAccess(oneTimeEvent.getFacility().getId());

        oneTimeEventRepository.deleteById(id);
        oneTimeEventSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the oneTimeEvent corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OneTimeEvents for query {}", query);
        return oneTimeEventSearchRepository.search(query, pageable).map(oneTimeEventMapper::toDto);
    }

    /**
     * Check if the current user has access to the facility.
     * Only ROLE_OWNER and ROLE_ADMIN can access the facility.
     *
     * @param facilityId the ID of the facility
     */
    private void checkUserHasAccess(Long facilityId) {
        String currentUserLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new IllegalArgumentException("Current user login not found"));

        User currentUser = userRepository
            .findOneByLogin(currentUserLogin)
            .orElseThrow(() -> new IllegalArgumentException("User not found with login: " + currentUserLogin));

        // Check if user is ROLE_ADMIN
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(authority -> authority.getName().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        // Check if user is ROLE_OWNER and owns the facility
        boolean isOwner = currentUser.getAuthorities().stream().anyMatch(authority -> authority.getName().equals("ROLE_OWNER"));

        if (isOwner) {
            Facility facility = facilityRepository
                .findById(facilityId)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found with id: " + facilityId));

            if (facility.getUser().getId().equals(currentUser.getId())) {
                return;
            }
        }

        throw new IllegalArgumentException("User does not have permission to perform this action");
    }

    /**
     * Update status of all events that have expired (event date is before today) to COMPLETED.
     * This method should be called by a scheduler at the start of each day.
     */
    @Transactional
    public void updateExpiredEventsStatus() {
        log.debug("Updating status of expired events to COMPLETED");
        LocalDate today = LocalDate.now();
        List<OneTimeEvent> expiredEvents = oneTimeEventRepository.findByEventDateBeforeAndStatus(today, EventStatusEnum.SCHEDULED);

        for (OneTimeEvent event : expiredEvents) {
            event.setStatus(EventStatusEnum.COMPLETED);
            oneTimeEventRepository.save(event);
            oneTimeEventSearchRepository.index(event);
            log.debug("Updated event {} status to COMPLETED", event.getId());
        }

        log.debug("Completed updating status of {} expired events", expiredEvents.size());
    }
}
