package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.OneTimeEventSubscribersRepository;
import com.sportifyindia.app.service.OneTimeEventSubscribersService;
import com.sportifyindia.app.service.dto.OneTimeEventSubscribersDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.OneTimeEventSubscribers}.
 */
@RestController
@RequestMapping("/api/one-time-event-subscribers")
public class OneTimeEventSubscribersResource {

    private final Logger log = LoggerFactory.getLogger(OneTimeEventSubscribersResource.class);

    private static final String ENTITY_NAME = "oneTimeEventSubscribers";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OneTimeEventSubscribersService oneTimeEventSubscribersService;

    private final OneTimeEventSubscribersRepository oneTimeEventSubscribersRepository;

    public OneTimeEventSubscribersResource(
        OneTimeEventSubscribersService oneTimeEventSubscribersService,
        OneTimeEventSubscribersRepository oneTimeEventSubscribersRepository
    ) {
        this.oneTimeEventSubscribersService = oneTimeEventSubscribersService;
        this.oneTimeEventSubscribersRepository = oneTimeEventSubscribersRepository;
    }

    /**
     * {@code POST  /one-time-event-subscribers} : Create a new oneTimeEventSubscribers.
     *
     * @param oneTimeEventSubscribersDTO the oneTimeEventSubscribersDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new oneTimeEventSubscribersDTO, or with status {@code 400 (Bad Request)} if the oneTimeEventSubscribers has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OneTimeEventSubscribersDTO> createOneTimeEventSubscribers(
        @Valid @RequestBody OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO
    ) throws URISyntaxException {
        log.debug("REST request to save OneTimeEventSubscribers : {}", oneTimeEventSubscribersDTO);
        if (oneTimeEventSubscribersDTO.getId() != null) {
            throw new BadRequestAlertException("A new oneTimeEventSubscribers cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OneTimeEventSubscribersDTO result = oneTimeEventSubscribersService.save(oneTimeEventSubscribersDTO);
        return ResponseEntity
            .created(new URI("/api/one-time-event-subscribers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /one-time-event-subscribers/:id} : Updates an existing oneTimeEventSubscribers.
     *
     * @param id the id of the oneTimeEventSubscribersDTO to save.
     * @param oneTimeEventSubscribersDTO the oneTimeEventSubscribersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oneTimeEventSubscribersDTO,
     * or with status {@code 400 (Bad Request)} if the oneTimeEventSubscribersDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the oneTimeEventSubscribersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OneTimeEventSubscribersDTO> updateOneTimeEventSubscribers(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OneTimeEventSubscribers : {}, {}", id, oneTimeEventSubscribersDTO);
        if (oneTimeEventSubscribersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, oneTimeEventSubscribersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!oneTimeEventSubscribersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OneTimeEventSubscribersDTO result = oneTimeEventSubscribersService.update(oneTimeEventSubscribersDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, oneTimeEventSubscribersDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /one-time-event-subscribers/:id} : Partial updates given fields of an existing oneTimeEventSubscribers, field will ignore if it is null
     *
     * @param id the id of the oneTimeEventSubscribersDTO to save.
     * @param oneTimeEventSubscribersDTO the oneTimeEventSubscribersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oneTimeEventSubscribersDTO,
     * or with status {@code 400 (Bad Request)} if the oneTimeEventSubscribersDTO is not valid,
     * or with status {@code 404 (Not Found)} if the oneTimeEventSubscribersDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the oneTimeEventSubscribersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OneTimeEventSubscribersDTO> partialUpdateOneTimeEventSubscribers(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OneTimeEventSubscribers partially : {}, {}", id, oneTimeEventSubscribersDTO);
        if (oneTimeEventSubscribersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, oneTimeEventSubscribersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!oneTimeEventSubscribersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OneTimeEventSubscribersDTO> result = oneTimeEventSubscribersService.partialUpdate(oneTimeEventSubscribersDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, oneTimeEventSubscribersDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /one-time-event-subscribers} : get all the oneTimeEventSubscribers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oneTimeEventSubscribers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OneTimeEventSubscribersDTO>> getAllOneTimeEventSubscribers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of OneTimeEventSubscribers");
        Page<OneTimeEventSubscribersDTO> page = oneTimeEventSubscribersService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /one-time-event-subscribers/:id} : get the "id" oneTimeEventSubscribers.
     *
     * @param id the id of the oneTimeEventSubscribersDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the oneTimeEventSubscribersDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OneTimeEventSubscribersDTO> getOneTimeEventSubscribers(@PathVariable("id") Long id) {
        log.debug("REST request to get OneTimeEventSubscribers : {}", id);
        Optional<OneTimeEventSubscribersDTO> oneTimeEventSubscribersDTO = oneTimeEventSubscribersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(oneTimeEventSubscribersDTO);
    }

    /**
     * {@code DELETE  /one-time-event-subscribers/:id} : delete the "id" oneTimeEventSubscribers.
     *
     * @param id the id of the oneTimeEventSubscribersDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOneTimeEventSubscribers(@PathVariable("id") Long id) {
        log.debug("REST request to delete OneTimeEventSubscribers : {}", id);
        oneTimeEventSubscribersService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /one-time-event-subscribers/_search?query=:query} : search for the oneTimeEventSubscribers corresponding
     * to the query.
     *
     * @param query the query of the oneTimeEventSubscribers search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<OneTimeEventSubscribersDTO>> searchOneTimeEventSubscribers(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of OneTimeEventSubscribers for query {}", query);
        try {
            Page<OneTimeEventSubscribersDTO> page = oneTimeEventSubscribersService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
