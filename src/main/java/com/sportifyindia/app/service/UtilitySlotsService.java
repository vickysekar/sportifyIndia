package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.UtilitySlots;
import com.sportifyindia.app.repository.UtilitySlotsRepository;
import com.sportifyindia.app.repository.search.UtilitySlotsSearchRepository;
import com.sportifyindia.app.service.dto.UtilitySlotsDTO;
import com.sportifyindia.app.service.mapper.UtilitySlotsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.UtilitySlots}.
 */
@Service
@Transactional
public class UtilitySlotsService {

    private final Logger log = LoggerFactory.getLogger(UtilitySlotsService.class);

    private final UtilitySlotsRepository utilitySlotsRepository;

    private final UtilitySlotsMapper utilitySlotsMapper;

    private final UtilitySlotsSearchRepository utilitySlotsSearchRepository;

    public UtilitySlotsService(
        UtilitySlotsRepository utilitySlotsRepository,
        UtilitySlotsMapper utilitySlotsMapper,
        UtilitySlotsSearchRepository utilitySlotsSearchRepository
    ) {
        this.utilitySlotsRepository = utilitySlotsRepository;
        this.utilitySlotsMapper = utilitySlotsMapper;
        this.utilitySlotsSearchRepository = utilitySlotsSearchRepository;
    }

    /**
     * Save a utilitySlots.
     *
     * @param utilitySlotsDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilitySlotsDTO save(UtilitySlotsDTO utilitySlotsDTO) {
        log.debug("Request to save UtilitySlots : {}", utilitySlotsDTO);
        UtilitySlots utilitySlots = utilitySlotsMapper.toEntity(utilitySlotsDTO);
        utilitySlots = utilitySlotsRepository.save(utilitySlots);
        UtilitySlotsDTO result = utilitySlotsMapper.toDto(utilitySlots);
        utilitySlotsSearchRepository.index(utilitySlots);
        return result;
    }

    /**
     * Update a utilitySlots.
     *
     * @param utilitySlotsDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilitySlotsDTO update(UtilitySlotsDTO utilitySlotsDTO) {
        log.debug("Request to update UtilitySlots : {}", utilitySlotsDTO);
        UtilitySlots utilitySlots = utilitySlotsMapper.toEntity(utilitySlotsDTO);
        utilitySlots = utilitySlotsRepository.save(utilitySlots);
        UtilitySlotsDTO result = utilitySlotsMapper.toDto(utilitySlots);
        utilitySlotsSearchRepository.index(utilitySlots);
        return result;
    }

    /**
     * Partially update a utilitySlots.
     *
     * @param utilitySlotsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UtilitySlotsDTO> partialUpdate(UtilitySlotsDTO utilitySlotsDTO) {
        log.debug("Request to partially update UtilitySlots : {}", utilitySlotsDTO);

        return utilitySlotsRepository
            .findById(utilitySlotsDTO.getId())
            .map(existingUtilitySlots -> {
                utilitySlotsMapper.partialUpdate(existingUtilitySlots, utilitySlotsDTO);

                return existingUtilitySlots;
            })
            .map(utilitySlotsRepository::save)
            .map(savedUtilitySlots -> {
                utilitySlotsSearchRepository.index(savedUtilitySlots);
                return savedUtilitySlots;
            })
            .map(utilitySlotsMapper::toDto);
    }

    /**
     * Get all the utilitySlots.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilitySlotsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UtilitySlots");
        return utilitySlotsRepository.findAll(pageable).map(utilitySlotsMapper::toDto);
    }

    /**
     * Get one utilitySlots by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UtilitySlotsDTO> findOne(Long id) {
        log.debug("Request to get UtilitySlots : {}", id);
        return utilitySlotsRepository.findById(id).map(utilitySlotsMapper::toDto);
    }

    /**
     * Delete the utilitySlots by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UtilitySlots : {}", id);
        utilitySlotsRepository.deleteById(id);
        utilitySlotsSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the utilitySlots corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilitySlotsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UtilitySlots for query {}", query);
        return utilitySlotsSearchRepository.search(query, pageable).map(utilitySlotsMapper::toDto);
    }
}
