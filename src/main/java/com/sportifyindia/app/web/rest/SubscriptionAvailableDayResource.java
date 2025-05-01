package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.SubscriptionAvailableDayRepository;
import com.sportifyindia.app.service.SubscriptionAvailableDayService;
import com.sportifyindia.app.service.dto.SubscriptionAvailableDayDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.SubscriptionAvailableDay}.
 */
@RestController
@RequestMapping("/api/subscription-available-days")
public class SubscriptionAvailableDayResource {

    private final Logger log = LoggerFactory.getLogger(SubscriptionAvailableDayResource.class);

    private static final String ENTITY_NAME = "subscriptionAvailableDay";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubscriptionAvailableDayService subscriptionAvailableDayService;

    private final SubscriptionAvailableDayRepository subscriptionAvailableDayRepository;

    public SubscriptionAvailableDayResource(
        SubscriptionAvailableDayService subscriptionAvailableDayService,
        SubscriptionAvailableDayRepository subscriptionAvailableDayRepository
    ) {
        this.subscriptionAvailableDayService = subscriptionAvailableDayService;
        this.subscriptionAvailableDayRepository = subscriptionAvailableDayRepository;
    }

    /**
     * {@code POST  /subscription-available-days} : Create a new subscriptionAvailableDay.
     *
     * @param subscriptionAvailableDayDTO the subscriptionAvailableDayDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subscriptionAvailableDayDTO, or with status {@code 400 (Bad Request)} if the subscriptionAvailableDay has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SubscriptionAvailableDayDTO> createSubscriptionAvailableDay(
        @Valid @RequestBody SubscriptionAvailableDayDTO subscriptionAvailableDayDTO
    ) throws URISyntaxException {
        log.debug("REST request to save SubscriptionAvailableDay : {}", subscriptionAvailableDayDTO);
        if (subscriptionAvailableDayDTO.getId() != null) {
            throw new BadRequestAlertException("A new subscriptionAvailableDay cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SubscriptionAvailableDayDTO result = subscriptionAvailableDayService.save(subscriptionAvailableDayDTO);
        return ResponseEntity
            .created(new URI("/api/subscription-available-days/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /subscription-available-days/:id} : Updates an existing subscriptionAvailableDay.
     *
     * @param id the id of the subscriptionAvailableDayDTO to save.
     * @param subscriptionAvailableDayDTO the subscriptionAvailableDayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subscriptionAvailableDayDTO,
     * or with status {@code 400 (Bad Request)} if the subscriptionAvailableDayDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subscriptionAvailableDayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionAvailableDayDTO> updateSubscriptionAvailableDay(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubscriptionAvailableDayDTO subscriptionAvailableDayDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SubscriptionAvailableDay : {}, {}", id, subscriptionAvailableDayDTO);
        if (subscriptionAvailableDayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subscriptionAvailableDayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subscriptionAvailableDayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SubscriptionAvailableDayDTO result = subscriptionAvailableDayService.update(subscriptionAvailableDayDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscriptionAvailableDayDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /subscription-available-days/:id} : Partial updates given fields of an existing subscriptionAvailableDay, field will ignore if it is null
     *
     * @param id the id of the subscriptionAvailableDayDTO to save.
     * @param subscriptionAvailableDayDTO the subscriptionAvailableDayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subscriptionAvailableDayDTO,
     * or with status {@code 400 (Bad Request)} if the subscriptionAvailableDayDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subscriptionAvailableDayDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subscriptionAvailableDayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SubscriptionAvailableDayDTO> partialUpdateSubscriptionAvailableDay(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubscriptionAvailableDayDTO subscriptionAvailableDayDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SubscriptionAvailableDay partially : {}, {}", id, subscriptionAvailableDayDTO);
        if (subscriptionAvailableDayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subscriptionAvailableDayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subscriptionAvailableDayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubscriptionAvailableDayDTO> result = subscriptionAvailableDayService.partialUpdate(subscriptionAvailableDayDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscriptionAvailableDayDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /subscription-available-days} : get all the subscriptionAvailableDays.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subscriptionAvailableDays in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SubscriptionAvailableDayDTO>> getAllSubscriptionAvailableDays(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of SubscriptionAvailableDays");
        Page<SubscriptionAvailableDayDTO> page = subscriptionAvailableDayService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /subscription-available-days/:id} : get the "id" subscriptionAvailableDay.
     *
     * @param id the id of the subscriptionAvailableDayDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subscriptionAvailableDayDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionAvailableDayDTO> getSubscriptionAvailableDay(@PathVariable("id") Long id) {
        log.debug("REST request to get SubscriptionAvailableDay : {}", id);
        Optional<SubscriptionAvailableDayDTO> subscriptionAvailableDayDTO = subscriptionAvailableDayService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subscriptionAvailableDayDTO);
    }

    /**
     * {@code DELETE  /subscription-available-days/:id} : delete the "id" subscriptionAvailableDay.
     *
     * @param id the id of the subscriptionAvailableDayDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriptionAvailableDay(@PathVariable("id") Long id) {
        log.debug("REST request to delete SubscriptionAvailableDay : {}", id);
        subscriptionAvailableDayService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /subscription-available-days/_search?query=:query} : search for the subscriptionAvailableDay corresponding
     * to the query.
     *
     * @param query the query of the subscriptionAvailableDay search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<SubscriptionAvailableDayDTO>> searchSubscriptionAvailableDays(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of SubscriptionAvailableDays for query {}", query);
        try {
            Page<SubscriptionAvailableDayDTO> page = subscriptionAvailableDayService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
