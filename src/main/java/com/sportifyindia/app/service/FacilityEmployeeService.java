package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.FacilityEmployee;
import com.sportifyindia.app.repository.FacilityEmployeeRepository;
import com.sportifyindia.app.repository.search.FacilityEmployeeSearchRepository;
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

    private final FacilityEmployeeMapper facilityEmployeeMapper;

    private final FacilityEmployeeSearchRepository facilityEmployeeSearchRepository;

    public FacilityEmployeeService(
        FacilityEmployeeRepository facilityEmployeeRepository,
        FacilityEmployeeMapper facilityEmployeeMapper,
        FacilityEmployeeSearchRepository facilityEmployeeSearchRepository
    ) {
        this.facilityEmployeeRepository = facilityEmployeeRepository;
        this.facilityEmployeeMapper = facilityEmployeeMapper;
        this.facilityEmployeeSearchRepository = facilityEmployeeSearchRepository;
    }

    /**
     * Save a facilityEmployee.
     *
     * @param facilityEmployeeDTO the entity to save.
     * @return the persisted entity.
     */
    public FacilityEmployeeDTO save(FacilityEmployeeDTO facilityEmployeeDTO) {
        log.debug("Request to save FacilityEmployee : {}", facilityEmployeeDTO);
        FacilityEmployee facilityEmployee = facilityEmployeeMapper.toEntity(facilityEmployeeDTO);
        facilityEmployee = facilityEmployeeRepository.save(facilityEmployee);
        FacilityEmployeeDTO result = facilityEmployeeMapper.toDto(facilityEmployee);
        facilityEmployeeSearchRepository.index(facilityEmployee);
        return result;
    }

    /**
     * Update a facilityEmployee.
     *
     * @param facilityEmployeeDTO the entity to save.
     * @return the persisted entity.
     */
    public FacilityEmployeeDTO update(FacilityEmployeeDTO facilityEmployeeDTO) {
        log.debug("Request to update FacilityEmployee : {}", facilityEmployeeDTO);
        FacilityEmployee facilityEmployee = facilityEmployeeMapper.toEntity(facilityEmployeeDTO);
        facilityEmployee = facilityEmployeeRepository.save(facilityEmployee);
        FacilityEmployeeDTO result = facilityEmployeeMapper.toDto(facilityEmployee);
        facilityEmployeeSearchRepository.index(facilityEmployee);
        return result;
    }

    /**
     * Partially update a facilityEmployee.
     *
     * @param facilityEmployeeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FacilityEmployeeDTO> partialUpdate(FacilityEmployeeDTO facilityEmployeeDTO) {
        log.debug("Request to partially update FacilityEmployee : {}", facilityEmployeeDTO);

        return facilityEmployeeRepository
            .findById(facilityEmployeeDTO.getId())
            .map(existingFacilityEmployee -> {
                facilityEmployeeMapper.partialUpdate(existingFacilityEmployee, facilityEmployeeDTO);

                return existingFacilityEmployee;
            })
            .map(facilityEmployeeRepository::save)
            .map(savedFacilityEmployee -> {
                facilityEmployeeSearchRepository.index(savedFacilityEmployee);
                return savedFacilityEmployee;
            })
            .map(facilityEmployeeMapper::toDto);
    }

    /**
     * Get all the facilityEmployees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FacilityEmployeeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FacilityEmployees");
        return facilityEmployeeRepository.findAll(pageable).map(facilityEmployeeMapper::toDto);
    }

    /**
     * Get one facilityEmployee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FacilityEmployeeDTO> findOne(Long id) {
        log.debug("Request to get FacilityEmployee : {}", id);
        return facilityEmployeeRepository.findById(id).map(facilityEmployeeMapper::toDto);
    }

    /**
     * Delete the facilityEmployee by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FacilityEmployee : {}", id);
        facilityEmployeeRepository.deleteById(id);
        facilityEmployeeSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the facilityEmployee corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FacilityEmployeeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of FacilityEmployees for query {}", query);
        return facilityEmployeeSearchRepository.search(query, pageable).map(facilityEmployeeMapper::toDto);
    }
}
