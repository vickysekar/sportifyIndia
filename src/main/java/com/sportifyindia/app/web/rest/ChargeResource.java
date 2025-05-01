package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.ChargeRepository;
import com.sportifyindia.app.service.ChargeService;
import com.sportifyindia.app.service.dto.ChargeDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.Charge}.
 */
@RestController
@RequestMapping("/api/charges")
public class ChargeResource {

    private final Logger log = LoggerFactory.getLogger(ChargeResource.class);

    private static final String ENTITY_NAME = "charge";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChargeService chargeService;

    private final ChargeRepository chargeRepository;

    public ChargeResource(ChargeService chargeService, ChargeRepository chargeRepository) {
        this.chargeService = chargeService;
        this.chargeRepository = chargeRepository;
    }

    /**
     * {@code POST  /charges} : Create a new charge.
     *
     * @param chargeDTO the chargeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chargeDTO, or with status {@code 400 (Bad Request)} if the charge has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChargeDTO> createCharge(@Valid @RequestBody ChargeDTO chargeDTO) throws URISyntaxException {
        log.debug("REST request to save Charge : {}", chargeDTO);
        if (chargeDTO.getId() != null) {
            throw new BadRequestAlertException("A new charge cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ChargeDTO result = chargeService.save(chargeDTO);
        return ResponseEntity
            .created(new URI("/api/charges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /charges/:id} : Updates an existing charge.
     *
     * @param id the id of the chargeDTO to save.
     * @param chargeDTO the chargeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chargeDTO,
     * or with status {@code 400 (Bad Request)} if the chargeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chargeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChargeDTO> updateCharge(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ChargeDTO chargeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Charge : {}, {}", id, chargeDTO);
        if (chargeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chargeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chargeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ChargeDTO result = chargeService.update(chargeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, chargeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /charges/:id} : Partial updates given fields of an existing charge, field will ignore if it is null
     *
     * @param id the id of the chargeDTO to save.
     * @param chargeDTO the chargeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chargeDTO,
     * or with status {@code 400 (Bad Request)} if the chargeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the chargeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the chargeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChargeDTO> partialUpdateCharge(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ChargeDTO chargeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Charge partially : {}, {}", id, chargeDTO);
        if (chargeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chargeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chargeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChargeDTO> result = chargeService.partialUpdate(chargeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, chargeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /charges} : get all the charges.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of charges in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ChargeDTO>> getAllCharges(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Charges");
        Page<ChargeDTO> page = chargeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /charges/:id} : get the "id" charge.
     *
     * @param id the id of the chargeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chargeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChargeDTO> getCharge(@PathVariable("id") Long id) {
        log.debug("REST request to get Charge : {}", id);
        Optional<ChargeDTO> chargeDTO = chargeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chargeDTO);
    }

    /**
     * {@code DELETE  /charges/:id} : delete the "id" charge.
     *
     * @param id the id of the chargeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharge(@PathVariable("id") Long id) {
        log.debug("REST request to delete Charge : {}", id);
        chargeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /charges/_search?query=:query} : search for the charge corresponding
     * to the query.
     *
     * @param query the query of the charge search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ChargeDTO>> searchCharges(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Charges for query {}", query);
        try {
            Page<ChargeDTO> page = chargeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
