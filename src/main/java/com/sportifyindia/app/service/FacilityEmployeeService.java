package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Authority;
import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.FacilityEmployee;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.domain.enumeration.EmployeeRoleEnum;
import com.sportifyindia.app.domain.enumeration.EmployeeStatusEnum;
import com.sportifyindia.app.repository.FacilityEmployeeRepository;
import com.sportifyindia.app.repository.FacilityRepository;
import com.sportifyindia.app.security.AuthoritiesConstants;
import com.sportifyindia.app.service.dto.AdminUserDTO;
import com.sportifyindia.app.service.dto.FacilityEmployeeDTO;
import com.sportifyindia.app.service.mapper.FacilityEmployeeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.FacilityEmployee}.
 */
@Service
@Transactional
public class FacilityEmployeeService {

    private final Logger log = LoggerFactory.getLogger(FacilityEmployeeService.class);

    private final FacilityEmployeeRepository facilityEmployeeRepository;
    private final FacilityRepository facilityRepository;
    private final UserService userService;
    private final FacilityEmployeeMapper facilityEmployeeMapper;

    public FacilityEmployeeService(
        FacilityEmployeeRepository facilityEmployeeRepository,
        FacilityRepository facilityRepository,
        UserService userService,
        FacilityEmployeeMapper facilityEmployeeMapper
    ) {
        this.facilityEmployeeRepository = facilityEmployeeRepository;
        this.facilityRepository = facilityRepository;
        this.userService = userService;
        this.facilityEmployeeMapper = facilityEmployeeMapper;
    }

    /**
     * Add an employee to a facility.
     * Only facility owners can add admins.
     * Both owners and admins can add other employees.
     *
     * @param facilityId the ID of the facility
     * @param userId the ID of the user to add as employee
     * @param role the role of the employee
     * @param position the position of the employee
     * @return the created FacilityEmployee
     */
    @Transactional
    public FacilityEmployee addEmployeeToFacility(Long facilityId, Long userId, EmployeeRoleEnum role, String position) {
        log.debug(
            "Request to add employee to facility: facilityId={}, userId={}, role={}, position={}",
            facilityId,
            userId,
            role,
            position
        );

        // Get current user
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            log.error("No authenticated user found");
            throw new RuntimeException("User not found");
        }

        // Get facility
        Optional<Facility> facility = facilityRepository.findById(facilityId);
        if (facility.isEmpty()) {
            log.error("Facility not found: {}", facilityId);
            throw new RuntimeException("Facility not found");
        }

        // Check if user exists
        Optional<User> employeeUser = userService.findOne(userId);
        if (employeeUser.isEmpty()) {
            log.error("Employee user not found: {}", userId);
            throw new RuntimeException("Employee user not found");
        }

        // Check authorization
        User user = currentUser.get();
        boolean isOwner = user.getId().equals(facility.get().getUser().getId());
        boolean isAdmin = facilityEmployeeRepository.existsByFacilityIdAndUserIdAndRoleAndStatus(
            facilityId,
            user.getId(),
            EmployeeRoleEnum.FACILITY_ADMIN,
            EmployeeStatusEnum.ACTIVE
        );

        // Only owners can add admins
        if (role == EmployeeRoleEnum.FACILITY_ADMIN && !isOwner) {
            log.error("Only facility owner can add admins. User: {}", user.getLogin());
            throw new RuntimeException("Only facility owner can add admins");
        }

        // Only owners and admins can add employees
        if (!isOwner && !isAdmin) {
            log.error("User {} is not authorized to add employees to facility {}", user.getLogin(), facilityId);
            throw new RuntimeException("Not authorized to add employees");
        }

        // Check if employee already exists
        if (facilityEmployeeRepository.existsByFacilityIdAndUserIdAndStatus(facilityId, userId, EmployeeStatusEnum.ACTIVE)) {
            log.error("Employee already exists for facility {} and user {}", facilityId, userId);
            throw new RuntimeException("Employee already exists");
        }

        // Update user's role based on position
        User employee = employeeUser.get();
        String newRole =
            switch (role) {
                case FACILITY_ADMIN -> AuthoritiesConstants.FACILITY_ADMIN;
                case TRAINER -> AuthoritiesConstants.TRAINER;
                case SALES_PERSON -> AuthoritiesConstants.SALES_PERSON;
                default -> throw new RuntimeException("Invalid employee role");
            };

