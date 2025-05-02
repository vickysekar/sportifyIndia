package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Course;
import com.sportifyindia.app.domain.SubscriptionAvailableDay;
import com.sportifyindia.app.domain.SubscriptionPlan;
import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.domain.enumeration.DaysOfWeekEnum;
import com.sportifyindia.app.repository.CourseRepository;
import com.sportifyindia.app.repository.SubscriptionAvailableDayRepository;
import com.sportifyindia.app.repository.SubscriptionPlanRepository;
import com.sportifyindia.app.repository.TimeSlotsRepository;
import com.sportifyindia.app.repository.UserRepository;
import com.sportifyindia.app.repository.search.SubscriptionPlanSearchRepository;
import com.sportifyindia.app.security.SecurityUtils;
import com.sportifyindia.app.service.dto.SubscriptionAvailableDayDTO;
import com.sportifyindia.app.service.dto.SubscriptionPlanDTO;
import com.sportifyindia.app.service.mapper.SubscriptionPlanMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.SubscriptionPlan}.
 */
@Service
@Transactional
public class SubscriptionPlanService {

    private final Logger log = LoggerFactory.getLogger(SubscriptionPlanService.class);

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    private final SubscriptionPlanMapper subscriptionPlanMapper;

    private final SubscriptionPlanSearchRepository subscriptionPlanSearchRepository;

    private final TimeSlotsRepository timeSlotsRepository;

    private final SubscriptionAvailableDayRepository subscriptionAvailableDayRepository;

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public SubscriptionPlanService(
        SubscriptionPlanRepository subscriptionPlanRepository,
        SubscriptionPlanMapper subscriptionPlanMapper,
        SubscriptionPlanSearchRepository subscriptionPlanSearchRepository,
        TimeSlotsRepository timeSlotsRepository,
        SubscriptionAvailableDayRepository subscriptionAvailableDayRepository,
        CourseRepository courseRepository,
        UserRepository userRepository
    ) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.subscriptionPlanMapper = subscriptionPlanMapper;
        this.subscriptionPlanSearchRepository = subscriptionPlanSearchRepository;
        this.timeSlotsRepository = timeSlotsRepository;
        this.subscriptionAvailableDayRepository = subscriptionAvailableDayRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    /**
     * Update a subscriptionPlan.
     *
     * @param subscriptionPlanDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public SubscriptionPlanDTO update(SubscriptionPlanDTO subscriptionPlanDTO) {
        log.debug("Request to update SubscriptionPlan : {}", subscriptionPlanDTO);
        SubscriptionPlan subscriptionPlan = subscriptionPlanMapper.toEntity(subscriptionPlanDTO);
        subscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
        SubscriptionPlanDTO result = subscriptionPlanMapper.toDto(subscriptionPlan);
        subscriptionPlanSearchRepository.index(subscriptionPlan);
        return result;
    }

    /**
     * Partially update a subscriptionPlan.
     *
     * @param subscriptionPlanDTO the entity to update partially.
     * @return the persisted entity.
     */
    @Transactional
    public Optional<SubscriptionPlanDTO> partialUpdate(SubscriptionPlanDTO subscriptionPlanDTO) {
        log.debug("Request to partially update SubscriptionPlan : {}", subscriptionPlanDTO);

        return subscriptionPlanRepository
            .findById(subscriptionPlanDTO.getId())
            .map(existingSubscriptionPlan -> {
                subscriptionPlanMapper.partialUpdate(existingSubscriptionPlan, subscriptionPlanDTO);
                return existingSubscriptionPlan;
            })
            .map(subscriptionPlanRepository::save)
            .map(savedSubscriptionPlan -> {
                subscriptionPlanSearchRepository.index(savedSubscriptionPlan);
                return savedSubscriptionPlan;
            })
            .map(subscriptionPlanMapper::toDto);
    }

