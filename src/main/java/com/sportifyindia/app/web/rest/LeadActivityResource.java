package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.LeadActivityRepository;
import com.sportifyindia.app.service.LeadActivityService;
import com.sportifyindia.app.service.dto.LeadActivityDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.LeadActivity}.
 */
@RestController
@RequestMapping("/api/lead-activities")
public class LeadActivityResource {

    private final Logger log = LoggerFactory.getLogger(LeadActivityResource.class);

    private static final String ENTITY_NAME = "leadActivity";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LeadActivityService leadActivityService;

    private final LeadActivityRepository leadActivityRepository;

    public LeadActivityResource(LeadActivityService leadActivityService, LeadActivityRepository leadActivityRepository) {
        this.leadActivityService = leadActivityService;
        this.leadActivityRepository = leadActivityRepository;
    }

    /**
     * {@code POST  /lead-activities} : Create a new leadActivity.
     *
     * @param leadActivityDTO the leadActivityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new leadActivityDTO, or with status {@code 400 (Bad Request)} if the leadActivity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LeadActivityDTO> createLeadActivity(@Valid @RequestBody LeadActivityDTO leadActivityDTO)
        throws URISyntaxException {
        log.debug("REST request to save LeadActivity : {}", leadActivityDTO);
        if (leadActivityDTO.getId() != null) {
            throw new BadRequestAlertException("A new leadActivity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LeadActivityDTO result = leadActivityService.save(leadActivityDTO);
        return ResponseEntity
            .created(new URI("/api/lead-activities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lead-activities/:id} : Updates an existing leadActivity.
     *
     * @param id the id of the leadActivityDTO to save.
     * @param leadActivityDTO the leadActivityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated leadActivityDTO,
     * or with status {@code 400 (Bad Request)} if the leadActivityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the leadActivityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LeadActivityDTO> updateLeadActivity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LeadActivityDTO leadActivityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update LeadActivity : {}, {}", id, leadActivityDTO);
        if (leadActivityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, leadActivityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leadActivityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LeadActivityDTO result = leadActivityService.update(leadActivityDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, leadActivityDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /lead-activities/:id} : Partial updates given fields of an existing leadActivity, field will ignore if it is null
     *
     * @param id the id of the leadActivityDTO to save.
     * @param leadActivityDTO the leadActivityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated leadActivityDTO,
     * or with status {@code 400 (Bad Request)} if the leadActivityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the leadActivityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the leadActivityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LeadActivityDTO> partialUpdateLeadActivity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LeadActivityDTO leadActivityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update LeadActivity partially : {}, {}", id, leadActivityDTO);
        if (leadActivityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, leadActivityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leadActivityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LeadActivityDTO> result = leadActivityService.partialUpdate(leadActivityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, leadActivityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /lead-activities} : get all the leadActivities.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of leadActivities in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LeadActivityDTO>> getAllLeadActivities(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of LeadActivities");
        Page<LeadActivityDTO> page = leadActivityService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /lead-activities/:id} : get the "id" leadActivity.
     *
     * @param id the id of the leadActivityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the leadActivityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeadActivityDTO> getLeadActivity(@PathVariable("id") Long id) {
        log.debug("REST request to get LeadActivity : {}", id);
        Optional<LeadActivityDTO> leadActivityDTO = leadActivityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(leadActivityDTO);
    }

    /**
     * {@code DELETE  /lead-activities/:id} : delete the "id" leadActivity.
     *
     * @param id the id of the leadActivityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeadActivity(@PathVariable("id") Long id) {
        log.debug("REST request to delete LeadActivity : {}", id);
        leadActivityService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /lead-activities/_search?query=:query} : search for the leadActivity corresponding
     * to the query.
     *
     * @param query the query of the leadActivity search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<LeadActivityDTO>> searchLeadActivities(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of LeadActivities for query {}", query);
        try {
            Page<LeadActivityDTO> page = leadActivityService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
