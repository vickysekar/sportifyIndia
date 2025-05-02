package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.SubscriptionPlanRepository;
import com.sportifyindia.app.service.SubscriptionPlanService;
import com.sportifyindia.app.service.dto.SubscriptionPlanDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.SubscriptionPlan}.
 */
@RestController
@RequestMapping("/api")
public class SubscriptionPlanResource {

    private final Logger log = LoggerFactory.getLogger(SubscriptionPlanResource.class);

    private static final String ENTITY_NAME = "subscriptionPlan";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubscriptionPlanService subscriptionPlanService;

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanResource(
        SubscriptionPlanService subscriptionPlanService,
        SubscriptionPlanRepository subscriptionPlanRepository
    ) {
        this.subscriptionPlanService = subscriptionPlanService;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    /**
     * {@code POST  /subscription-plans/course/{courseId}} : Create a new subscription plan for a course.
     *
     * @param courseId the ID of the course
     * @param subscriptionPlanDTO the subscriptionPlanDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subscriptionPlanDTO,
     * or with status {@code 400 (Bad Request)} if the subscriptionPlan has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/subscription-plans/course/{courseId}")
    public ResponseEntity<SubscriptionPlanDTO> createSubscriptionPlanForCourse(
        @PathVariable Long courseId,
        @Valid @RequestBody SubscriptionPlanDTO subscriptionPlanDTO
    ) throws URISyntaxException {
        log.debug("REST request to save SubscriptionPlan for Course {}: {}", courseId, subscriptionPlanDTO);
        if (subscriptionPlanDTO.getId() != null) {
            throw new BadRequestAlertException("A new subscriptionPlan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SubscriptionPlanDTO result = subscriptionPlanService.createSubscriptionPlanForCourse(courseId, subscriptionPlanDTO);
        return ResponseEntity
            .created(new URI("/api/subscription-plans/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /subscription-plans/:id} : Updates an existing subscriptionPlan.
     *
     * @param id the id of the subscriptionPlanDTO to save.
     * @param subscriptionPlanDTO the subscriptionPlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subscriptionPlanDTO,
     * or with status {@code 400 (Bad Request)} if the subscriptionPlanDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subscriptionPlanDTO couldn't be updated.
     */
    @PutMapping("/subscription-plans/{id}")
    public ResponseEntity<SubscriptionPlanDTO> updateSubscriptionPlan(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubscriptionPlanDTO subscriptionPlanDTO
    ) {
        log.debug("REST request to update SubscriptionPlan : {}, {}", id, subscriptionPlanDTO);
        if (subscriptionPlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subscriptionPlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subscriptionPlanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SubscriptionPlanDTO result = subscriptionPlanService.update(subscriptionPlanDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscriptionPlanDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /subscription-plans/:id} : Partial updates given fields of an existing subscriptionPlan, field will ignore if it is null
     *
     * @param id the id of the subscriptionPlanDTO to save.
     * @param subscriptionPlanDTO the subscriptionPlanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subscriptionPlanDTO,
     * or with status {@code 400 (Bad Request)} if the subscriptionPlanDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subscriptionPlanDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subscriptionPlanDTO couldn't be updated.
     */
    @PatchMapping(value = "/subscription-plans/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SubscriptionPlanDTO> partialUpdateSubscriptionPlan(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubscriptionPlanDTO subscriptionPlanDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SubscriptionPlan partially : {}, {}", id, subscriptionPlanDTO);
        if (subscriptionPlanDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subscriptionPlanDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subscriptionPlanRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubscriptionPlanDTO> result = subscriptionPlanService.partialUpdate(subscriptionPlanDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscriptionPlanDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /subscription-plans/:id} : get the subscription plan by ID.
     *
     * @param id the ID of the subscription plan to retrieve
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subscription plan,
     * or with status {@code 404 (Not Found)} if the subscription plan does not exist
     */
    @GetMapping("/subscription-plans/{id}")
    public ResponseEntity<SubscriptionPlanDTO> getSubscriptionPlan(
        @PathVariable Long id,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get SubscriptionPlan : {}", id);
        Optional<SubscriptionPlanDTO> subscriptionPlanDTO;
        if (eagerload) {
            subscriptionPlanDTO = subscriptionPlanService.findOneWithEagerRelationships(id);
        } else {
            subscriptionPlanDTO = subscriptionPlanService.findOne(id);
        }
        return ResponseUtil.wrapOrNotFound(subscriptionPlanDTO);
    }

    /**
     * {@code DELETE  /subscription-plans/:id} : delete the subscription plan by ID.
     * Only ROLE_OWNER and ROLE_ADMIN can delete subscription plans.
     * For ROLE_OWNER, they must own the facility that the course belongs to.
     *
     * @param id the ID of the subscription plan to delete
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)} if successful,
     * or with status {@code 403 (Forbidden)} if the user doesn't have permission,
     * or with status {@code 404 (Not Found)} if the subscription plan doesn't exist
     */
    @DeleteMapping("/subscription-plans/{id}")
    public ResponseEntity<Void> deleteSubscriptionPlan(@PathVariable Long id) {
        log.debug("REST request to delete SubscriptionPlan : {}", id);
        try {
            subscriptionPlanService.delete(id);
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
     * {@code SEARCH  /subscription-plans/_search?query=:query} : search for the subscriptionPlan corresponding
     * to the query.
     *
     * @param query the query of the subscriptionPlan search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/subscription-plans/_search")
    public ResponseEntity<List<SubscriptionPlanDTO>> searchSubscriptionPlans(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of SubscriptionPlans for query {}", query);
        try {
            Page<SubscriptionPlanDTO> page = subscriptionPlanService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }

    /**
     * {@code PUT  /subscription-plans/course/{courseId}} : Updates an existing subscription plan for a course.
     *
     * @param courseId the ID of the course
     * @param subscriptionPlanDTO the subscription plan to update
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subscription plan,
     * or with status {@code 400 (Bad Request)} if the subscription plan is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subscription plan couldn't be updated.
     */
    @PutMapping("/subscription-plans/course/{courseId}")
    public ResponseEntity<SubscriptionPlanDTO> updateSubscriptionPlanForCourse(
        @PathVariable Long courseId,
        @RequestBody SubscriptionPlanDTO subscriptionPlanDTO
    ) {
        log.debug("REST request to update SubscriptionPlan for Course {}: {}", courseId, subscriptionPlanDTO);

        if (subscriptionPlanDTO.getId() == null) {
            throw new IllegalArgumentException("Invalid id");
        }

        SubscriptionPlanDTO result = subscriptionPlanService.updateSubscriptionPlanForCourse(courseId, subscriptionPlanDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscriptionPlanDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /subscription-plans/course/{courseId}} : get all subscription plans for a course.
     *
     * @param courseId the ID of the course
     * @param pageable the pagination information
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subscription plans in body
     */
    @GetMapping("/subscription-plans/course/{courseId}")
    public ResponseEntity<List<SubscriptionPlanDTO>> getAllSubscriptionPlansByCourse(
        @PathVariable Long courseId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of SubscriptionPlans for Course {}", courseId);
        Page<SubscriptionPlanDTO> page;
        if (eagerload) {
            page = subscriptionPlanService.findAllByCourseIdWithEagerRelationships(courseId, pageable);
        } else {
            page = subscriptionPlanService.findAllByCourseId(courseId, pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
