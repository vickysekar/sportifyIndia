package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.CourseSubscription;
import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.domain.enumeration.EmployeeRoleEnum;
import com.sportifyindia.app.domain.enumeration.EmployeeStatusEnum;
import com.sportifyindia.app.repository.CourseSubscriptionRepository;
import com.sportifyindia.app.repository.FacilityRepository;
import com.sportifyindia.app.repository.search.FacilitySearchRepository;
import com.sportifyindia.app.security.AuthoritiesConstants;
import com.sportifyindia.app.security.SecurityUtils;
import com.sportifyindia.app.service.dto.FacilityDTO;
import com.sportifyindia.app.service.mapper.FacilityMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.Facility}.
 */
@Service
@Transactional
public class FacilityService {

    private final Logger log = LoggerFactory.getLogger(FacilityService.class);

    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;
    private final FacilitySearchRepository facilitySearchRepository;
    private final UserService userService;
    private final CourseSubscriptionRepository courseSubscriptionRepository;

    public FacilityService(
        FacilityRepository facilityRepository,
        FacilityMapper facilityMapper,
        FacilitySearchRepository facilitySearchRepository,
        UserService userService,
        CourseSubscriptionRepository courseSubscriptionRepository
    ) {
        this.facilityRepository = facilityRepository;
        this.facilityMapper = facilityMapper;
        this.facilitySearchRepository = facilitySearchRepository;
        this.userService = userService;
        this.courseSubscriptionRepository = courseSubscriptionRepository;
    }