    /**
     * Get one subscription plan by ID.
     *
     * @param id the ID of the subscription plan
     * @return the subscription plan
     */
    @Transactional(readOnly = true)
    public Optional<SubscriptionPlanDTO> findOne(Long id) {
        log.debug("Request to get SubscriptionPlan : {}", id);
        return subscriptionPlanRepository.findById(id).map(subscriptionPlanMapper::toDto);
    }

    /**
     * Get one subscription plan by ID with eager loaded relationships.
     *
     * @param id the ID of the subscription plan
     * @return the subscription plan with eager loaded relationships
     */
    @Transactional(readOnly = true)
    public Optional<SubscriptionPlanDTO> findOneWithEagerRelationships(Long id) {
        log.debug("Request to get SubscriptionPlan with eager relationships : {}", id);
        return subscriptionPlanRepository.findOneWithEagerRelationships(id).map(subscriptionPlanMapper::toDto);
    }

    /**
     * Delete the subscription plan by ID.
     *
     * @param id the ID of the subscription plan
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete SubscriptionPlan : {}", id);

        // Get the subscription plan to check course ownership
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("SubscriptionPlan not found with id: " + id));

        // Check if user has access to delete
        checkUserHasAccess(subscriptionPlan.getCourse().getId());

        subscriptionPlanRepository.deleteById(id);
        subscriptionPlanSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the subscriptionPlan corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionPlanDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SubscriptionPlans for query {}", query);
        return subscriptionPlanSearchRepository.search(query, pageable).map(subscriptionPlanMapper::toDto);
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
    public SubscriptionAvailableDay findOrCreateAvailableDay(DaysOfWeekEnum daysOfWeek, TimeSlots timeSlots) {
        return subscriptionAvailableDayRepository
            .findByDaysOfWeekAndTimeSlots(daysOfWeek, timeSlots)
            .orElseGet(() -> {
                SubscriptionAvailableDay newAvailableDay = new SubscriptionAvailableDay();
                newAvailableDay.setDaysOfWeek(daysOfWeek);
                newAvailableDay.setTimeSlots(timeSlots);
                return subscriptionAvailableDayRepository.save(newAvailableDay);
            });
    }

    /**
     * Update a subscription plan for a course with role-based access control.
     * Only ROLE_OWNER and ROLE_ADMIN can update subscription plans.
     *
     * @param courseId the ID of the course
     * @param subscriptionPlanDTO the subscription plan to update
     * @return the updated subscription plan
     */
    @Transactional
    public SubscriptionPlanDTO updateSubscriptionPlanForCourse(Long courseId, SubscriptionPlanDTO subscriptionPlanDTO) {
        log.debug("Request to update SubscriptionPlan for Course {}: {}", courseId, subscriptionPlanDTO);

        // Check if user has access to the course
        checkUserHasAccess(courseId);

        // Find the course
        Course course = courseRepository
            .findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        // Find the subscription plan
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository
            .findById(subscriptionPlanDTO.getId())
            .orElseThrow(() -> new IllegalArgumentException("SubscriptionPlan not found with id: " + subscriptionPlanDTO.getId()));

        // Verify the subscription plan belongs to the course
        if (!subscriptionPlan.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("SubscriptionPlan does not belong to the specified course");
        }

        // Update the subscription plan with available days
        subscriptionPlan = subscriptionPlanMapper.toEntityWithAvailableDays(subscriptionPlanDTO);

        // Handle available days
        if (subscriptionPlanDTO.getSubscriptionAvailableDays() != null) {
            // Clear existing available days
            subscriptionPlan.getSubscriptionAvailableDays().clear();

            // Add new available days
            for (SubscriptionAvailableDayDTO availableDayDTO : subscriptionPlanDTO.getSubscriptionAvailableDays()) {
                // Find or create time slot
                TimeSlots timeSlot = findOrCreateTimeSlot(availableDayDTO.getStartTime(), availableDayDTO.getEndTime());

                // Find or create available day
                SubscriptionAvailableDay availableDay = findOrCreateAvailableDay(availableDayDTO.getDaysOfWeek(), timeSlot);

                // Add to subscription plan
                subscriptionPlan.addSubscriptionAvailableDay(availableDay);
            }
        }

        // Save and return the updated subscription plan
        SubscriptionPlan updatedSubscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
        SubscriptionPlanDTO result = subscriptionPlanMapper.toDtoWithAvailableDays(updatedSubscriptionPlan);
        subscriptionPlanSearchRepository.index(updatedSubscriptionPlan);

        return result;
    }

