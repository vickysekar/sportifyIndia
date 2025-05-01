package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.TimeSlotsRepository;
import com.sportifyindia.app.service.TimeSlotsService;
import com.sportifyindia.app.service.dto.TimeSlotsDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.TimeSlots}.
 */
@RestController
@RequestMapping("/api/time-slots")
public class TimeSlotsResource {

    private final Logger log = LoggerFactory.getLogger(TimeSlotsResource.class);

    private static final String ENTITY_NAME = "timeSlots";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TimeSlotsService timeSlotsService;

    private final TimeSlotsRepository timeSlotsRepository;

    public TimeSlotsResource(TimeSlotsService timeSlotsService, TimeSlotsRepository timeSlotsRepository) {
        this.timeSlotsService = timeSlotsService;
        this.timeSlotsRepository = timeSlotsRepository;
    }

    /**
     * {@code POST  /time-slots} : Create a new timeSlots.
     *
     * @param timeSlotsDTO the timeSlotsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new timeSlotsDTO, or with status {@code 400 (Bad Request)} if the timeSlots has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TimeSlotsDTO> createTimeSlots(@Valid @RequestBody TimeSlotsDTO timeSlotsDTO) throws URISyntaxException {
        log.debug("REST request to save TimeSlots : {}", timeSlotsDTO);
        if (timeSlotsDTO.getId() != null) {
            throw new BadRequestAlertException("A new timeSlots cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TimeSlotsDTO result = timeSlotsService.save(timeSlotsDTO);
        return ResponseEntity
            .created(new URI("/api/time-slots/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /time-slots/:id} : Updates an existing timeSlots.
     *
     * @param id the id of the timeSlotsDTO to save.
     * @param timeSlotsDTO the timeSlotsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeSlotsDTO,
     * or with status {@code 400 (Bad Request)} if the timeSlotsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the timeSlotsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TimeSlotsDTO> updateTimeSlots(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TimeSlotsDTO timeSlotsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TimeSlots : {}, {}", id, timeSlotsDTO);
        if (timeSlotsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeSlotsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeSlotsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TimeSlotsDTO result = timeSlotsService.update(timeSlotsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeSlotsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /time-slots/:id} : Partial updates given fields of an existing timeSlots, field will ignore if it is null
     *
     * @param id the id of the timeSlotsDTO to save.
     * @param timeSlotsDTO the timeSlotsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated timeSlotsDTO,
     * or with status {@code 400 (Bad Request)} if the timeSlotsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the timeSlotsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the timeSlotsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TimeSlotsDTO> partialUpdateTimeSlots(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TimeSlotsDTO timeSlotsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TimeSlots partially : {}, {}", id, timeSlotsDTO);
        if (timeSlotsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, timeSlotsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!timeSlotsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TimeSlotsDTO> result = timeSlotsService.partialUpdate(timeSlotsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, timeSlotsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /time-slots} : get all the timeSlots.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of timeSlots in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TimeSlotsDTO>> getAllTimeSlots(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of TimeSlots");
        Page<TimeSlotsDTO> page = timeSlotsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /time-slots/:id} : get the "id" timeSlots.
     *
     * @param id the id of the timeSlotsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the timeSlotsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotsDTO> getTimeSlots(@PathVariable("id") Long id) {
        log.debug("REST request to get TimeSlots : {}", id);
        Optional<TimeSlotsDTO> timeSlotsDTO = timeSlotsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(timeSlotsDTO);
    }

    /**
     * {@code DELETE  /time-slots/:id} : delete the "id" timeSlots.
     *
     * @param id the id of the timeSlotsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlots(@PathVariable("id") Long id) {
        log.debug("REST request to delete TimeSlots : {}", id);
        timeSlotsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /time-slots/_search?query=:query} : search for the timeSlots corresponding
     * to the query.
     *
     * @param query the query of the timeSlots search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<TimeSlotsDTO>> searchTimeSlots(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TimeSlots for query {}", query);
        try {
            Page<TimeSlotsDTO> page = timeSlotsService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
