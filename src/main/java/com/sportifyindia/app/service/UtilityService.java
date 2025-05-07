package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.domain.Utility;
import com.sportifyindia.app.domain.UtilityAvailableDays;
import com.sportifyindia.app.domain.UtilityExceptionDays;
import com.sportifyindia.app.domain.enumeration.DaysOfWeekEnum;
import com.sportifyindia.app.repository.FacilityRepository;
import com.sportifyindia.app.repository.TimeSlotsRepository;
import com.sportifyindia.app.repository.UserRepository;
import com.sportifyindia.app.repository.UtilityAvailableDaysRepository;
import com.sportifyindia.app.repository.UtilityExceptionDaysRepository;
import com.sportifyindia.app.repository.UtilityRepository;
import com.sportifyindia.app.repository.search.UtilitySearchRepository;
import com.sportifyindia.app.security.SecurityUtils;
import com.sportifyindia.app.service.dto.TimeSlotDTO;
import com.sportifyindia.app.service.dto.UtilityAvailableDaysDTO;
import com.sportifyindia.app.service.dto.UtilityDTO;
import com.sportifyindia.app.service.mapper.UtilityMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.Utility}.
 */
@Service
@Transactional
public class UtilityService {

    private final Logger log = LoggerFactory.getLogger(UtilityService.class);

    private final UtilityRepository utilityRepository;
    private final UtilityMapper utilityMapper;
    private final UtilitySearchRepository utilitySearchRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final UtilityAvailableDaysRepository utilityAvailableDaysRepository;
    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;
    private final UtilityExceptionDaysRepository utilityExceptionDaysRepository;

    public UtilityService(
        UtilityRepository utilityRepository,
        UtilityMapper utilityMapper,
        UtilitySearchRepository utilitySearchRepository,
        TimeSlotsRepository timeSlotsRepository,
        UtilityAvailableDaysRepository utilityAvailableDaysRepository,
        FacilityRepository facilityRepository,
        UserRepository userRepository,
        UtilityExceptionDaysRepository utilityExceptionDaysRepository
    ) {
        this.utilityRepository = utilityRepository;
        this.utilityMapper = utilityMapper;
        this.utilitySearchRepository = utilitySearchRepository;
        this.timeSlotsRepository = timeSlotsRepository;
        this.utilityAvailableDaysRepository = utilityAvailableDaysRepository;
        this.facilityRepository = facilityRepository;
        this.userRepository = userRepository;
        this.utilityExceptionDaysRepository = utilityExceptionDaysRepository;
    }

