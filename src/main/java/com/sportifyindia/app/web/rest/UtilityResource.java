package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.UtilityRepository;
import com.sportifyindia.app.service.UtilityService;
import com.sportifyindia.app.service.dto.UtilityDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.Utility}.
 */
@RestController
@RequestMapping("/api/utilities")
public class UtilityResource {

    private final Logger log = LoggerFactory.getLogger(UtilityResource.class);

    private static final String ENTITY_NAME = "utility";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UtilityService utilityService;

    private final UtilityRepository utilityRepository;

    public UtilityResource(UtilityService utilityService, UtilityRepository utilityRepository) {
        this.utilityService = utilityService;
        this.utilityRepository = utilityRepository;
    }

    /**
     * {@code POST  /utilities} : Create a new utility.
     *
     * @param utilityDTO the utilityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new utilityDTO, or with status {@code 400 (Bad Request)} if the utility has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UtilityDTO> createUtility(@Valid @RequestBody UtilityDTO utilityDTO) throws URISyntaxException {
        log.debug("REST request to save Utility : {}", utilityDTO);
        if (utilityDTO.getId() != null) {
            throw new BadRequestAlertException("A new utility cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UtilityDTO result = utilityService.save(utilityDTO);
        return ResponseEntity
            .created(new URI("/api/utilities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /utilities/:id} : Updates an existing utility.
     *
     * @param id the id of the utilityDTO to save.
     * @param utilityDTO the utilityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilityDTO,
     * or with status {@code 400 (Bad Request)} if the utilityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the utilityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilityDTO> updateUtility(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UtilityDTO utilityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Utility : {}, {}", id, utilityDTO);
        if (utilityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UtilityDTO result = utilityService.update(utilityDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilityDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /utilities/:id} : Partial updates given fields of an existing utility, field will ignore if it is null
     *
     * @param id the id of the utilityDTO to save.
     * @param utilityDTO the utilityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilityDTO,
     * or with status {@code 400 (Bad Request)} if the utilityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the utilityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the utilityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UtilityDTO> partialUpdateUtility(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UtilityDTO utilityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Utility partially : {}, {}", id, utilityDTO);
        if (utilityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UtilityDTO> result = utilityService.partialUpdate(utilityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /utilities} : get all the utilities.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of utilities in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UtilityDTO>> getAllUtilities(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Utilities");
        Page<UtilityDTO> page;
        if (eagerload) {
            page = utilityService.findAllWithEagerRelationships(pageable);
        } else {
            page = utilityService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /utilities/:id} : get the "id" utility.
     *
     * @param id the id of the utilityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the utilityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilityDTO> getUtility(@PathVariable("id") Long id) {
        log.debug("REST request to get Utility : {}", id);
        Optional<UtilityDTO> utilityDTO = utilityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(utilityDTO);
    }

    /**
     * {@code DELETE  /utilities/:id} : delete the "id" utility.
     *
     * @param id the id of the utilityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtility(@PathVariable("id") Long id) {
        log.debug("REST request to delete Utility : {}", id);
        utilityService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /utilities/_search?query=:query} : search for the utility corresponding
     * to the query.
     *
     * @param query the query of the utility search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<UtilityDTO>> searchUtilities(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Utilities for query {}", query);
        try {
            Page<UtilityDTO> page = utilityService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
