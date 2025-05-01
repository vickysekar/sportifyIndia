package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.repository.FacilityEmployeeRepository;
import com.sportifyindia.app.service.FacilityEmployeeService;
import com.sportifyindia.app.service.dto.FacilityEmployeeDTO;
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
 * REST controller for managing {@link com.sportifyindia.app.domain.FacilityEmployee}.
 */
@RestController
@RequestMapping("/api/facility-employees")
public class FacilityEmployeeResource {

    private final Logger log = LoggerFactory.getLogger(FacilityEmployeeResource.class);

    private static final String ENTITY_NAME = "facilityEmployee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FacilityEmployeeService facilityEmployeeService;

    private final FacilityEmployeeRepository facilityEmployeeRepository;

    public FacilityEmployeeResource(
        FacilityEmployeeService facilityEmployeeService,
        FacilityEmployeeRepository facilityEmployeeRepository
    ) {
        this.facilityEmployeeService = facilityEmployeeService;
        this.facilityEmployeeRepository = facilityEmployeeRepository;
    }

    /**
     * {@code POST  /facility-employees} : Create a new facilityEmployee.
     *
     * @param facilityEmployeeDTO the facilityEmployeeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new facilityEmployeeDTO, or with status {@code 400 (Bad Request)} if the facilityEmployee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FacilityEmployeeDTO> createFacilityEmployee(@Valid @RequestBody FacilityEmployeeDTO facilityEmployeeDTO)
        throws URISyntaxException {
        log.debug("REST request to save FacilityEmployee : {}", facilityEmployeeDTO);
        if (facilityEmployeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new facilityEmployee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FacilityEmployeeDTO result = facilityEmployeeService.save(facilityEmployeeDTO);
        return ResponseEntity
            .created(new URI("/api/facility-employees/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /facility-employees/:id} : Updates an existing facilityEmployee.
     *
     * @param id the id of the facilityEmployeeDTO to save.
     * @param facilityEmployeeDTO the facilityEmployeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated facilityEmployeeDTO,
     * or with status {@code 400 (Bad Request)} if the facilityEmployeeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the facilityEmployeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FacilityEmployeeDTO> updateFacilityEmployee(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FacilityEmployeeDTO facilityEmployeeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FacilityEmployee : {}, {}", id, facilityEmployeeDTO);
        if (facilityEmployeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, facilityEmployeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facilityEmployeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FacilityEmployeeDTO result = facilityEmployeeService.update(facilityEmployeeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, facilityEmployeeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /facility-employees/:id} : Partial updates given fields of an existing facilityEmployee, field will ignore if it is null
     *
     * @param id the id of the facilityEmployeeDTO to save.
     * @param facilityEmployeeDTO the facilityEmployeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated facilityEmployeeDTO,
     * or with status {@code 400 (Bad Request)} if the facilityEmployeeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the facilityEmployeeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the facilityEmployeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FacilityEmployeeDTO> partialUpdateFacilityEmployee(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FacilityEmployeeDTO facilityEmployeeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FacilityEmployee partially : {}, {}", id, facilityEmployeeDTO);
        if (facilityEmployeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, facilityEmployeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!facilityEmployeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FacilityEmployeeDTO> result = facilityEmployeeService.partialUpdate(facilityEmployeeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, facilityEmployeeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /facility-employees} : get all the facilityEmployees.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of facilityEmployees in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FacilityEmployeeDTO>> getAllFacilityEmployees(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of FacilityEmployees");
        Page<FacilityEmployeeDTO> page = facilityEmployeeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /facility-employees/:id} : get the "id" facilityEmployee.
     *
     * @param id the id of the facilityEmployeeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the facilityEmployeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FacilityEmployeeDTO> getFacilityEmployee(@PathVariable("id") Long id) {
        log.debug("REST request to get FacilityEmployee : {}", id);
        Optional<FacilityEmployeeDTO> facilityEmployeeDTO = facilityEmployeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(facilityEmployeeDTO);
    }

    /**
     * {@code DELETE  /facility-employees/:id} : delete the "id" facilityEmployee.
     *
     * @param id the id of the facilityEmployeeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacilityEmployee(@PathVariable("id") Long id) {
        log.debug("REST request to delete FacilityEmployee : {}", id);
        facilityEmployeeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /facility-employees/_search?query=:query} : search for the facilityEmployee corresponding
     * to the query.
     *
     * @param query the query of the facilityEmployee search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<FacilityEmployeeDTO>> searchFacilityEmployees(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of FacilityEmployees for query {}", query);
        try {
            Page<FacilityEmployeeDTO> page = facilityEmployeeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
