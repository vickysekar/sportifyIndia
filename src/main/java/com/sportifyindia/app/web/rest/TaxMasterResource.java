package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.TaxMasterRepository;
import com.sportifyindia.app.service.TaxMasterService;
import com.sportifyindia.app.service.dto.TaxMasterDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.TaxMaster}.
 */
@RestController
@RequestMapping("/api/tax-masters")
public class TaxMasterResource {

    private final Logger log = LoggerFactory.getLogger(TaxMasterResource.class);

    private static final String ENTITY_NAME = "taxMaster";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaxMasterService taxMasterService;

    private final TaxMasterRepository taxMasterRepository;

    public TaxMasterResource(TaxMasterService taxMasterService, TaxMasterRepository taxMasterRepository) {
        this.taxMasterService = taxMasterService;
        this.taxMasterRepository = taxMasterRepository;
    }

    /**
     * {@code POST  /tax-masters} : Create a new taxMaster.
     *
     * @param taxMasterDTO the taxMasterDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taxMasterDTO, or with status {@code 400 (Bad Request)} if the taxMaster has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TaxMasterDTO> createTaxMaster(@Valid @RequestBody TaxMasterDTO taxMasterDTO) throws URISyntaxException {
        log.debug("REST request to save TaxMaster : {}", taxMasterDTO);
        if (taxMasterDTO.getId() != null) {
            throw new BadRequestAlertException("A new taxMaster cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaxMasterDTO result = taxMasterService.save(taxMasterDTO);
        return ResponseEntity
            .created(new URI("/api/tax-masters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tax-masters/:id} : Updates an existing taxMaster.
     *
     * @param id the id of the taxMasterDTO to save.
     * @param taxMasterDTO the taxMasterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxMasterDTO,
     * or with status {@code 400 (Bad Request)} if the taxMasterDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taxMasterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaxMasterDTO> updateTaxMaster(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaxMasterDTO taxMasterDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TaxMaster : {}, {}", id, taxMasterDTO);
        if (taxMasterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxMasterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taxMasterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TaxMasterDTO result = taxMasterService.update(taxMasterDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taxMasterDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tax-masters/:id} : Partial updates given fields of an existing taxMaster, field will ignore if it is null
     *
     * @param id the id of the taxMasterDTO to save.
     * @param taxMasterDTO the taxMasterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxMasterDTO,
     * or with status {@code 400 (Bad Request)} if the taxMasterDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taxMasterDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taxMasterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaxMasterDTO> partialUpdateTaxMaster(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaxMasterDTO taxMasterDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TaxMaster partially : {}, {}", id, taxMasterDTO);
        if (taxMasterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxMasterDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taxMasterRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaxMasterDTO> result = taxMasterService.partialUpdate(taxMasterDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taxMasterDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tax-masters} : get all the taxMasters.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taxMasters in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TaxMasterDTO>> getAllTaxMasters(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of TaxMasters");
        Page<TaxMasterDTO> page = taxMasterService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tax-masters/:id} : get the "id" taxMaster.
     *
     * @param id the id of the taxMasterDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taxMasterDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaxMasterDTO> getTaxMaster(@PathVariable("id") Long id) {
        log.debug("REST request to get TaxMaster : {}", id);
        Optional<TaxMasterDTO> taxMasterDTO = taxMasterService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taxMasterDTO);
    }

    /**
     * {@code DELETE  /tax-masters/:id} : delete the "id" taxMaster.
     *
     * @param id the id of the taxMasterDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaxMaster(@PathVariable("id") Long id) {
        log.debug("REST request to delete TaxMaster : {}", id);
        taxMasterService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /tax-masters/_search?query=:query} : search for the taxMaster corresponding
     * to the query.
     *
     * @param query the query of the taxMaster search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<TaxMasterDTO>> searchTaxMasters(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TaxMasters for query {}", query);
        try {
            Page<TaxMasterDTO> page = taxMasterService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
