package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Course;
import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.repository.CourseRepository;
import com.sportifyindia.app.repository.FacilityRepository;
import com.sportifyindia.app.repository.UserRepository;
import com.sportifyindia.app.repository.search.CourseSearchRepository;
import com.sportifyindia.app.security.SecurityUtils;
import com.sportifyindia.app.service.dto.CourseDTO;
import com.sportifyindia.app.service.mapper.CourseMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Course}.
 */
@Service
@Transactional
public class CourseService {

    private final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;
    private final CourseSearchRepository courseSearchRepository;

    public CourseService(
        CourseRepository courseRepository,
        CourseMapper courseMapper,
        FacilityRepository facilityRepository,
        UserRepository userRepository,
        CourseSearchRepository courseSearchRepository
    ) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.facilityRepository = facilityRepository;
        this.userRepository = userRepository;
        this.courseSearchRepository = courseSearchRepository;
    }

    /**
     * Save a course.
     *
     * @param courseDTO the entity to save.
     * @return the persisted entity.
     */
    public CourseDTO save(CourseDTO courseDTO) {
        log.debug("Request to save Course : {}", courseDTO);
        checkUserHasAccess(courseDTO.getFacilityId());
        Course course = courseMapper.toEntity(courseDTO);
        course = courseRepository.save(course);
        CourseDTO result = courseMapper.toDto(course);
        courseSearchRepository.index(course);
        return result;
    }

    /**
     * Create a course for a specific facility.
     * Only ROLE_OWNER and ROLE_ADMIN can create courses.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param facilityId the ID of the facility
     * @param courseDTO the course to create
     * @return the persisted course
     */
    @Transactional
    public CourseDTO createCourseForFacility(Long facilityId, CourseDTO courseDTO) {
        log.debug("Request to create Course for Facility {}: {}", facilityId, courseDTO);

        // Check if user has access to the facility
        checkUserHasAccess(facilityId);

        // Find the facility
        Facility facility = facilityRepository
            .findById(facilityId)
            .orElseThrow(() -> new IllegalArgumentException("Facility not found with id: " + facilityId));

        Course course = courseMapper.toEntity(courseDTO);
        course.setFacility(facility);

        Course savedCourse = courseRepository.save(course);
        CourseDTO result = courseMapper.toDto(savedCourse);
        courseSearchRepository.index(savedCourse);

        return result;
    }

    /**
     * Update a course.
     * Only ROLE_OWNER and ROLE_ADMIN can update courses.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param courseDTO the entity to update
     * @return the persisted entity
     */
    @Transactional
    public CourseDTO update(CourseDTO courseDTO) {
        log.debug("Request to update Course : {}", courseDTO);

        // Get the course to check facility ownership
        Course course = courseRepository
            .findById(courseDTO.getId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseDTO.getId()));

        // Check if user has access to the facility
        checkUserHasAccess(course.getFacility().getId());

        Course updatedCourse = courseMapper.toEntity(courseDTO);
        updatedCourse = courseRepository.save(updatedCourse);
        CourseDTO result = courseMapper.toDto(updatedCourse);
        courseSearchRepository.index(updatedCourse);

        return result;
    }

    /**
     * Partially update a course.
     * Only ROLE_OWNER and ROLE_ADMIN can update courses.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param courseDTO the entity to update partially
     * @return the persisted entity
     */
    @Transactional
    public Optional<CourseDTO> partialUpdate(CourseDTO courseDTO) {
        log.debug("Request to partially update Course : {}", courseDTO);

        // Get the course to check facility ownership
        Course course = courseRepository
            .findById(courseDTO.getId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseDTO.getId()));

        // Check if user has access to the facility
        checkUserHasAccess(course.getFacility().getId());

        return courseRepository
            .findById(courseDTO.getId())
            .map(existingCourse -> {
                courseMapper.partialUpdate(existingCourse, courseDTO);
                return existingCourse;
            })
            .map(courseRepository::save)
            .map(savedCourse -> {
                courseSearchRepository.index(savedCourse);
                return savedCourse;
            })
            .map(courseMapper::toDto);
    }

    /**
     * Get one course by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CourseDTO> findOne(Long id) {
        log.debug("Request to get Course : {}", id);
        return courseRepository.findOneWithEagerRelationships(id).map(courseMapper::toDto);
    }

    /**
     * Delete the course by ID.
     * Only ROLE_OWNER and ROLE_ADMIN can delete courses.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param id the ID of the course
     */
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Course : {}", id);

        // Get the course to check facility ownership
        Course course = courseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));

        // Check if user has access to the facility
        checkUserHasAccess(course.getFacility().getId());

        courseRepository.deleteById(id);
        courseSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the course corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Courses for query {}", query);
        return courseSearchRepository.search(query, pageable).map(courseMapper::toDto);
    }

    /**
     * Get all the courses by facility ID.
     *
     * @param facilityId the ID of the facility.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseDTO> findAllByFacilityId(Long facilityId, Pageable pageable) {
        log.debug("Request to get all Courses for facility : {}", facilityId);
        return courseRepository.findByFacilityId(facilityId, pageable).map(courseMapper::toDto);
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
}
