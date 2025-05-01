package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.SaleLeadRepository;
import com.sportifyindia.app.service.SaleLeadService;
import com.sportifyindia.app.service.dto.SaleLeadDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.SaleLead}.
 */
@RestController
@RequestMapping("/api/sale-leads")
public class SaleLeadResource {

    private final Logger log = LoggerFactory.getLogger(SaleLeadResource.class);

    private static final String ENTITY_NAME = "saleLead";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SaleLeadService saleLeadService;

    private final SaleLeadRepository saleLeadRepository;

    public SaleLeadResource(SaleLeadService saleLeadService, SaleLeadRepository saleLeadRepository) {
        this.saleLeadService = saleLeadService;
        this.saleLeadRepository = saleLeadRepository;
    }

    /**
     * {@code POST  /sale-leads} : Create a new saleLead.
     *
     * @param saleLeadDTO the saleLeadDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new saleLeadDTO, or with status {@code 400 (Bad Request)} if the saleLead has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SaleLeadDTO> createSaleLead(@Valid @RequestBody SaleLeadDTO saleLeadDTO) throws URISyntaxException {
        log.debug("REST request to save SaleLead : {}", saleLeadDTO);
        if (saleLeadDTO.getId() != null) {
            throw new BadRequestAlertException("A new saleLead cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SaleLeadDTO result = saleLeadService.save(saleLeadDTO);
        return ResponseEntity
            .created(new URI("/api/sale-leads/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sale-leads/:id} : Updates an existing saleLead.
     *
     * @param id the id of the saleLeadDTO to save.
     * @param saleLeadDTO the saleLeadDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleLeadDTO,
     * or with status {@code 400 (Bad Request)} if the saleLeadDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the saleLeadDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SaleLeadDTO> updateSaleLead(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SaleLeadDTO saleLeadDTO
    ) throws URISyntaxException {
        log.debug("REST request to update SaleLead : {}, {}", id, saleLeadDTO);
        if (saleLeadDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleLeadDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleLeadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SaleLeadDTO result = saleLeadService.update(saleLeadDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleLeadDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sale-leads/:id} : Partial updates given fields of an existing saleLead, field will ignore if it is null
     *
     * @param id the id of the saleLeadDTO to save.
     * @param saleLeadDTO the saleLeadDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated saleLeadDTO,
     * or with status {@code 400 (Bad Request)} if the saleLeadDTO is not valid,
     * or with status {@code 404 (Not Found)} if the saleLeadDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the saleLeadDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SaleLeadDTO> partialUpdateSaleLead(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SaleLeadDTO saleLeadDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SaleLead partially : {}, {}", id, saleLeadDTO);
        if (saleLeadDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, saleLeadDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!saleLeadRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SaleLeadDTO> result = saleLeadService.partialUpdate(saleLeadDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, saleLeadDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sale-leads} : get all the saleLeads.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of saleLeads in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SaleLeadDTO>> getAllSaleLeads(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of SaleLeads");
        Page<SaleLeadDTO> page = saleLeadService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sale-leads/:id} : get the "id" saleLead.
     *
     * @param id the id of the saleLeadDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the saleLeadDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleLeadDTO> getSaleLead(@PathVariable("id") Long id) {
        log.debug("REST request to get SaleLead : {}", id);
        Optional<SaleLeadDTO> saleLeadDTO = saleLeadService.findOne(id);
        return ResponseUtil.wrapOrNotFound(saleLeadDTO);
    }

    /**
     * {@code DELETE  /sale-leads/:id} : delete the "id" saleLead.
     *
     * @param id the id of the saleLeadDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleLead(@PathVariable("id") Long id) {
        log.debug("REST request to delete SaleLead : {}", id);
        saleLeadService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /sale-leads/_search?query=:query} : search for the saleLead corresponding
     * to the query.
     *
     * @param query the query of the saleLead search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<SaleLeadDTO>> searchSaleLeads(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of SaleLeads for query {}", query);
        try {
            Page<SaleLeadDTO> page = saleLeadService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
