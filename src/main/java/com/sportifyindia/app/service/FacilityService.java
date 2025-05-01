package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.repository.FacilityRepository;
import com.sportifyindia.app.repository.search.FacilitySearchRepository;
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

    public FacilityService(
        FacilityRepository facilityRepository,
        FacilityMapper facilityMapper,
        FacilitySearchRepository facilitySearchRepository
    ) {
        this.facilityRepository = facilityRepository;
        this.facilityMapper = facilityMapper;
        this.facilitySearchRepository = facilitySearchRepository;
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
        Facility facility = facilityMapper.toEntity(facilityDTO);
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

        return facilityRepository
            .findById(facilityDTO.getId())
            .map(existingFacility -> {
                facilityMapper.partialUpdate(existingFacility, facilityDTO);

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
     *  Get all the facilities where Address is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FacilityDTO> findAllWhereAddressIsNull() {
        log.debug("Request to get all facilities where Address is null");
        return StreamSupport
            .stream(facilityRepository.findAll().spliterator(), false)
            .filter(facility -> facility.getAddress() == null)
            .map(facilityMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
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