    /**
     * Save a facility.
     *
     * @param facilityDTO the entity to save.
     * @return the persisted entity.
     */
    public FacilityDTO save(FacilityDTO facilityDTO) {
        log.debug("Request to save Facility : {}", facilityDTO);
        Facility facility = facilityMapper.toEntity(facilityDTO);

        // Get current user
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Check if user is a facility owner
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.FACILITY_OWNER)) {
            throw new RuntimeException("Only facility owners can create facilities");
        }

        // Set the owner
        facility.setUser(currentUser.get());
        facility = facilityRepository.save(facility);
        FacilityDTO result = facilityMapper.toDto(facility);
        facilitySearchRepository.index(facility);
        return result;
    }

    /**
     * Update a facility.
     *
     * @param facilityDTO the entity to save.
     * @return the persisted entity.
     */
    public FacilityDTO update(FacilityDTO facilityDTO) {
        log.debug("Request to update Facility : {}", facilityDTO);

        // Get current user
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Check if user is a facility owner
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.FACILITY_OWNER)) {
            throw new RuntimeException("Only facility owners can update facilities");
        }

        // Check if user is the owner of this facility
        Optional<Facility> existingFacility = facilityRepository.findById(facilityDTO.getId());
        if (existingFacility.isEmpty()) {
            throw new RuntimeException("Facility not found");
        }
        if (!existingFacility.get().getUser().equals(currentUser.get())) {
            throw new RuntimeException("Only the facility owner can update the facility");
        }

        Facility facility = facilityMapper.toEntity(facilityDTO);
        facility.setUser(currentUser.get()); // Ensure the owner remains the same
        facility = facilityRepository.save(facility);
        FacilityDTO result = facilityMapper.toDto(facility);
        facilitySearchRepository.index(facility);
        return result;
    }

    /**
     * Partially update a facility.
     *
     * @param facilityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FacilityDTO> partialUpdate(FacilityDTO facilityDTO) {
        log.debug("Request to partially update Facility : {}", facilityDTO);

        // Get current user
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Check if user is a facility owner
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.FACILITY_OWNER)) {
            throw new RuntimeException("Only facility owners can update facilities");
        }

        return facilityRepository
            .findById(facilityDTO.getId())
            .map(existingFacility -> {
                // Check if user is the owner of this facility
                if (!existingFacility.getUser().equals(currentUser.get())) {
                    throw new RuntimeException("Only the facility owner can update the facility");
                }

                facilityMapper.partialUpdate(existingFacility, facilityDTO);
                existingFacility.setUser(currentUser.get()); // Ensure the owner remains the same
                return existingFacility;
            })
            .map(facilityRepository::save)
            .map(savedFacility -> {
                facilitySearchRepository.index(savedFacility);
                return savedFacility;
            })
            .map(facilityMapper::toDto);
    }

    /**
     * Get all facilities based on user role and subscriptions.
     * For ROLE_USER: returns only facilities where user has active course subscriptions
     * For ROLE_FACILITY_OWNER: returns only the facility where user is the owner
     * For ROLE_FACILITY_ADMIN: returns only the facility where user is an admin
     * For ROLE_TRAINER: returns only the facility where user is a trainer
     * For ROLE_SALES_PERSON: returns only the facility where user is a sales person
     * For other roles: returns all facilities
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FacilityDTO> findAllForCurrentUser(Pageable pageable) {
        log.debug("Request to get all Facilities for current user");
        Optional<User> currentUser = userService.getUserWithAuthorities();

        if (currentUser.isEmpty()) {
            log.error("No authenticated user found");
            throw new RuntimeException("User not found");
        }

        User user = currentUser.get();
        String username = user.getLogin();
        List<String> roles = user.getAuthorities().stream().map(auth -> auth.getName()).collect(Collectors.toList());
        log.debug("Processing request for user: {} with roles: {}", username, roles);

        // Check if user is a facility owner
        if (roles.contains("ROLE_FACILITY_OWNER")) {
            log.debug("User {} is a facility owner, returning owned facilities", username);
            return facilityRepository.findByUserIsCurrentUser(pageable).map(facilityMapper::toDto);
        }

        // Check if user is a facility admin
        if (roles.contains("ROLE_FACILITY_ADMIN")) {
            log.debug("User {} is a facility admin, returning managed facilities", username);
            return facilityRepository
                .findByEmployeeRole(user.getId(), EmployeeRoleEnum.FACILITY_ADMIN, EmployeeStatusEnum.ACTIVE, pageable)
                .map(facilityMapper::toDto);
        }

        // Check if user is a trainer
        if (roles.contains("ROLE_TRAINER")) {
            log.debug("User {} is a trainer, returning associated facilities", username);
            return facilityRepository
                .findByEmployeeRole(user.getId(), EmployeeRoleEnum.TRAINER, EmployeeStatusEnum.ACTIVE, pageable)
                .map(facilityMapper::toDto);
        }

        // Check if user is a sales person
        if (roles.contains("ROLE_SALES_PERSON")) {
            log.debug("User {} is a sales person, returning associated facilities", username);
            return facilityRepository
                .findByEmployeeRole(user.getId(), EmployeeRoleEnum.SALES_PERSON, EmployeeStatusEnum.ACTIVE, pageable)
                .map(facilityMapper::toDto);
        }

        // Check if user is a regular user (ROLE_USER)
        if (roles.contains("ROLE_USER")) {
            log.debug("User {} is a regular user, returning facilities with active subscriptions", username);
            return facilityRepository.findByUserSubscriptions(pageable).map(facilityMapper::toDto);
        }

        // For other roles (like ADMIN), return all facilities
        log.debug("User {} has admin privileges, returning all facilities", username);
        return facilityRepository.findAll(pageable).map(facilityMapper::toDto);
    }

    /**
     * Get all the facilities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FacilityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Facilities");
        return facilityRepository.findAll(pageable).map(facilityMapper::toDto);
    }

    /**
     * Get one facility by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FacilityDTO> findOne(Long id) {
        log.debug("Request to get Facility : {}", id);
        return facilityRepository.findById(id).map(facilityMapper::toDto);
    }

    /**
     * Delete the facility by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Facility : {}", id);
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Check if user is the owner of this facility
        Optional<Facility> facility = facilityRepository.findById(id);
        if (facility.isPresent() && !facility.get().getUser().equals(currentUser.get())) {
            throw new RuntimeException("Only the facility owner can delete the facility");
        }

        facilityRepository.deleteById(id);
        facilitySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the facility corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FacilityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Facilities for query {}", query);
        return facilitySearchRepository.search(query, pageable).map(facilityMapper::toDto);
    }
}
