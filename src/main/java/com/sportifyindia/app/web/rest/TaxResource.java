package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.TaxRepository;
import com.sportifyindia.app.service.TaxService;
import com.sportifyindia.app.service.dto.TaxDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.Tax}.
 */
@RestController
@RequestMapping("/api/taxes")
public class TaxResource {

    private final Logger log = LoggerFactory.getLogger(TaxResource.class);

    private static final String ENTITY_NAME = "tax";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaxService taxService;

    private final TaxRepository taxRepository;

    public TaxResource(TaxService taxService, TaxRepository taxRepository) {
        this.taxService = taxService;
        this.taxRepository = taxRepository;
    }

    /**
     * {@code POST  /taxes} : Create a new tax.
     *
     * @param taxDTO the taxDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taxDTO, or with status {@code 400 (Bad Request)} if the tax has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TaxDTO> createTax(@Valid @RequestBody TaxDTO taxDTO) throws URISyntaxException {
        log.debug("REST request to save Tax : {}", taxDTO);
        if (taxDTO.getId() != null) {
            throw new BadRequestAlertException("A new tax cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaxDTO result = taxService.save(taxDTO);
        return ResponseEntity
            .created(new URI("/api/taxes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /taxes/:id} : Updates an existing tax.
     *
     * @param id the id of the taxDTO to save.
     * @param taxDTO the taxDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxDTO,
     * or with status {@code 400 (Bad Request)} if the taxDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taxDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaxDTO> updateTax(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody TaxDTO taxDTO)
        throws URISyntaxException {
        log.debug("REST request to update Tax : {}, {}", id, taxDTO);
        if (taxDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taxRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TaxDTO result = taxService.update(taxDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taxDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /taxes/:id} : Partial updates given fields of an existing tax, field will ignore if it is null
     *
     * @param id the id of the taxDTO to save.
     * @param taxDTO the taxDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taxDTO,
     * or with status {@code 400 (Bad Request)} if the taxDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taxDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taxDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaxDTO> partialUpdateTax(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaxDTO taxDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Tax partially : {}, {}", id, taxDTO);
        if (taxDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taxDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taxRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaxDTO> result = taxService.partialUpdate(taxDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taxDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /taxes} : get all the taxes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taxes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TaxDTO>> getAllTaxes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Taxes");
        Page<TaxDTO> page = taxService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /taxes/:id} : get the "id" tax.
     *
     * @param id the id of the taxDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taxDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaxDTO> getTax(@PathVariable("id") Long id) {
        log.debug("REST request to get Tax : {}", id);
        Optional<TaxDTO> taxDTO = taxService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taxDTO);
    }

    /**
     * {@code DELETE  /taxes/:id} : delete the "id" tax.
     *
     * @param id the id of the taxDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTax(@PathVariable("id") Long id) {
        log.debug("REST request to delete Tax : {}", id);
        taxService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /taxes/_search?query=:query} : search for the tax corresponding
     * to the query.
     *
     * @param query the query of the tax search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<TaxDTO>> searchTaxes(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Taxes for query {}", query);
        try {
            Page<TaxDTO> page = taxService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
