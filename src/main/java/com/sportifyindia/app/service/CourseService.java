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
     * Update a course.
     *
     * @param courseDTO the entity to update.
     * @return the persisted entity.
     */
    public CourseDTO update(CourseDTO courseDTO) {
        log.debug("Request to update Course : {}", courseDTO);
        checkUserHasAccess(courseDTO.getFacilityId());
        Course course = courseMapper.toEntity(courseDTO);
        course = courseRepository.save(course);
        CourseDTO result = courseMapper.toDto(course);
        courseSearchRepository.index(course);
        return result;
    }

    /**
     * Partially update a course.
     *
     * @param courseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CourseDTO> partialUpdate(CourseDTO courseDTO) {
        log.debug("Request to partially update Course : {}", courseDTO);
        checkUserHasAccess(courseDTO.getFacilityId());
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
     * Get all the courses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CourseDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Courses");
        return courseRepository.findAll(pageable).map(courseMapper::toDto);
    }

    /**
     * Get all the courses with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CourseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return courseRepository.findAllWithEagerRelationships(pageable).map(courseMapper::toDto);
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
     * Delete the course by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Course : {}", id);
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            checkUserHasAccess(course.get().getFacility().getId());
            courseRepository.deleteById(id);
            courseSearchRepository.deleteFromIndexById(id);
        }
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

    private void checkUserHasAccess(Long facilityId) {
        Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElse(""));
        if (user.isPresent()) {
            Optional<Facility> facility = facilityRepository.findById(facilityId);
            if (facility.isPresent()) {
                if (
                    !user
                        .get()
                        .getAuthorities()
                        .stream()
                        .anyMatch(auth -> auth.getName().equals("ROLE_OWNER") || auth.getName().equals("ROLE_ADMIN"))
                ) {
                    throw new RuntimeException("User does not have permission to perform this action");
                }
            }
        }
    }
}
