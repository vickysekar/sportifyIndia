package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.UtilityAvailableDaysRepository;
import com.sportifyindia.app.service.UtilityAvailableDaysService;
import com.sportifyindia.app.service.dto.UtilityAvailableDaysDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.UtilityAvailableDays}.
 */
@RestController
@RequestMapping("/api/utility-available-days")
public class UtilityAvailableDaysResource {

    private final Logger log = LoggerFactory.getLogger(UtilityAvailableDaysResource.class);

    private static final String ENTITY_NAME = "utilityAvailableDays";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UtilityAvailableDaysService utilityAvailableDaysService;

    private final UtilityAvailableDaysRepository utilityAvailableDaysRepository;

    public UtilityAvailableDaysResource(
        UtilityAvailableDaysService utilityAvailableDaysService,
        UtilityAvailableDaysRepository utilityAvailableDaysRepository
    ) {
        this.utilityAvailableDaysService = utilityAvailableDaysService;
        this.utilityAvailableDaysRepository = utilityAvailableDaysRepository;
    }

    /**
     * {@code POST  /utility-available-days} : Create a new utilityAvailableDays.
     *
     * @param utilityAvailableDaysDTO the utilityAvailableDaysDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new utilityAvailableDaysDTO, or with status {@code 400 (Bad Request)} if the utilityAvailableDays has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UtilityAvailableDaysDTO> createUtilityAvailableDays(
        @Valid @RequestBody UtilityAvailableDaysDTO utilityAvailableDaysDTO
    ) throws URISyntaxException {
        log.debug("REST request to save UtilityAvailableDays : {}", utilityAvailableDaysDTO);
        if (utilityAvailableDaysDTO.getId() != null) {
            throw new BadRequestAlertException("A new utilityAvailableDays cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UtilityAvailableDaysDTO result = utilityAvailableDaysService.save(utilityAvailableDaysDTO);
        return ResponseEntity
            .created(new URI("/api/utility-available-days/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /utility-available-days/:id} : Updates an existing utilityAvailableDays.
     *
     * @param id the id of the utilityAvailableDaysDTO to save.
     * @param utilityAvailableDaysDTO the utilityAvailableDaysDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilityAvailableDaysDTO,
     * or with status {@code 400 (Bad Request)} if the utilityAvailableDaysDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the utilityAvailableDaysDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilityAvailableDaysDTO> updateUtilityAvailableDays(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UtilityAvailableDaysDTO utilityAvailableDaysDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UtilityAvailableDays : {}, {}", id, utilityAvailableDaysDTO);
        if (utilityAvailableDaysDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilityAvailableDaysDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilityAvailableDaysRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UtilityAvailableDaysDTO result = utilityAvailableDaysService.update(utilityAvailableDaysDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilityAvailableDaysDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /utility-available-days/:id} : Partial updates given fields of an existing utilityAvailableDays, field will ignore if it is null
     *
     * @param id the id of the utilityAvailableDaysDTO to save.
     * @param utilityAvailableDaysDTO the utilityAvailableDaysDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilityAvailableDaysDTO,
     * or with status {@code 400 (Bad Request)} if the utilityAvailableDaysDTO is not valid,
     * or with status {@code 404 (Not Found)} if the utilityAvailableDaysDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the utilityAvailableDaysDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UtilityAvailableDaysDTO> partialUpdateUtilityAvailableDays(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UtilityAvailableDaysDTO utilityAvailableDaysDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UtilityAvailableDays partially : {}, {}", id, utilityAvailableDaysDTO);
        if (utilityAvailableDaysDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilityAvailableDaysDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilityAvailableDaysRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UtilityAvailableDaysDTO> result = utilityAvailableDaysService.partialUpdate(utilityAvailableDaysDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilityAvailableDaysDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /utility-available-days} : get all the utilityAvailableDays.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of utilityAvailableDays in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UtilityAvailableDaysDTO>> getAllUtilityAvailableDays(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of UtilityAvailableDays");
        Page<UtilityAvailableDaysDTO> page = utilityAvailableDaysService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /utility-available-days/:id} : get the "id" utilityAvailableDays.
     *
     * @param id the id of the utilityAvailableDaysDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the utilityAvailableDaysDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilityAvailableDaysDTO> getUtilityAvailableDays(@PathVariable("id") Long id) {
        log.debug("REST request to get UtilityAvailableDays : {}", id);
        Optional<UtilityAvailableDaysDTO> utilityAvailableDaysDTO = utilityAvailableDaysService.findOne(id);
        return ResponseUtil.wrapOrNotFound(utilityAvailableDaysDTO);
    }

    /**
     * {@code DELETE  /utility-available-days/:id} : delete the "id" utilityAvailableDays.
     *
     * @param id the id of the utilityAvailableDaysDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilityAvailableDays(@PathVariable("id") Long id) {
        log.debug("REST request to delete UtilityAvailableDays : {}", id);
        utilityAvailableDaysService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /utility-available-days/_search?query=:query} : search for the utilityAvailableDays corresponding
     * to the query.
     *
     * @param query the query of the utilityAvailableDays search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<UtilityAvailableDaysDTO>> searchUtilityAvailableDays(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of UtilityAvailableDays for query {}", query);
        try {
            Page<UtilityAvailableDaysDTO> page = utilityAvailableDaysService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