    /**
     * Save a utility.
     *
     * @param utilityDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityDTO save(UtilityDTO utilityDTO) {
        log.debug("Request to save Utility : {}", utilityDTO);
        checkUserHasAccess(utilityDTO.getFacilityId());

        Facility facility = facilityRepository
            .findById(utilityDTO.getFacilityId())
            .orElseThrow(() -> new IllegalArgumentException("Facility not found with id: " + utilityDTO.getFacilityId()));

        Utility utility = utilityMapper.toEntity(utilityDTO);
        utility.setFacility(facility);

        if (utilityDTO.getUtilityAvailableDays() != null) {
            for (UtilityAvailableDaysDTO availableDayDTO : utilityDTO.getUtilityAvailableDays()) {
                for (TimeSlotDTO timeSlotDTO : availableDayDTO.getTimeSlots()) {
                    TimeSlots timeSlot = findOrCreateTimeSlot(timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime());
                    UtilityAvailableDays availableDay = findOrCreateAvailableDay(availableDayDTO.getDaysOfWeek(), timeSlot);
                    utility.addUtilityAvailableDays(availableDay);
                }
            }
        }

        utility = utilityRepository.save(utility);
        return utilityMapper.toDto(utility);
    }

    /**
     * Update a utility.
     *
     * @param utilityDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityDTO update(UtilityDTO utilityDTO) {
        log.debug("Request to update Utility : {}", utilityDTO);
        checkUserHasAccess(utilityDTO.getFacilityId());

        Facility facility = facilityRepository
            .findById(utilityDTO.getFacilityId())
            .orElseThrow(() -> new IllegalArgumentException("Facility not found with id: " + utilityDTO.getFacilityId()));

        Utility utility = utilityRepository
            .findById(utilityDTO.getId())
            .orElseThrow(() -> new IllegalArgumentException("Utility not found with id: " + utilityDTO.getId()));

        if (!utility.getFacility().getId().equals(utilityDTO.getFacilityId())) {
            throw new IllegalArgumentException("Utility does not belong to the specified facility");
        }

        utility = utilityMapper.toEntity(utilityDTO);
        utility.setFacility(facility);

        if (utilityDTO.getUtilityAvailableDays() != null) {
            utility.getUtilityAvailableDays().clear();

            for (UtilityAvailableDaysDTO availableDayDTO : utilityDTO.getUtilityAvailableDays()) {
                for (TimeSlotDTO timeSlotDTO : availableDayDTO.getTimeSlots()) {
                    TimeSlots timeSlot = findOrCreateTimeSlot(timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime());
                    UtilityAvailableDays availableDay = findOrCreateAvailableDay(availableDayDTO.getDaysOfWeek(), timeSlot);
                    utility.addUtilityAvailableDays(availableDay);
                }
            }
        }

        utility = utilityRepository.save(utility);
        return utilityMapper.toDto(utility);
    }

    /**
     * Get one utility by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UtilityDTO> findOne(Long id) {
        log.debug("Request to get Utility : {}", id);
        return utilityRepository.findOneWithEagerRelationships(id).map(utilityMapper::toDto);
    }

    /**
     * Delete the utility by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Utility : {}", id);
        Utility utility = utilityRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utility not found with id: " + id));
        checkUserHasAccess(utility.getFacility().getId());
        utilityRepository.deleteById(id);
    }

    /**
     * Search for the utility corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Utilities for query {}", query);
        return utilitySearchRepository.search(query, pageable).map(utilityMapper::toDto);
    }

    @Transactional(readOnly = true)
    public TimeSlots findOrCreateTimeSlot(Instant startTime, Instant endTime) {
        return timeSlotsRepository
            .findByStartTimeAndEndTime(startTime, endTime)
            .orElseGet(() -> {
                TimeSlots newTimeSlot = new TimeSlots();
                newTimeSlot.setStartTime(startTime);
                newTimeSlot.setEndTime(endTime);
                return timeSlotsRepository.save(newTimeSlot);
            });
    }

    @Transactional(readOnly = true)
    public UtilityAvailableDays findOrCreateAvailableDay(DaysOfWeekEnum daysOfWeek, TimeSlots timeSlots) {
        return utilityAvailableDaysRepository
            .findByDaysOfWeekAndTimeSlots(daysOfWeek, timeSlots)
            .orElseGet(() -> {
                UtilityAvailableDays newAvailableDay = new UtilityAvailableDays();
                newAvailableDay.setDaysOfWeek(daysOfWeek);
                newAvailableDay.setTimeSlots(timeSlots);
                return utilityAvailableDaysRepository.save(newAvailableDay);
            });
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

    @Transactional(readOnly = true)
    public Page<UtilityDTO> findByFacilityId(Long facilityId, Pageable pageable) {
        log.debug("Request to get Utilities by facility ID : {}", facilityId);
        return utilityRepository.findByFacilityId(facilityId, pageable).map(utilityMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<UtilityExceptionDays> findExceptionDaysByUtilityIdAndDateRange(
        Long utilityId,
        Instant fromDate,
        Instant toDate,
        Pageable pageable
    ) {
        log.debug("Request to get exception days for Utility : {} from {} to {}", utilityId, fromDate, toDate);

        if (fromDate == null) {
            fromDate = Instant.now();
        }

        return utilityExceptionDaysRepository.findByUtilityIdAndDateBetween(utilityId, fromDate, toDate, pageable);
    }

    public UtilityExceptionDays addExceptionDay(Long utilityId, UtilityExceptionDays exceptionDay) {
        log.debug("Request to add exception day for Utility : {}", utilityId);

        Utility utility = utilityRepository
            .findById(utilityId)
            .orElseThrow(() -> new IllegalArgumentException("Utility not found with id: " + utilityId));

        checkUserHasAccess(utility.getFacility().getId());

        exceptionDay.setUtility(utility);
        return utilityExceptionDaysRepository.save(exceptionDay);
    }

    @Transactional(readOnly = true)
    public List<UtilityAvailableDays> getAvailableDaysByUtilityId(Long utilityId) {
        log.debug("Request to get available days for Utility : {}", utilityId);

        Utility utility = utilityRepository
            .findById(utilityId)
            .orElseThrow(() -> new IllegalArgumentException("Utility not found with id: " + utilityId));

        return new ArrayList<>(utility.getUtilityAvailableDays());
    }

    public List<UtilityAvailableDays> updateAvailableDays(Long utilityId, List<UtilityAvailableDaysDTO> availableDaysDTOs) {
        log.debug("Request to update available days for Utility : {}", utilityId);

        Utility utility = utilityRepository
            .findById(utilityId)
            .orElseThrow(() -> new IllegalArgumentException("Utility not found with id: " + utilityId));

        checkUserHasAccess(utility.getFacility().getId());

        // Clear existing available days
        utility.getUtilityAvailableDays().clear();

        // Add new available days
        List<UtilityAvailableDays> updatedAvailableDays = new ArrayList<>();
        for (UtilityAvailableDaysDTO availableDayDTO : availableDaysDTOs) {
            for (TimeSlotDTO timeSlotDTO : availableDayDTO.getTimeSlots()) {
                TimeSlots timeSlot = findOrCreateTimeSlot(timeSlotDTO.getStartTime(), timeSlotDTO.getEndTime());

                UtilityAvailableDays availableDay = new UtilityAvailableDays();
                availableDay.setDaysOfWeek(availableDayDTO.getDaysOfWeek());
                availableDay.setTimeSlots(timeSlot);

                utility.addUtilityAvailableDays(availableDay);
                updatedAvailableDays.add(availableDay);
            }
        }

        utilityRepository.save(utility);
        return updatedAvailableDays;
    }
}
