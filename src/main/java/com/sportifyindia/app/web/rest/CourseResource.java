package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.CourseRepository;
import com.sportifyindia.app.service.CourseService;
import com.sportifyindia.app.service.dto.CourseDTO;
import com.sportifyindia.app.web.rest.errors.BadRequestAlertException;
import com.sportifyindia.app.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.sportifyindia.app.domain.Course}.
 */
@RestController
@RequestMapping("/api")
public class CourseResource {

    private final Logger log = LoggerFactory.getLogger(CourseResource.class);

    private static final String ENTITY_NAME = "course";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CourseService courseService;

    private final CourseRepository courseRepository;

    public CourseResource(CourseService courseService, CourseRepository courseRepository) {
        this.courseService = courseService;
        this.courseRepository = courseRepository;
    }

    /**
     * {@code POST  /courses/facility/{facilityId}} : Create a new course for a facility.
     * Only ROLE_OWNER and ROLE_ADMIN can create courses.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param facilityId the ID of the facility
     * @param courseDTO the courseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new courseDTO,
     * or with status {@code 400 (Bad Request)} if the course has already an ID,
     * or with status {@code 403 (Forbidden)} if the user doesn't have permission.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/courses/facility/{facilityId}")
    public ResponseEntity<CourseDTO> createCourseForFacility(@PathVariable Long facilityId, @Valid @RequestBody CourseDTO courseDTO)
        throws URISyntaxException {
        log.debug("REST request to save Course for Facility {}: {}", facilityId, courseDTO);
        if (courseDTO.getId() != null) {
            throw new BadRequestAlertException("A new course cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CourseDTO result = courseService.createCourseForFacility(facilityId, courseDTO);
        return ResponseEntity
            .created(new URI("/api/courses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /courses/:id} : Updates an existing course.
     * Only ROLE_OWNER and ROLE_ADMIN can update courses.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param id the id of the courseDTO to save.
     * @param courseDTO the courseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseDTO,
     * or with status {@code 400 (Bad Request)} if the courseDTO is not valid,
     * or with status {@code 403 (Forbidden)} if the user doesn't have permission,
     * or with status {@code 500 (Internal Server Error)} if the courseDTO couldn't be updated.
     */
    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> updateCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CourseDTO courseDTO
    ) {
        log.debug("REST request to update Course : {}, {}", id, courseDTO);
        if (courseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        try {
            CourseDTO result = courseService.update(courseDTO);
            return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseDTO.getId().toString()))
                .body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * {@code PATCH  /courses/:id} : Partial updates given fields of an existing course, field will ignore if it is null
     * Only ROLE_OWNER and ROLE_ADMIN can update courses.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param id the id of the courseDTO to save.
     * @param courseDTO the courseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated courseDTO,
     * or with status {@code 400 (Bad Request)} if the courseDTO is not valid,
     * or with status {@code 403 (Forbidden)} if the user doesn't have permission,
     * or with status {@code 404 (Not Found)} if the courseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the courseDTO couldn't be updated.
     */
    @PatchMapping(value = "/courses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CourseDTO> partialUpdateCourse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CourseDTO courseDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Course partially : {}, {}", id, courseDTO);
        if (courseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, courseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!courseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        try {
            Optional<CourseDTO> result = courseService.partialUpdate(courseDTO);
            return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, courseDTO.getId().toString())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * {@code GET  /courses/:id} : get the "id" course.
     *
     * @param id the id of the courseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the courseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable Long id) {
        log.debug("REST request to get Course : {}", id);
        Optional<CourseDTO> courseDTO = courseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseDTO);
    }

    /**
     * {@code DELETE  /courses/:id} : delete the course by ID.
     * Only ROLE_OWNER and ROLE_ADMIN can delete courses.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param id the ID of the course to delete
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)} if successful,
     * or with status {@code 403 (Forbidden)} if the user doesn't have permission,
     * or with status {@code 404 (Not Found)} if the course doesn't exist
     */
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        log.debug("REST request to delete Course : {}", id);
        try {
            courseService.delete(id);
            return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * {@code SEARCH  /courses/_search?query=:query} : search for the course corresponding
     * to the query.
     *
     * @param query the query of the course search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<CourseDTO>> searchCourses(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Courses for query {}", query);
        try {
            Page<CourseDTO> page = courseService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }

    /**
     * {@code GET  /courses/facility/:facilityId} : get all courses by facility ID.
     *
     * @param facilityId the ID of the facility.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of courses in body.
     */
    @GetMapping("/courses/facility/{facilityId}")
    public ResponseEntity<List<CourseDTO>> getAllCoursesByFacilityId(
        @PathVariable Long facilityId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of Courses for facility : {}", facilityId);
        Page<CourseDTO> page = courseService.findAllByFacilityId(facilityId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