        // Add the new role to user's authorities
        Authority authority = new Authority();
        authority.setName(newRole);
        employee.getAuthorities().add(authority);

        // Convert User to AdminUserDTO for update
        AdminUserDTO adminUserDTO = new AdminUserDTO(employee);
        userService.updateUser(adminUserDTO);

        // Create new facility employee
        FacilityEmployee facilityEmployee = new FacilityEmployee()
            .facility(facility.get())
            .user(employee)
            .role(role)
            .position(position)
            .status(EmployeeStatusEnum.ACTIVE);

        return facilityEmployeeRepository.save(facilityEmployee);
    }

    /**
     * Get all employees for a facility.
     *
     * @param facilityId the ID of the facility
     * @param pageable the pagination information
     * @return the page of employees
     */
    @Transactional(readOnly = true)
    public Page<FacilityEmployeeDTO> findAllEmployeesForFacility(Long facilityId, Pageable pageable) {
        log.debug("Request to get all employees for facility: {}", facilityId);
        return facilityEmployeeRepository.findByFacilityId(facilityId, pageable).map(facilityEmployeeMapper::toDto);
    }

    /**
     * Get an employee by ID for a specific facility.
     *
     * @param facilityId the ID of the facility
     * @param id the ID of the employee
     * @return the employee if found
     */
    @Transactional(readOnly = true)
    public Optional<FacilityEmployeeDTO> findEmployeeById(Long facilityId, Long id) {
        log.debug("Request to get employee: {} for facility: {}", id, facilityId);
        return facilityEmployeeRepository.findByIdAndFacilityId(id, facilityId).map(facilityEmployeeMapper::toDto);
    }

    /**
     * Remove an employee from a facility.
     * Only facility owners and admins can remove employees.
     * Owners can remove any employee, while admins can only remove non-admin employees.
     *
     * @param facilityId the ID of the facility
     * @param employeeId the ID of the employee to remove
     */
    @Transactional
    public void removeEmployeeFromFacility(Long facilityId, Long employeeId) {
        log.debug("Request to remove employee from facility: facilityId={}, employeeId={}", facilityId, employeeId);

        // Get current user
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            log.error("No authenticated user found");
            throw new RuntimeException("User not found");
        }

        // Get facility
        Optional<Facility> facility = facilityRepository.findById(facilityId);
        if (facility.isEmpty()) {
            log.error("Facility not found: {}", facilityId);
            throw new RuntimeException("Facility not found");
        }

        // Get employee
        Optional<FacilityEmployee> employee = facilityEmployeeRepository.findByIdAndFacilityId(employeeId, facilityId);
        if (employee.isEmpty()) {
            log.error("Employee not found: {} for facility: {}", employeeId, facilityId);
            throw new RuntimeException("Employee not found");
        }

        // Check authorization
        User user = currentUser.get();
        boolean isOwner = user.getId().equals(facility.get().getUser().getId());
        boolean isAdmin = facilityEmployeeRepository.existsByFacilityIdAndUserIdAndRoleAndStatus(
            facilityId,
            user.getId(),
            EmployeeRoleEnum.FACILITY_ADMIN,
            EmployeeStatusEnum.ACTIVE
        );

        // Only owners can remove admins
        if (employee.get().getRole() == EmployeeRoleEnum.FACILITY_ADMIN && !isOwner) {
            log.error("Only facility owner can remove admins. User: {}", user.getLogin());
            throw new RuntimeException("Only facility owner can remove admins");
        }

        // Only owners and admins can remove employees
        if (!isOwner && !isAdmin) {
            log.error("User {} is not authorized to remove employees from facility {}", user.getLogin(), facilityId);
            throw new RuntimeException("Not authorized to remove employees");
        }

        // Remove the employee role from user's authorities
        User employeeUser = employee.get().getUser();
        String roleToRemove =
            switch (employee.get().getRole()) {
                case FACILITY_ADMIN -> AuthoritiesConstants.FACILITY_ADMIN;
                case TRAINER -> AuthoritiesConstants.TRAINER;
                case SALES_PERSON -> AuthoritiesConstants.SALES_PERSON;
                default -> throw new RuntimeException("Invalid employee role");
            };

        employeeUser.getAuthorities().removeIf(auth -> auth.getName().equals(roleToRemove));

        // Convert User to AdminUserDTO for update
        AdminUserDTO adminUserDTO = new AdminUserDTO(employeeUser);
        userService.updateUser(adminUserDTO);

        // Delete the employee
        facilityEmployeeRepository.deleteById(employeeId);
    }

    /**
     * Update an employee's details in a facility.
     * Only facility owners and admins can update employees.
     * Owners can update any employee, while admins can only update non-admin employees.
     *
     * @param facilityId the ID of the facility
     * @param employeeId the ID of the employee to update
     * @param role the new role of the employee
     * @param position the new position of the employee
     * @return the updated employee
     */
    @Transactional
    public FacilityEmployee updateEmployee(Long facilityId, Long employeeId, EmployeeRoleEnum role, String position) {
        log.debug("Request to update employee: facilityId={}, employeeId={}, role={}, position={}", facilityId, employeeId, role, position);

        // Get current user
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            log.error("No authenticated user found");
            throw new RuntimeException("User not found");
        }

        // Get facility
        Optional<Facility> facility = facilityRepository.findById(facilityId);
        if (facility.isEmpty()) {
            log.error("Facility not found: {}", facilityId);
            throw new RuntimeException("Facility not found");
        }

        // Get employee
        Optional<FacilityEmployee> existingEmployee = facilityEmployeeRepository.findByIdAndFacilityId(employeeId, facilityId);
        if (existingEmployee.isEmpty()) {
            log.error("Employee not found: {} for facility: {}", employeeId, facilityId);
            throw new RuntimeException("Employee not found");
        }

        // Check authorization
        User user = currentUser.get();
        boolean isOwner = user.getId().equals(facility.get().getUser().getId());
        boolean isAdmin = facilityEmployeeRepository.existsByFacilityIdAndUserIdAndRoleAndStatus(
            facilityId,
            user.getId(),
            EmployeeRoleEnum.FACILITY_ADMIN,
            EmployeeStatusEnum.ACTIVE
        );

        // Only owners can update admins
        if (existingEmployee.get().getRole() == EmployeeRoleEnum.FACILITY_ADMIN && !isOwner) {
            log.error("Only facility owner can update admins. User: {}", user.getLogin());
            throw new RuntimeException("Only facility owner can update admins");
        }

        // Only owners and admins can update employees
        if (!isOwner && !isAdmin) {
            log.error("User {} is not authorized to update employees in facility {}", user.getLogin(), facilityId);
            throw new RuntimeException("Not authorized to update employees");
        }

        // Update user's role if it has changed
        if (existingEmployee.get().getRole() != role) {
            User employeeUser = existingEmployee.get().getUser();

            // Remove old role
            String oldRole =
                switch (existingEmployee.get().getRole()) {
                    case FACILITY_ADMIN -> AuthoritiesConstants.FACILITY_ADMIN;
                    case TRAINER -> AuthoritiesConstants.TRAINER;
                    case SALES_PERSON -> AuthoritiesConstants.SALES_PERSON;
                    default -> throw new RuntimeException("Invalid employee role");
                };
            employeeUser.getAuthorities().removeIf(auth -> auth.getName().equals(oldRole));

            // Add new role
            String newRole =
                switch (role) {
                    case FACILITY_ADMIN -> AuthoritiesConstants.FACILITY_ADMIN;
                    case TRAINER -> AuthoritiesConstants.TRAINER;
                    case SALES_PERSON -> AuthoritiesConstants.SALES_PERSON;
                    default -> throw new RuntimeException("Invalid employee role");
                };
            Authority authority = new Authority();
            authority.setName(newRole);
            employeeUser.getAuthorities().add(authority);

            // Update user
            AdminUserDTO adminUserDTO = new AdminUserDTO(employeeUser);
            userService.updateUser(adminUserDTO);
        }

        // Update employee details
        FacilityEmployee employee = existingEmployee.get();
        employee.setRole(role);
        employee.setPosition(position);

        return facilityEmployeeRepository.save(employee);
    }
}
