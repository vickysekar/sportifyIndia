package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.UtilityBookingsRepository;
import com.sportifyindia.app.service.UtilityBookingsService;
import com.sportifyindia.app.service.dto.UtilityBookingsDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.UtilityBookings}.
 */
@RestController
@RequestMapping("/api/utility-bookings")
public class UtilityBookingsResource {

    private final Logger log = LoggerFactory.getLogger(UtilityBookingsResource.class);

    private static final String ENTITY_NAME = "utilityBookings";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UtilityBookingsService utilityBookingsService;

    private final UtilityBookingsRepository utilityBookingsRepository;

    public UtilityBookingsResource(UtilityBookingsService utilityBookingsService, UtilityBookingsRepository utilityBookingsRepository) {
        this.utilityBookingsService = utilityBookingsService;
        this.utilityBookingsRepository = utilityBookingsRepository;
    }

    /**
     * {@code POST  /utility-bookings} : Create a new utilityBookings.
     *
     * @param utilityBookingsDTO the utilityBookingsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new utilityBookingsDTO, or with status {@code 400 (Bad Request)} if the utilityBookings has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UtilityBookingsDTO> createUtilityBookings(@Valid @RequestBody UtilityBookingsDTO utilityBookingsDTO)
        throws URISyntaxException {
        log.debug("REST request to save UtilityBookings : {}", utilityBookingsDTO);
        if (utilityBookingsDTO.getId() != null) {
            throw new BadRequestAlertException("A new utilityBookings cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UtilityBookingsDTO result = utilityBookingsService.save(utilityBookingsDTO);
        return ResponseEntity
            .created(new URI("/api/utility-bookings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /utility-bookings/:id} : Updates an existing utilityBookings.
     *
     * @param id the id of the utilityBookingsDTO to save.
     * @param utilityBookingsDTO the utilityBookingsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilityBookingsDTO,
     * or with status {@code 400 (Bad Request)} if the utilityBookingsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the utilityBookingsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilityBookingsDTO> updateUtilityBookings(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UtilityBookingsDTO utilityBookingsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UtilityBookings : {}, {}", id, utilityBookingsDTO);
        if (utilityBookingsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilityBookingsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilityBookingsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UtilityBookingsDTO result = utilityBookingsService.update(utilityBookingsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilityBookingsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /utility-bookings/:id} : Partial updates given fields of an existing utilityBookings, field will ignore if it is null
     *
     * @param id the id of the utilityBookingsDTO to save.
     * @param utilityBookingsDTO the utilityBookingsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilityBookingsDTO,
     * or with status {@code 400 (Bad Request)} if the utilityBookingsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the utilityBookingsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the utilityBookingsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UtilityBookingsDTO> partialUpdateUtilityBookings(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UtilityBookingsDTO utilityBookingsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UtilityBookings partially : {}, {}", id, utilityBookingsDTO);
        if (utilityBookingsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilityBookingsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilityBookingsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UtilityBookingsDTO> result = utilityBookingsService.partialUpdate(utilityBookingsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilityBookingsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /utility-bookings} : get all the utilityBookings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of utilityBookings in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UtilityBookingsDTO>> getAllUtilityBookings(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of UtilityBookings");
        Page<UtilityBookingsDTO> page = utilityBookingsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /utility-bookings/:id} : get the "id" utilityBookings.
     *
     * @param id the id of the utilityBookingsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the utilityBookingsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilityBookingsDTO> getUtilityBookings(@PathVariable("id") Long id) {
        log.debug("REST request to get UtilityBookings : {}", id);
        Optional<UtilityBookingsDTO> utilityBookingsDTO = utilityBookingsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(utilityBookingsDTO);
    }

    /**
     * {@code DELETE  /utility-bookings/:id} : delete the "id" utilityBookings.
     *
     * @param id the id of the utilityBookingsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilityBookings(@PathVariable("id") Long id) {
        log.debug("REST request to delete UtilityBookings : {}", id);
        utilityBookingsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /utility-bookings/_search?query=:query} : search for the utilityBookings corresponding
     * to the query.
     *
     * @param query the query of the utilityBookings search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<UtilityBookingsDTO>> searchUtilityBookings(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of UtilityBookings for query {}", query);
        try {
            Page<UtilityBookingsDTO> page = utilityBookingsService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
