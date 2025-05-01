package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.UtilityExceptionDays;
import com.sportifyindia.app.repository.UtilityExceptionDaysRepository;
import com.sportifyindia.app.repository.search.UtilityExceptionDaysSearchRepository;
import com.sportifyindia.app.service.dto.UtilityExceptionDaysDTO;
import com.sportifyindia.app.service.mapper.UtilityExceptionDaysMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.UtilityExceptionDays}.
 */
@Service
@Transactional
public class UtilityExceptionDaysService {

    private final Logger log = LoggerFactory.getLogger(UtilityExceptionDaysService.class);

    private final UtilityExceptionDaysRepository utilityExceptionDaysRepository;

    private final UtilityExceptionDaysMapper utilityExceptionDaysMapper;

    private final UtilityExceptionDaysSearchRepository utilityExceptionDaysSearchRepository;

    public UtilityExceptionDaysService(
        UtilityExceptionDaysRepository utilityExceptionDaysRepository,
        UtilityExceptionDaysMapper utilityExceptionDaysMapper,
        UtilityExceptionDaysSearchRepository utilityExceptionDaysSearchRepository
    ) {
        this.utilityExceptionDaysRepository = utilityExceptionDaysRepository;
        this.utilityExceptionDaysMapper = utilityExceptionDaysMapper;
        this.utilityExceptionDaysSearchRepository = utilityExceptionDaysSearchRepository;
    }

    /**
     * Save a utilityExceptionDays.
     *
     * @param utilityExceptionDaysDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityExceptionDaysDTO save(UtilityExceptionDaysDTO utilityExceptionDaysDTO) {
        log.debug("Request to save UtilityExceptionDays : {}", utilityExceptionDaysDTO);
        UtilityExceptionDays utilityExceptionDays = utilityExceptionDaysMapper.toEntity(utilityExceptionDaysDTO);
        utilityExceptionDays = utilityExceptionDaysRepository.save(utilityExceptionDays);
        UtilityExceptionDaysDTO result = utilityExceptionDaysMapper.toDto(utilityExceptionDays);
        utilityExceptionDaysSearchRepository.index(utilityExceptionDays);
        return result;
    }

    /**
     * Update a utilityExceptionDays.
     *
     * @param utilityExceptionDaysDTO the entity to save.
     * @return the persisted entity.
     */
    public UtilityExceptionDaysDTO update(UtilityExceptionDaysDTO utilityExceptionDaysDTO) {
        log.debug("Request to update UtilityExceptionDays : {}", utilityExceptionDaysDTO);
        UtilityExceptionDays utilityExceptionDays = utilityExceptionDaysMapper.toEntity(utilityExceptionDaysDTO);
        utilityExceptionDays = utilityExceptionDaysRepository.save(utilityExceptionDays);
        UtilityExceptionDaysDTO result = utilityExceptionDaysMapper.toDto(utilityExceptionDays);
        utilityExceptionDaysSearchRepository.index(utilityExceptionDays);
        return result;
    }

    /**
     * Partially update a utilityExceptionDays.
     *
     * @param utilityExceptionDaysDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UtilityExceptionDaysDTO> partialUpdate(UtilityExceptionDaysDTO utilityExceptionDaysDTO) {
        log.debug("Request to partially update UtilityExceptionDays : {}", utilityExceptionDaysDTO);

        return utilityExceptionDaysRepository
            .findById(utilityExceptionDaysDTO.getId())
            .map(existingUtilityExceptionDays -> {
                utilityExceptionDaysMapper.partialUpdate(existingUtilityExceptionDays, utilityExceptionDaysDTO);

                return existingUtilityExceptionDays;
            })
            .map(utilityExceptionDaysRepository::save)
            .map(savedUtilityExceptionDays -> {
                utilityExceptionDaysSearchRepository.index(savedUtilityExceptionDays);
                return savedUtilityExceptionDays;
            })
            .map(utilityExceptionDaysMapper::toDto);
    }

    /**
     * Get all the utilityExceptionDays.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilityExceptionDaysDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UtilityExceptionDays");
        return utilityExceptionDaysRepository.findAll(pageable).map(utilityExceptionDaysMapper::toDto);
    }

    /**
     * Get one utilityExceptionDays by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UtilityExceptionDaysDTO> findOne(Long id) {
        log.debug("Request to get UtilityExceptionDays : {}", id);
        return utilityExceptionDaysRepository.findById(id).map(utilityExceptionDaysMapper::toDto);
    }

    /**
     * Delete the utilityExceptionDays by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UtilityExceptionDays : {}", id);
        utilityExceptionDaysRepository.deleteById(id);
        utilityExceptionDaysSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the utilityExceptionDays corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UtilityExceptionDaysDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UtilityExceptionDays for query {}", query);
        return utilityExceptionDaysSearchRepository.search(query, pageable).map(utilityExceptionDaysMapper::toDto);
    }
}
