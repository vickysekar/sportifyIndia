package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.OneTimeEventRepository;
import com.sportifyindia.app.service.OneTimeEventService;
import com.sportifyindia.app.service.dto.OneTimeEventDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.OneTimeEvent}.
 */
@RestController
@RequestMapping("/api/one-time-events")
public class OneTimeEventResource {

    private final Logger log = LoggerFactory.getLogger(OneTimeEventResource.class);

    private static final String ENTITY_NAME = "oneTimeEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OneTimeEventService oneTimeEventService;

    private final OneTimeEventRepository oneTimeEventRepository;

    public OneTimeEventResource(OneTimeEventService oneTimeEventService, OneTimeEventRepository oneTimeEventRepository) {
        this.oneTimeEventService = oneTimeEventService;
        this.oneTimeEventRepository = oneTimeEventRepository;
    }

    /**
     * {@code POST  /one-time-events} : Create a new oneTimeEvent.
     *
     * @param oneTimeEventDTO the oneTimeEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new oneTimeEventDTO, or with status {@code 400 (Bad Request)} if the oneTimeEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OneTimeEventDTO> createOneTimeEvent(@Valid @RequestBody OneTimeEventDTO oneTimeEventDTO)
        throws URISyntaxException {
        log.debug("REST request to save OneTimeEvent : {}", oneTimeEventDTO);
        if (oneTimeEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new oneTimeEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OneTimeEventDTO result = oneTimeEventService.save(oneTimeEventDTO);
        return ResponseEntity
            .created(new URI("/api/one-time-events/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /one-time-events/:id} : Updates an existing oneTimeEvent.
     *
     * @param id the id of the oneTimeEventDTO to save.
     * @param oneTimeEventDTO the oneTimeEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oneTimeEventDTO,
     * or with status {@code 400 (Bad Request)} if the oneTimeEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the oneTimeEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OneTimeEventDTO> updateOneTimeEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OneTimeEventDTO oneTimeEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OneTimeEvent : {}, {}", id, oneTimeEventDTO);
        if (oneTimeEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, oneTimeEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!oneTimeEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OneTimeEventDTO result = oneTimeEventService.update(oneTimeEventDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, oneTimeEventDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /one-time-events/:id} : Partial updates given fields of an existing oneTimeEvent, field will ignore if it is null
     *
     * @param id the id of the oneTimeEventDTO to save.
     * @param oneTimeEventDTO the oneTimeEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oneTimeEventDTO,
     * or with status {@code 400 (Bad Request)} if the oneTimeEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the oneTimeEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the oneTimeEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OneTimeEventDTO> partialUpdateOneTimeEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OneTimeEventDTO oneTimeEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OneTimeEvent partially : {}, {}", id, oneTimeEventDTO);
        if (oneTimeEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, oneTimeEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!oneTimeEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OneTimeEventDTO> result = oneTimeEventService.partialUpdate(oneTimeEventDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, oneTimeEventDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /one-time-events} : get all the oneTimeEvents.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oneTimeEvents in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OneTimeEventDTO>> getAllOneTimeEvents(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of OneTimeEvents");
        Page<OneTimeEventDTO> page = oneTimeEventService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /one-time-events/:id} : get the "id" oneTimeEvent.
     *
     * @param id the id of the oneTimeEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the oneTimeEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OneTimeEventDTO> getOneTimeEvent(@PathVariable("id") Long id) {
        log.debug("REST request to get OneTimeEvent : {}", id);
        Optional<OneTimeEventDTO> oneTimeEventDTO = oneTimeEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(oneTimeEventDTO);
    }

    /**
     * {@code DELETE  /one-time-events/:id} : delete the "id" oneTimeEvent.
     *
     * @param id the id of the oneTimeEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOneTimeEvent(@PathVariable("id") Long id) {
        log.debug("REST request to delete OneTimeEvent : {}", id);
        oneTimeEventService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /one-time-events/_search?query=:query} : search for the oneTimeEvent corresponding
     * to the query.
     *
     * @param query the query of the oneTimeEvent search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<OneTimeEventDTO>> searchOneTimeEvents(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of OneTimeEvents for query {}", query);
        try {
            Page<OneTimeEventDTO> page = oneTimeEventService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
