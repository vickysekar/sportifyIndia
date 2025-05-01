package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.UtilityExceptionDaysRepository;
import com.sportifyindia.app.service.UtilityExceptionDaysService;
import com.sportifyindia.app.service.dto.UtilityExceptionDaysDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.UtilityExceptionDays}.
 */
@RestController
@RequestMapping("/api/utility-exception-days")
public class UtilityExceptionDaysResource {

    private final Logger log = LoggerFactory.getLogger(UtilityExceptionDaysResource.class);

    private static final String ENTITY_NAME = "utilityExceptionDays";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UtilityExceptionDaysService utilityExceptionDaysService;

    private final UtilityExceptionDaysRepository utilityExceptionDaysRepository;

    public UtilityExceptionDaysResource(
        UtilityExceptionDaysService utilityExceptionDaysService,
        UtilityExceptionDaysRepository utilityExceptionDaysRepository
    ) {
        this.utilityExceptionDaysService = utilityExceptionDaysService;
        this.utilityExceptionDaysRepository = utilityExceptionDaysRepository;
    }

    /**
     * {@code POST  /utility-exception-days} : Create a new utilityExceptionDays.
     *
     * @param utilityExceptionDaysDTO the utilityExceptionDaysDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new utilityExceptionDaysDTO, or with status {@code 400 (Bad Request)} if the utilityExceptionDays has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UtilityExceptionDaysDTO> createUtilityExceptionDays(
        @Valid @RequestBody UtilityExceptionDaysDTO utilityExceptionDaysDTO
    ) throws URISyntaxException {
        log.debug("REST request to save UtilityExceptionDays : {}", utilityExceptionDaysDTO);
        if (utilityExceptionDaysDTO.getId() != null) {
            throw new BadRequestAlertException("A new utilityExceptionDays cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UtilityExceptionDaysDTO result = utilityExceptionDaysService.save(utilityExceptionDaysDTO);
        return ResponseEntity
            .created(new URI("/api/utility-exception-days/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /utility-exception-days/:id} : Updates an existing utilityExceptionDays.
     *
     * @param id the id of the utilityExceptionDaysDTO to save.
     * @param utilityExceptionDaysDTO the utilityExceptionDaysDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilityExceptionDaysDTO,
     * or with status {@code 400 (Bad Request)} if the utilityExceptionDaysDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the utilityExceptionDaysDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilityExceptionDaysDTO> updateUtilityExceptionDays(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UtilityExceptionDaysDTO utilityExceptionDaysDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UtilityExceptionDays : {}, {}", id, utilityExceptionDaysDTO);
        if (utilityExceptionDaysDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilityExceptionDaysDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilityExceptionDaysRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UtilityExceptionDaysDTO result = utilityExceptionDaysService.update(utilityExceptionDaysDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilityExceptionDaysDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /utility-exception-days/:id} : Partial updates given fields of an existing utilityExceptionDays, field will ignore if it is null
     *
     * @param id the id of the utilityExceptionDaysDTO to save.
     * @param utilityExceptionDaysDTO the utilityExceptionDaysDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilityExceptionDaysDTO,
     * or with status {@code 400 (Bad Request)} if the utilityExceptionDaysDTO is not valid,
     * or with status {@code 404 (Not Found)} if the utilityExceptionDaysDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the utilityExceptionDaysDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UtilityExceptionDaysDTO> partialUpdateUtilityExceptionDays(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UtilityExceptionDaysDTO utilityExceptionDaysDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UtilityExceptionDays partially : {}, {}", id, utilityExceptionDaysDTO);
        if (utilityExceptionDaysDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilityExceptionDaysDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilityExceptionDaysRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UtilityExceptionDaysDTO> result = utilityExceptionDaysService.partialUpdate(utilityExceptionDaysDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilityExceptionDaysDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /utility-exception-days} : get all the utilityExceptionDays.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of utilityExceptionDays in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UtilityExceptionDaysDTO>> getAllUtilityExceptionDays(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of UtilityExceptionDays");
        Page<UtilityExceptionDaysDTO> page = utilityExceptionDaysService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /utility-exception-days/:id} : get the "id" utilityExceptionDays.
     *
     * @param id the id of the utilityExceptionDaysDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the utilityExceptionDaysDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilityExceptionDaysDTO> getUtilityExceptionDays(@PathVariable("id") Long id) {
        log.debug("REST request to get UtilityExceptionDays : {}", id);
        Optional<UtilityExceptionDaysDTO> utilityExceptionDaysDTO = utilityExceptionDaysService.findOne(id);
        return ResponseUtil.wrapOrNotFound(utilityExceptionDaysDTO);
    }

    /**
     * {@code DELETE  /utility-exception-days/:id} : delete the "id" utilityExceptionDays.
     *
     * @param id the id of the utilityExceptionDaysDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilityExceptionDays(@PathVariable("id") Long id) {
        log.debug("REST request to delete UtilityExceptionDays : {}", id);
        utilityExceptionDaysService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /utility-exception-days/_search?query=:query} : search for the utilityExceptionDays corresponding
     * to the query.
     *
     * @param query the query of the utilityExceptionDays search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<UtilityExceptionDaysDTO>> searchUtilityExceptionDays(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of UtilityExceptionDays for query {}", query);
        try {
            Page<UtilityExceptionDaysDTO> page = utilityExceptionDaysService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
