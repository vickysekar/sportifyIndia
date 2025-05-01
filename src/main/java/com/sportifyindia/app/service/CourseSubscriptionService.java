package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.CourseSubscription;
import com.sportifyindia.app.domain.SubscriptionPlan;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.domain.enumeration.CourseSubscriptionStatusEnum;
import com.sportifyindia.app.repository.CourseSubscriptionRepository;
import com.sportifyindia.app.service.dto.CourseSubscriptionDTO;
import com.sportifyindia.app.service.mapper.CourseSubscriptionMapper;
import com.sportifyindia.app.service.mapper.SubscriptionPlanMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.CourseSubscription}.
 */
@Service
@Transactional
public class CourseSubscriptionService {

    private final Logger log = LoggerFactory.getLogger(CourseSubscriptionService.class);

    private final CourseSubscriptionRepository courseSubscriptionRepository;

    private final CourseSubscriptionMapper courseSubscriptionMapper;

    private final UserService userService;

    private final SubscriptionPlanService subscriptionPlanService;

    private final SubscriptionPlanMapper subscriptionPlanMapper;

    public CourseSubscriptionService(
        CourseSubscriptionRepository courseSubscriptionRepository,
        CourseSubscriptionMapper courseSubscriptionMapper,
        UserService userService,
        SubscriptionPlanService subscriptionPlanService,
        SubscriptionPlanMapper subscriptionPlanMapper
    ) {
        this.courseSubscriptionRepository = courseSubscriptionRepository;
        this.courseSubscriptionMapper = courseSubscriptionMapper;
        this.userService = userService;
        this.subscriptionPlanService = subscriptionPlanService;
        this.subscriptionPlanMapper = subscriptionPlanMapper;
    }

    /**
     * Calculate the end date based on the subscription plan's validity type and period.
     *
     * @param startDate the start date of the subscription
     * @param subscriptionPlan the subscription plan
     * @return the calculated end date
     */
    private Instant calculateEndDate(Instant startDate, SubscriptionPlan subscriptionPlan) {
        String validityType = subscriptionPlan.getValidityType();
        Integer validityPeriod = subscriptionPlan.getValidityPeriod();

        if (validityType == null || validityPeriod == null) {
            throw new IllegalArgumentException("Subscription plan must have both validity type and period");
        }

        return switch (validityType.toUpperCase()) {
            case "DAYS" -> startDate.plus(validityPeriod, ChronoUnit.DAYS);
            case "WEEKS" -> startDate.plus(validityPeriod * 7L, ChronoUnit.DAYS);
            case "MONTHS" -> startDate.plus(validityPeriod, ChronoUnit.MONTHS);
            case "YEARS" -> startDate.plus(validityPeriod, ChronoUnit.YEARS);
            default -> throw new IllegalArgumentException("Invalid validity type: " + validityType);
        };
    }

