package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Utility;
import com.sportifyindia.app.repository.UtilityRepository;
import com.sportifyindia.app.repository.search.UtilitySearchRepository;
import com.sportifyindia.app.service.dto.UtilityDTO;
import com.sportifyindia.app.service.mapper.UtilityMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.Utility}.
 */
@Service
@Transactional
public class UtilityService {

    private final Logger log = LoggerFactory.getLogger(UtilityService.class);

    private final UtilityRepository utilityRepository;

    private final UtilityMapper utilityMapper;

    private final UtilitySearchRepository utilitySearchRepository;

    public UtilityService(
        UtilityRepository utilityRepository,
        UtilityMapper utilityMapper,
        UtilitySearchRepository utilitySearchRepository
    ) {
        this.utilityRepository = utilityRepository;
        this.utilityMapper = utilityMapper;
        this.utilitySearchRepository = utilitySearchRepository;
    }

    /**
     * Save a utility.
     *
     * @param utilityDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityDTO save(UtilityDTO utilityDTO) {
        log.debug("Request to save Utility : {}", utilityDTO);
        Utility utility = utilityMapper.toEntity(utilityDTO);
        utility = utilityRepository.save(utility);
        UtilityDTO result = utilityMapper.toDto(utility);
        utilitySearchRepository.index(utility);
        return result;
    }

    /**
     * Update a utility.
     *
     * @param utilityDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityDTO update(UtilityDTO utilityDTO) {
        log.debug("Request to update Utility : {}", utilityDTO);
        Utility utility = utilityMapper.toEntity(utilityDTO);
        utility = utilityRepository.save(utility);
        UtilityDTO result = utilityMapper.toDto(utility);
        utilitySearchRepository.index(utility);
        return result;
    }

    /**
     * Partially update a utility.
     *
     * @param utilityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UtilityDTO> partialUpdate(UtilityDTO utilityDTO) {
        log.debug("Request to partially update Utility : {}", utilityDTO);

        return utilityRepository
            .findById(utilityDTO.getId())
            .map(existingUtility -> {
                utilityMapper.partialUpdate(existingUtility, utilityDTO);

                return existingUtility;
            })
            .map(utilityRepository::save)
            .map(savedUtility -> {
                utilitySearchRepository.index(savedUtility);
                return savedUtility;
            })
            .map(utilityMapper::toDto);
    }

    /**
     * Get all the utilities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Utilities");
        return utilityRepository.findAll(pageable).map(utilityMapper::toDto);
    }

    /**
     * Get all the utilities with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<UtilityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return utilityRepository.findAllWithEagerRelationships(pageable).map(utilityMapper::toDto);
    }

    /**
     * Get one utility by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UtilityDTO> findOne(Long id) {
        log.debug("Request to get Utility : {}", id);
        return utilityRepository.findOneWithEagerRelationships(id).map(utilityMapper::toDto);
    }

    /**
     * Delete the utility by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Utility : {}", id);
        utilityRepository.deleteById(id);
        utilitySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the utility corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Utilities for query {}", query);
        return utilitySearchRepository.search(query, pageable).map(utilityMapper::toDto);
    }
}
