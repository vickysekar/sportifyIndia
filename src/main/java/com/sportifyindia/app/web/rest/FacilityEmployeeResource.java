package com.sportifyindia.app.web.rest;

import com.sportifyindia.app.domain.FacilityEmployee;
import com.sportifyindia.app.domain.enumeration.EmployeeRoleEnum;
import com.sportifyindia.app.service.FacilityEmployeeService;
import com.sportifyindia.app.service.dto.FacilityEmployeeDTO;
import com.sportifyindia.app.service.mapper.FacilityEmployeeMapper;
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
 * REST controller for managing facility employees.
 */
@RestController
@RequestMapping("/api")
public class FacilityEmployeeResource {

    private final Logger log = LoggerFactory.getLogger(FacilityEmployeeResource.class);

    private static final String ENTITY_NAME = "facilityEmployee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FacilityEmployeeService facilityEmployeeService;
    private final FacilityEmployeeMapper facilityEmployeeMapper;

    public FacilityEmployeeResource(FacilityEmployeeService facilityEmployeeService, FacilityEmployeeMapper facilityEmployeeMapper) {
        this.facilityEmployeeService = facilityEmployeeService;
        this.facilityEmployeeMapper = facilityEmployeeMapper;
    }

    /**
     * {@code POST  /facilities/{facilityId}/employees} : Add a new employee to a facility.
     *
     * @param facilityId the ID of the facility
     * @param request the request containing employee details
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new facility employee,
     * or with status {@code 400 (Bad Request)} if the employee already exists or if the request is invalid.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/facilities/{facilityId}/employees")
    public ResponseEntity<FacilityEmployeeDTO> addEmployeeToFacility(
        @PathVariable Long facilityId,
        @Valid @RequestBody AddEmployeeRequest request
    ) throws URISyntaxException {
        log.debug("REST request to add employee to facility : {} with request: {}", facilityId, request);

        try {
            FacilityEmployee employee = facilityEmployeeService.addEmployeeToFacility(
                facilityId,
                request.getUserId(),
                request.getRole(),
                request.getPosition()
            );

            FacilityEmployeeDTO result = facilityEmployeeMapper.toDto(employee);
            return ResponseEntity
                .created(new URI("/api/facilities/" + facilityId + "/employees/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert(applicationName, true, ENTITY_NAME, "error", e.getMessage()))
                .build();
        }
    }

    /**
     * {@code GET  /facilities/{facilityId}/employees} : Get all employees for a facility.
     *
     * @param facilityId the ID of the facility
     * @param pageable the pagination information
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of employees in body.
     */
    @GetMapping("/facilities/{facilityId}/employees")
    public ResponseEntity<List<FacilityEmployeeDTO>> getAllEmployeesForFacility(
        @PathVariable Long facilityId,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get all employees for facility : {}", facilityId);
        Page<FacilityEmployeeDTO> page = facilityEmployeeService.findAllEmployeesForFacility(facilityId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /facilities/{facilityId}/employees/{id}} : Get an employee by ID.
     *
     * @param facilityId the ID of the facility
     * @param id the ID of the employee
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employee,
     * or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/facilities/{facilityId}/employees/{id}")
    public ResponseEntity<FacilityEmployeeDTO> getEmployee(@PathVariable Long facilityId, @PathVariable Long id) {
        log.debug("REST request to get employee : {} for facility : {}", id, facilityId);
        Optional<FacilityEmployeeDTO> employee = facilityEmployeeService.findEmployeeById(facilityId, id);
        return ResponseUtil.wrapOrNotFound(employee);
    }

    /**
     * {@code DELETE  /facilities/{facilityId}/employees/{id}} : Remove an employee from a facility.
     *
     * @param facilityId the ID of the facility
     * @param id the ID of the employee to remove
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)} on success,
     * or with status {@code 400 (Bad Request)} if the employee cannot be removed.
     */
    @DeleteMapping("/facilities/{facilityId}/employees/{id}")
    public ResponseEntity<Void> removeEmployeeFromFacility(@PathVariable Long facilityId, @PathVariable Long id) {
        log.debug("REST request to remove employee : {} from facility : {}", id, facilityId);
        try {
            facilityEmployeeService.removeEmployeeFromFacility(facilityId, id);
            return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
        } catch (RuntimeException e) {
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert(applicationName, true, ENTITY_NAME, "error", e.getMessage()))
                .build();
        }
    }

    /**
     * {@code PUT  /facilities/{facilityId}/employees/{id}} : Update an employee's details.
     *
     * @param facilityId the ID of the facility
     * @param id the ID of the employee to update
     * @param request the request containing the updated employee details
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated employee,
     * or with status {@code 400 (Bad Request)} if the employee cannot be updated.
     */
    @PutMapping("/facilities/{facilityId}/employees/{id}")
    public ResponseEntity<FacilityEmployeeDTO> updateEmployee(
        @PathVariable Long facilityId,
        @PathVariable Long id,
        @Valid @RequestBody UpdateEmployeeRequest request
    ) {
        log.debug("REST request to update employee : {} for facility : {} with request: {}", id, facilityId, request);
        try {
            FacilityEmployee employee = facilityEmployeeService.updateEmployee(facilityId, id, request.getRole(), request.getPosition());
            FacilityEmployeeDTO result = facilityEmployeeMapper.toDto(employee);
            return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .body(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert(applicationName, true, ENTITY_NAME, "error", e.getMessage()))
                .build();
        }
    }

    /**
     * Request class for adding an employee to a facility.
     */
    public static class AddEmployeeRequest {

        @NotNull
        private Long userId;

        @NotNull
        private EmployeeRoleEnum role;

        @NotNull
        private String position;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public EmployeeRoleEnum getRole() {
            return role;
        }

        public void setRole(EmployeeRoleEnum role) {
            this.role = role;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }
    }

    /**
     * Request class for updating an employee.
     */
    public static class UpdateEmployeeRequest {

        @NotNull
        private EmployeeRoleEnum role;

        @NotNull
        private String position;

        public EmployeeRoleEnum getRole() {
            return role;
        }

        public void setRole(EmployeeRoleEnum role) {
            this.role = role;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }
    }
}
