package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.domain.UtilityAvailableDays;
import com.sportifyindia.app.domain.UtilityExceptionDays;
import com.sportifyindia.app.domain.enumeration.DaysOfWeekEnum;
import com.sportifyindia.app.service.UtilityService;
import com.sportifyindia.app.service.dto.TimeSlotDTO;
import com.sportifyindia.app.service.dto.UtilityAvailableDaysDTO;
import com.sportifyindia.app.service.dto.UtilityDTO;
import com.sportifyindia.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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

/**
 * REST controller for managing {@link com.sportifyindia.app.domain.Utility}.
 */
@RestController
@RequestMapping("/api")
public class UtilityResource {

    private final Logger log = LoggerFactory.getLogger(UtilityResource.class);

    private static final String ENTITY_NAME = "utility";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UtilityService utilityService;

    public UtilityResource(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    /**
     * {@code POST  /utilities} : Create a new utility.
     *
     * @param utilityDTO the utilityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new utilityDTO, or with status {@code 400 (Bad Request)} if the utility has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/utilities")
    public ResponseEntity<UtilityDTO> createUtility(@Valid @RequestBody UtilityDTO utilityDTO) throws URISyntaxException {
        log.debug("REST request to save Utility : {}", utilityDTO);
        if (utilityDTO.getId() != null) {
            throw new BadRequestAlertException("A new utility cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UtilityDTO result = utilityService.save(utilityDTO);
        return ResponseEntity
            .created(new URI("/api/utilities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /utilities/:id} : Updates an existing utility.
     *
     * @param id the id of the utilityDTO to save.
     * @param utilityDTO the utilityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilityDTO,
     * or with status {@code 400 (Bad Request)} if the utilityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the utilityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/utilities/{id}")
    public ResponseEntity<UtilityDTO> updateUtility(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UtilityDTO utilityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Utility : {}, {}", id, utilityDTO);
        if (utilityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        UtilityDTO result = utilityService.update(utilityDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, utilityDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /utilities/:id} : get the "id" utility.
     *
     * @param id the id of the utilityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the utilityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/utilities/{id}")
    public ResponseEntity<UtilityDTO> getUtility(@PathVariable("id") Long id) {
        log.debug("REST request to get Utility : {}", id);
        return ResponseEntity.ok(utilityService.findOne(id).orElseThrow());
    }

    /**
     * {@code DELETE  /utilities/:id} : delete the "id" utility.
     *
     * @param id the id of the utilityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/utilities/{id}")
    public ResponseEntity<Void> deleteUtility(@PathVariable("id") Long id) {
        log.debug("REST request to delete Utility : {}", id);
        utilityService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /utilities/_search} : search for the utility corresponding
     * to the query.
     *
     * @param query the query of the utility search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/utilities/_search")
    public ResponseEntity<List<UtilityDTO>> searchUtilities(
        @RequestParam String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Utilities for query {}", query);
        Page<UtilityDTO> page = utilityService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/utilities/facility/{facilityId}")
    public ResponseEntity<List<UtilityDTO>> getUtilitiesByFacilityId(
        @PathVariable Long facilityId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Utilities by facility ID : {}", facilityId);
        Page<UtilityDTO> page = utilityService.findByFacilityId(facilityId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/utilities/{utilityId}/exception-days")
    public ResponseEntity<List<UtilityExceptionDays>> getExceptionDaysByUtilityId(
        @PathVariable Long utilityId,
        @RequestParam(required = false) Instant fromDate,
        @RequestParam(required = false) Instant toDate,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get exception days for Utility : {}", utilityId);
        Page<UtilityExceptionDays> page = utilityService.findExceptionDaysByUtilityIdAndDateRange(utilityId, fromDate, toDate, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/utilities/{utilityId}/exception-days")
    public ResponseEntity<UtilityExceptionDays> addExceptionDay(
        @PathVariable Long utilityId,
        @RequestBody UtilityExceptionDays exceptionDay
    ) {
        log.debug("REST request to add exception day for Utility : {}", utilityId);
        UtilityExceptionDays result = utilityService.addExceptionDay(utilityId, exceptionDay);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/utilities/{utilityId}/available-days")
    public ResponseEntity<List<UtilityAvailableDaysDTO>> getAvailableDays(@PathVariable Long utilityId) {
        log.debug("REST request to get available days for Utility : {}", utilityId);
        List<UtilityAvailableDays> availableDays = utilityService.getAvailableDaysByUtilityId(utilityId);

        // Group available days by day of week
        Map<DaysOfWeekEnum, List<UtilityAvailableDays>> groupedDays = availableDays
            .stream()
            .collect(Collectors.groupingBy(UtilityAvailableDays::getDaysOfWeek));

        List<UtilityAvailableDaysDTO> result = new ArrayList<>();
        for (Map.Entry<DaysOfWeekEnum, List<UtilityAvailableDays>> entry : groupedDays.entrySet()) {
            UtilityAvailableDaysDTO dto = new UtilityAvailableDaysDTO();
            dto.setDaysOfWeek(entry.getKey());

            List<TimeSlotDTO> timeSlots = entry
                .getValue()
                .stream()
                .map(day -> new TimeSlotDTO(day.getTimeSlots().getStartTime(), day.getTimeSlots().getEndTime()))
                .collect(Collectors.toList());

            dto.setTimeSlots(timeSlots);
            result.add(dto);
        }

        return ResponseEntity.ok().body(result);
    }

    @PutMapping("/utilities/{utilityId}/available-days")
    public ResponseEntity<List<UtilityAvailableDaysDTO>> updateAvailableDays(
        @PathVariable Long utilityId,
        @Valid @RequestBody List<UtilityAvailableDaysDTO> availableDaysDTOs
    ) {
        log.debug("REST request to update available days for Utility : {}", utilityId);
        List<UtilityAvailableDays> updatedDays = utilityService.updateAvailableDays(utilityId, availableDaysDTOs);

        // Convert back to DTO format
        Map<DaysOfWeekEnum, List<UtilityAvailableDays>> groupedDays = updatedDays
            .stream()
            .collect(Collectors.groupingBy(UtilityAvailableDays::getDaysOfWeek));

        List<UtilityAvailableDaysDTO> result = new ArrayList<>();
        for (Map.Entry<DaysOfWeekEnum, List<UtilityAvailableDays>> entry : groupedDays.entrySet()) {
            UtilityAvailableDaysDTO dto = new UtilityAvailableDaysDTO();
            dto.setDaysOfWeek(entry.getKey());

            List<TimeSlotDTO> timeSlots = entry
                .getValue()
                .stream()
                .map(day -> new TimeSlotDTO(day.getTimeSlots().getStartTime(), day.getTimeSlots().getEndTime()))
                .collect(Collectors.toList());

            dto.setTimeSlots(timeSlots);
            result.add(dto);
        }

        return ResponseEntity.ok().body(result);
    }
}
