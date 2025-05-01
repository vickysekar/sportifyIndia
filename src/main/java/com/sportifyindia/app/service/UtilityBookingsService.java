package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.UtilityBookings;
import com.sportifyindia.app.repository.UtilityBookingsRepository;
import com.sportifyindia.app.repository.search.UtilityBookingsSearchRepository;
import com.sportifyindia.app.service.dto.UtilityBookingsDTO;
import com.sportifyindia.app.service.mapper.UtilityBookingsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.UtilityBookings}.
 */
@Service
@Transactional
public class UtilityBookingsService {

    private final Logger log = LoggerFactory.getLogger(UtilityBookingsService.class);

    private final UtilityBookingsRepository utilityBookingsRepository;

    private final UtilityBookingsMapper utilityBookingsMapper;

    private final UtilityBookingsSearchRepository utilityBookingsSearchRepository;

    public UtilityBookingsService(
        UtilityBookingsRepository utilityBookingsRepository,
        UtilityBookingsMapper utilityBookingsMapper,
        UtilityBookingsSearchRepository utilityBookingsSearchRepository
    ) {
        this.utilityBookingsRepository = utilityBookingsRepository;
        this.utilityBookingsMapper = utilityBookingsMapper;
        this.utilityBookingsSearchRepository = utilityBookingsSearchRepository;
    }

    /**
     * Save a utilityBookings.
     *
     * @param utilityBookingsDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityBookingsDTO save(UtilityBookingsDTO utilityBookingsDTO) {
        log.debug("Request to save UtilityBookings : {}", utilityBookingsDTO);
        UtilityBookings utilityBookings = utilityBookingsMapper.toEntity(utilityBookingsDTO);
        utilityBookings = utilityBookingsRepository.save(utilityBookings);
        UtilityBookingsDTO result = utilityBookingsMapper.toDto(utilityBookings);
        utilityBookingsSearchRepository.index(utilityBookings);
        return result;
    }

    /**
     * Update a utilityBookings.
     *
     * @param utilityBookingsDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityBookingsDTO update(UtilityBookingsDTO utilityBookingsDTO) {
        log.debug("Request to update UtilityBookings : {}", utilityBookingsDTO);
        UtilityBookings utilityBookings = utilityBookingsMapper.toEntity(utilityBookingsDTO);
        utilityBookings = utilityBookingsRepository.save(utilityBookings);
        UtilityBookingsDTO result = utilityBookingsMapper.toDto(utilityBookings);
        utilityBookingsSearchRepository.index(utilityBookings);
        return result;
    }

    /**
     * Partially update a utilityBookings.
     *
     * @param utilityBookingsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UtilityBookingsDTO> partialUpdate(UtilityBookingsDTO utilityBookingsDTO) {
        log.debug("Request to partially update UtilityBookings : {}", utilityBookingsDTO);

        return utilityBookingsRepository
            .findById(utilityBookingsDTO.getId())
            .map(existingUtilityBookings -> {
                utilityBookingsMapper.partialUpdate(existingUtilityBookings, utilityBookingsDTO);

                return existingUtilityBookings;
            })
            .map(utilityBookingsRepository::save)
            .map(savedUtilityBookings -> {
                utilityBookingsSearchRepository.index(savedUtilityBookings);
                return savedUtilityBookings;
            })
            .map(utilityBookingsMapper::toDto);
    }

    /**
     * Get all the utilityBookings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilityBookingsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UtilityBookings");
        return utilityBookingsRepository.findAll(pageable).map(utilityBookingsMapper::toDto);
    }

    /**
     * Get one utilityBookings by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UtilityBookingsDTO> findOne(Long id) {
        log.debug("Request to get UtilityBookings : {}", id);
        return utilityBookingsRepository.findById(id).map(utilityBookingsMapper::toDto);
    }

    /**
     * Delete the utilityBookings by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UtilityBookings : {}", id);
        utilityBookingsRepository.deleteById(id);
        utilityBookingsSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the utilityBookings corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilityBookingsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UtilityBookings for query {}", query);
        return utilityBookingsSearchRepository.search(query, pageable).map(utilityBookingsMapper::toDto);
    }
}