    /**
     * Check if the current user has access to the course.
     * Only ROLE_OWNER and ROLE_ADMIN can access the course.
     *
     * @param courseId the ID of the course
     */
    private void checkUserHasAccess(Long courseId) {
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

        // Check if user is ROLE_OWNER and owns the course
        boolean isOwner = currentUser.getAuthorities().stream().anyMatch(authority -> authority.getName().equals("ROLE_OWNER"));

        if (isOwner) {
            Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

            if (course.getFacility().getUser().getId().equals(currentUser.getId())) {
                return;
            }
        }

        throw new IllegalArgumentException("User does not have permission to perform this action");
    }

    @Transactional
    public SubscriptionPlanDTO createSubscriptionPlanForCourse(Long courseId, SubscriptionPlanDTO subscriptionPlanDTO) {
        log.debug("Request to create SubscriptionPlan for Course {}: {}", courseId, subscriptionPlanDTO);

        // Check if user has access to the course
        checkUserHasAccess(courseId);

        // Find the course
        Course course = courseRepository
            .findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        // Create the subscription plan with available days
        SubscriptionPlan subscriptionPlan = subscriptionPlanMapper.toEntityWithAvailableDays(subscriptionPlanDTO);
        subscriptionPlan.setCourse(course);

        // Handle available days
        if (subscriptionPlanDTO.getSubscriptionAvailableDays() != null) {
            for (SubscriptionAvailableDayDTO availableDayDTO : subscriptionPlanDTO.getSubscriptionAvailableDays()) {
                // Find or create time slot
                TimeSlots timeSlot = findOrCreateTimeSlot(availableDayDTO.getStartTime(), availableDayDTO.getEndTime());

                // Find or create available day
                SubscriptionAvailableDay availableDay = findOrCreateAvailableDay(availableDayDTO.getDaysOfWeek(), timeSlot);

                // Add to subscription plan
                subscriptionPlan.addSubscriptionAvailableDay(availableDay);
            }
        }

        // Save and return the new subscription plan
        SubscriptionPlan savedSubscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
        SubscriptionPlanDTO result = subscriptionPlanMapper.toDtoWithAvailableDays(savedSubscriptionPlan);
        subscriptionPlanSearchRepository.index(savedSubscriptionPlan);

        return result;
    }

    /**
     * Get all subscription plans for a specific course.
     *
     * @param courseId the ID of the course
     * @param pageable the pagination information
     * @return the list of subscription plans for the course
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionPlanDTO> findAllByCourseId(Long courseId, Pageable pageable) {
        log.debug("Request to get all SubscriptionPlans for Course {}", courseId);
        return subscriptionPlanRepository.findByCourseId(courseId, pageable).map(subscriptionPlanMapper::toDto);
    }

    /**
     * Get all subscription plans for a specific course with eager load of relationships.
     *
     * @param courseId the ID of the course
     * @param pageable the pagination information
     * @return the list of subscription plans for the course with eager loaded relationships
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionPlanDTO> findAllByCourseIdWithEagerRelationships(Long courseId, Pageable pageable) {
        log.debug("Request to get all SubscriptionPlans with eager relationships for Course {}", courseId);
        return subscriptionPlanRepository.findByCourseIdWithEagerRelationships(courseId, pageable).map(subscriptionPlanMapper::toDto);
    }
}
