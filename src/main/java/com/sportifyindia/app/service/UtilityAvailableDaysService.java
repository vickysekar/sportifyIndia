package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.UtilityAvailableDays;
import com.sportifyindia.app.repository.UtilityAvailableDaysRepository;
import com.sportifyindia.app.repository.search.UtilityAvailableDaysSearchRepository;
import com.sportifyindia.app.service.dto.UtilityAvailableDaysDTO;
import com.sportifyindia.app.service.mapper.UtilityAvailableDaysMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.UtilityAvailableDays}.
 */
@Service
@Transactional
public class UtilityAvailableDaysService {

    private final Logger log = LoggerFactory.getLogger(UtilityAvailableDaysService.class);

    private final UtilityAvailableDaysRepository utilityAvailableDaysRepository;

    private final UtilityAvailableDaysMapper utilityAvailableDaysMapper;

    private final UtilityAvailableDaysSearchRepository utilityAvailableDaysSearchRepository;

    public UtilityAvailableDaysService(
        UtilityAvailableDaysRepository utilityAvailableDaysRepository,
        UtilityAvailableDaysMapper utilityAvailableDaysMapper,
        UtilityAvailableDaysSearchRepository utilityAvailableDaysSearchRepository
    ) {
        this.utilityAvailableDaysRepository = utilityAvailableDaysRepository;
        this.utilityAvailableDaysMapper = utilityAvailableDaysMapper;
        this.utilityAvailableDaysSearchRepository = utilityAvailableDaysSearchRepository;
    }

    /**
     * Save a utilityAvailableDays.
     *
     * @param utilityAvailableDaysDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityAvailableDaysDTO save(UtilityAvailableDaysDTO utilityAvailableDaysDTO) {
        log.debug("Request to save UtilityAvailableDays : {}", utilityAvailableDaysDTO);
        UtilityAvailableDays utilityAvailableDays = utilityAvailableDaysMapper.toEntity(utilityAvailableDaysDTO);
        utilityAvailableDays = utilityAvailableDaysRepository.save(utilityAvailableDays);
        UtilityAvailableDaysDTO result = utilityAvailableDaysMapper.toDto(utilityAvailableDays);
        utilityAvailableDaysSearchRepository.index(utilityAvailableDays);
        return result;
    }

    /**
     * Update a utilityAvailableDays.
     *
     * @param utilityAvailableDaysDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityAvailableDaysDTO update(UtilityAvailableDaysDTO utilityAvailableDaysDTO) {
        log.debug("Request to update UtilityAvailableDays : {}", utilityAvailableDaysDTO);
        UtilityAvailableDays utilityAvailableDays = utilityAvailableDaysMapper.toEntity(utilityAvailableDaysDTO);
        utilityAvailableDays = utilityAvailableDaysRepository.save(utilityAvailableDays);
        UtilityAvailableDaysDTO result = utilityAvailableDaysMapper.toDto(utilityAvailableDays);
        utilityAvailableDaysSearchRepository.index(utilityAvailableDays);
        return result;
    }

    /**
     * Partially update a utilityAvailableDays.
     *
     * @param utilityAvailableDaysDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UtilityAvailableDaysDTO> partialUpdate(UtilityAvailableDaysDTO utilityAvailableDaysDTO) {
        log.debug("Request to partially update UtilityAvailableDays : {}", utilityAvailableDaysDTO);

        return utilityAvailableDaysRepository
            .findById(utilityAvailableDaysDTO.getId())
            .map(existingUtilityAvailableDays -> {
                utilityAvailableDaysMapper.partialUpdate(existingUtilityAvailableDays, utilityAvailableDaysDTO);

                return existingUtilityAvailableDays;
            })
            .map(utilityAvailableDaysRepository::save)
            .map(savedUtilityAvailableDays -> {
                utilityAvailableDaysSearchRepository.index(savedUtilityAvailableDays);
                return savedUtilityAvailableDays;
            })
            .map(utilityAvailableDaysMapper::toDto);
    }

    /**
     * Get all the utilityAvailableDays.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilityAvailableDaysDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UtilityAvailableDays");
        return utilityAvailableDaysRepository.findAll(pageable).map(utilityAvailableDaysMapper::toDto);
    }

    /**
     * Get one utilityAvailableDays by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UtilityAvailableDaysDTO> findOne(Long id) {
        log.debug("Request to get UtilityAvailableDays : {}", id);
        return utilityAvailableDaysRepository.findById(id).map(utilityAvailableDaysMapper::toDto);
    }

    /**
     * Delete the utilityAvailableDays by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UtilityAvailableDays : {}", id);
        utilityAvailableDaysRepository.deleteById(id);
        utilityAvailableDaysSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the utilityAvailableDays corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilityAvailableDaysDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UtilityAvailableDays for query {}", query);
        return utilityAvailableDaysSearchRepository.search(query, pageable).map(utilityAvailableDaysMapper::toDto);
    }
}
