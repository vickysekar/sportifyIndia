package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.UtilitySlotsRepository;
import com.sportifyindia.app.service.UtilitySlotsService;
import com.sportifyindia.app.service.dto.UtilitySlotsDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.UtilitySlots}.
 */
@RestController
@RequestMapping("/api/utility-slots")
public class UtilitySlotsResource {

    private final Logger log = LoggerFactory.getLogger(UtilitySlotsResource.class);

    private static final String ENTITY_NAME = "utilitySlots";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UtilitySlotsService utilitySlotsService;

    private final UtilitySlotsRepository utilitySlotsRepository;

    public UtilitySlotsResource(UtilitySlotsService utilitySlotsService, UtilitySlotsRepository utilitySlotsRepository) {
        this.utilitySlotsService = utilitySlotsService;
        this.utilitySlotsRepository = utilitySlotsRepository;
    }

    /**
     * {@code POST  /utility-slots} : Create a new utilitySlots.
     *
     * @param utilitySlotsDTO the utilitySlotsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new utilitySlotsDTO, or with status {@code 400 (Bad Request)} if the utilitySlots has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UtilitySlotsDTO> createUtilitySlots(@Valid @RequestBody UtilitySlotsDTO utilitySlotsDTO)
        throws URISyntaxException {
        log.debug("REST request to save UtilitySlots : {}", utilitySlotsDTO);
        if (utilitySlotsDTO.getId() != null) {
            throw new BadRequestAlertException("A new utilitySlots cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UtilitySlotsDTO result = utilitySlotsService.save(utilitySlotsDTO);
        return ResponseEntity
            .created(new URI("/api/utility-slots/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /utility-slots/:id} : Updates an existing utilitySlots.
     *
     * @param id the id of the utilitySlotsDTO to save.
     * @param utilitySlotsDTO the utilitySlotsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilitySlotsDTO,
     * or with status {@code 400 (Bad Request)} if the utilitySlotsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the utilitySlotsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilitySlotsDTO> updateUtilitySlots(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UtilitySlotsDTO utilitySlotsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UtilitySlots : {}, {}", id, utilitySlotsDTO);
        if (utilitySlotsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilitySlotsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilitySlotsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        UtilitySlotsDTO result = utilitySlotsService.update(utilitySlotsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilitySlotsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /utility-slots/:id} : Partial updates given fields of an existing utilitySlots, field will ignore if it is null
     *
     * @param id the id of the utilitySlotsDTO to save.
     * @param utilitySlotsDTO the utilitySlotsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilitySlotsDTO,
     * or with status {@code 400 (Bad Request)} if the utilitySlotsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the utilitySlotsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the utilitySlotsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UtilitySlotsDTO> partialUpdateUtilitySlots(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UtilitySlotsDTO utilitySlotsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UtilitySlots partially : {}, {}", id, utilitySlotsDTO);
        if (utilitySlotsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilitySlotsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!utilitySlotsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UtilitySlotsDTO> result = utilitySlotsService.partialUpdate(utilitySlotsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilitySlotsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /utility-slots} : get all the utilitySlots.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of utilitySlots in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UtilitySlotsDTO>> getAllUtilitySlots(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of UtilitySlots");
        Page<UtilitySlotsDTO> page = utilitySlotsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /utility-slots/:id} : get the "id" utilitySlots.
     *
     * @param id the id of the utilitySlotsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the utilitySlotsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilitySlotsDTO> getUtilitySlots(@PathVariable("id") Long id) {
        log.debug("REST request to get UtilitySlots : {}", id);
        Optional<UtilitySlotsDTO> utilitySlotsDTO = utilitySlotsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(utilitySlotsDTO);
    }

    /**
     * {@code DELETE  /utility-slots/:id} : delete the "id" utilitySlots.
     *
     * @param id the id of the utilitySlotsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilitySlots(@PathVariable("id") Long id) {
        log.debug("REST request to delete UtilitySlots : {}", id);
        utilitySlotsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /utility-slots/_search?query=:query} : search for the utilitySlots corresponding
     * to the query.
     *
     * @param query the query of the utilitySlots search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<UtilitySlotsDTO>> searchUtilitySlots(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of UtilitySlots for query {}", query);
        try {
            Page<UtilitySlotsDTO> page = utilitySlotsService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
