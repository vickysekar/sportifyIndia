package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.domain.enumeration.EventStatusEnum;
import com.sportifyindia.app.repository.OneTimeEventRepository;
import com.sportifyindia.app.service.OneTimeEventService;
import com.sportifyindia.app.service.dto.OneTimeEventDTO;
import com.sportifyindia.app.web.rest.errors.BadRequestAlertException;
import com.sportifyindia.app.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api")
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
     * {@code POST  /one-time-events/facility/{facilityId}} : Create a new oneTimeEvent for a facility.
     * Only ROLE_OWNER and ROLE_ADMIN can create events.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param facilityId the ID of the facility
     * @param oneTimeEventDTO the oneTimeEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new oneTimeEventDTO,
     * or with status {@code 400 (Bad Request)} if the oneTimeEvent has already an ID,
     * or with status {@code 403 (Forbidden)} if the user doesn't have permission.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/one-time-events/facility/{facilityId}")
    public ResponseEntity<OneTimeEventDTO> createOneTimeEventForFacility(
        @PathVariable Long facilityId,
        @Valid @RequestBody OneTimeEventDTO oneTimeEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to save OneTimeEvent for Facility {}: {}", facilityId, oneTimeEventDTO);
        if (oneTimeEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new oneTimeEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        try {
            OneTimeEventDTO result = oneTimeEventService.createOneTimeEventForFacility(facilityId, oneTimeEventDTO);
            return ResponseEntity
                .created(new URI("/api/one-time-events/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * {@code GET  /one-time-events/facility/{facilityId}} : get all oneTimeEvents by facility ID.
     *
     * @param facilityId the ID of the facility
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oneTimeEvents in body.
     */
    @GetMapping("/one-time-events/facility/{facilityId}")
    public ResponseEntity<List<OneTimeEventDTO>> getAllOneTimeEventsByFacilityId(
        @PathVariable Long facilityId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of OneTimeEvents for facility : {}", facilityId);
        Page<OneTimeEventDTO> page = oneTimeEventService.findAllByFacilityId(facilityId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /one-time-events/facility/{facilityId}/status/{status}} : get all oneTimeEvents by facility ID and status.
     *
     * @param facilityId the ID of the facility
     * @param status the status of the events
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oneTimeEvents in body.
     */
    @GetMapping("/one-time-events/facility/{facilityId}/status/{status}")
    public ResponseEntity<List<OneTimeEventDTO>> getAllOneTimeEventsByFacilityIdAndStatus(
        @PathVariable Long facilityId,
        @PathVariable EventStatusEnum status,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of OneTimeEvents for facility {} and status {}", facilityId, status);
        Page<OneTimeEventDTO> page = oneTimeEventService.findAllByFacilityIdAndStatus(facilityId, status, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /one-time-events/facility/{facilityId}/date} : get all oneTimeEvents by facility ID and date.
     * If no date is provided, uses current date.
     *
     * @param facilityId the ID of the facility
     * @param date the date to filter events (optional)
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oneTimeEvents in body.
     */
    @GetMapping("/one-time-events/facility/{facilityId}/date")
    public ResponseEntity<List<OneTimeEventDTO>> getAllOneTimeEventsByFacilityIdAndDate(
        @PathVariable Long facilityId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of OneTimeEvents for facility {} and date {}", facilityId, date);
        Page<OneTimeEventDTO> page = oneTimeEventService.findAllByFacilityIdAndDate(facilityId, date, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /one-time-events/facility/{facilityId}/status/{status}/date} : get all oneTimeEvents by facility ID, status and date.
     * If no date is provided, uses current date.
     *
     * @param facilityId the ID of the facility
     * @param status the status of the events
     * @param date the date to filter events (optional)
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oneTimeEvents in body.
     */
    @GetMapping("/one-time-events/facility/{facilityId}/status/{status}/date")
    public ResponseEntity<List<OneTimeEventDTO>> getAllOneTimeEventsByFacilityIdAndStatusAndDate(
        @PathVariable Long facilityId,
        @PathVariable EventStatusEnum status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of OneTimeEvents for facility {}, status {} and date {}", facilityId, status, date);
        Page<OneTimeEventDTO> page = oneTimeEventService.findAllByFacilityIdAndStatusAndDate(facilityId, status, date, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code PUT  /one-time-events/:id} : Updates an existing oneTimeEvent.
     * Only ROLE_OWNER and ROLE_ADMIN can update events.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param id the id of the oneTimeEventDTO to save.
     * @param oneTimeEventDTO the oneTimeEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oneTimeEventDTO,
     * or with status {@code 400 (Bad Request)} if the oneTimeEventDTO is not valid,
     * or with status {@code 403 (Forbidden)} if the user doesn't have permission,
     * or with status {@code 500 (Internal Server Error)} if the oneTimeEventDTO couldn't be updated.
     */
    @PutMapping("/one-time-events/{id}")
    public ResponseEntity<OneTimeEventDTO> updateOneTimeEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OneTimeEventDTO oneTimeEventDTO
    ) {
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

        try {
            OneTimeEventDTO result = oneTimeEventService.update(oneTimeEventDTO);
            return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, oneTimeEventDTO.getId().toString()))
                .body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * {@code DELETE  /one-time-events/:id} : delete the "id" oneTimeEvent.
     * Only ROLE_OWNER and ROLE_ADMIN can delete events.
     * For ROLE_OWNER, they must own the facility.
     *
     * @param id the id of the oneTimeEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/one-time-events/{id}")
    public ResponseEntity<Void> deleteOneTimeEvent(@PathVariable Long id) {
        log.debug("REST request to delete OneTimeEvent : {}", id);
        try {
            oneTimeEventService.delete(id);
            return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * {@code GET  /one-time-events/:id} : get the "id" oneTimeEvent.
     *
     * @param id the id of the oneTimeEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the oneTimeEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/one-time-events/{id}")
    public ResponseEntity<OneTimeEventDTO> getOneTimeEvent(@PathVariable("id") Long id) {
        log.debug("REST request to get OneTimeEvent : {}", id);
        Optional<OneTimeEventDTO> oneTimeEventDTO = oneTimeEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(oneTimeEventDTO);
    }

    /**
     * {@code SEARCH  /one-time-events/_search?query=:query} : search for the oneTimeEvent corresponding
     * to the query.
     *
     * @param query the query of the oneTimeEvent search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/one-time-events/_search")
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