    /**
     * Save a courseSubscription.
     *
     * @param courseSubscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    public CourseSubscriptionDTO save(CourseSubscriptionDTO courseSubscriptionDTO) {
        log.debug("Request to save CourseSubscription : {}", courseSubscriptionDTO);

        // Get current user
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Validate subscription plan
        if (courseSubscriptionDTO.getSubscriptionPlan() == null || courseSubscriptionDTO.getSubscriptionPlan().getId() == null) {
            throw new IllegalArgumentException("Subscription plan ID is required");
        }

        // Fetch the subscription plan
        SubscriptionPlan subscriptionPlan = subscriptionPlanService
            .findOne(courseSubscriptionDTO.getSubscriptionPlan().getId())
            .map(subscriptionPlanMapper::toEntity)
            .orElseThrow(() -> new IllegalArgumentException("Subscription plan not found"));

        // Create new course subscription
        CourseSubscription courseSubscription = new CourseSubscription();

        // Set the user
        courseSubscription.setUser(currentUser.get());

        // Set the subscription plan
        courseSubscription.setSubscriptionPlan(subscriptionPlan);

        // Set the course from the subscription plan
        courseSubscription.setCourse(subscriptionPlan.getCourse());

        // Set start date (default to now if not provided)
        Instant startDate = courseSubscriptionDTO.getStartDate();
        if (startDate == null) {
            startDate = Instant.now();
        }
        courseSubscription.setStartDate(startDate);

        // Calculate and set the end date based on the subscription plan
        Instant endDate = calculateEndDate(startDate, subscriptionPlan);
        courseSubscription.setEndDate(endDate);

        // Set initial remaining sessions
        courseSubscription.setRemainingSessions(subscriptionPlan.getSessionLimit());

        // Set initial status
        courseSubscription.setStatus(CourseSubscriptionStatusEnum.ACTIVE);

        courseSubscription = courseSubscriptionRepository.save(courseSubscription);
        return courseSubscriptionMapper.toDto(courseSubscription);
    }

    /**
     * Update a courseSubscription.
     *
     * @param courseSubscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    public CourseSubscriptionDTO update(CourseSubscriptionDTO courseSubscriptionDTO) {
        log.debug("Request to update CourseSubscription : {}", courseSubscriptionDTO);
        CourseSubscription courseSubscription = courseSubscriptionMapper.toEntity(courseSubscriptionDTO);

        // Get current user
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Verify the subscription belongs to the current user
        Optional<CourseSubscription> existingSubscription = courseSubscriptionRepository.findById(courseSubscriptionDTO.getId());
        if (existingSubscription.isEmpty() || !existingSubscription.get().getUser().equals(currentUser.get())) {
            throw new RuntimeException("Subscription not found or does not belong to the current user");
        }

        courseSubscription.setUser(currentUser.get());
        courseSubscription = courseSubscriptionRepository.save(courseSubscription);
        return courseSubscriptionMapper.toDto(courseSubscription);
    }

    /**
     * Partially update a courseSubscription.
     *
     * @param courseSubscriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CourseSubscriptionDTO> partialUpdate(CourseSubscriptionDTO courseSubscriptionDTO) {
        log.debug("Request to partially update CourseSubscription : {}", courseSubscriptionDTO);

        return courseSubscriptionRepository
            .findById(courseSubscriptionDTO.getId())
            .map(existingCourseSubscription -> {
                // Get current user
                Optional<User> currentUser = userService.getUserWithAuthorities();
                if (currentUser.isEmpty()) {
                    throw new RuntimeException("User not found");
                }

                // Verify the subscription belongs to the current user
                if (!existingCourseSubscription.getUser().equals(currentUser.get())) {
                    throw new RuntimeException("Subscription does not belong to the current user");
                }

                courseSubscriptionMapper.partialUpdate(existingCourseSubscription, courseSubscriptionDTO);
                return existingCourseSubscription;
            })
            .map(courseSubscriptionRepository::save)
            .map(courseSubscriptionMapper::toDto);
    }

    /**
     * Get all the courseSubscriptions for the current user.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseSubscriptionDTO> findAllForCurrentUser(Pageable pageable) {
        log.debug("Request to get all CourseSubscriptions for current user");
        return courseSubscriptionRepository.findByUserIsCurrentUser(pageable).map(courseSubscriptionMapper::toDto);
    }

    /**
     * Get one courseSubscription by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CourseSubscriptionDTO> findOne(Long id) {
        log.debug("Request to get CourseSubscription : {}", id);
        return courseSubscriptionRepository.findById(id).map(courseSubscriptionMapper::toDto);
    }

    /**
     * Delete the courseSubscription by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete CourseSubscription : {}", id);

        // Get current user
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Verify the subscription belongs to the current user
        Optional<CourseSubscription> existingSubscription = courseSubscriptionRepository.findById(id);
        if (existingSubscription.isEmpty() || !existingSubscription.get().getUser().equals(currentUser.get())) {
            throw new RuntimeException("Subscription not found or does not belong to the current user");
        }

        courseSubscriptionRepository.deleteById(id);
    }

    /**
     * Get one courseSubscription by course id for the current user.
     *
     * @param courseId the id of the course.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CourseSubscriptionDTO> findOneByCourseAndUserIsCurrentUser(Long courseId) {
        log.debug("Request to get CourseSubscription for course : {}", courseId);
        return courseSubscriptionRepository.findOneByCourseAndUserIsCurrentUser(courseId).map(courseSubscriptionMapper::toDto);
    }
}
