package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.OneTimeEvent;
import com.sportifyindia.app.repository.OneTimeEventRepository;
import com.sportifyindia.app.repository.search.OneTimeEventSearchRepository;
import com.sportifyindia.app.service.dto.OneTimeEventDTO;
import com.sportifyindia.app.service.mapper.OneTimeEventMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.OneTimeEvent}.
 */
@Service
@Transactional
public class OneTimeEventService {

    private final Logger log = LoggerFactory.getLogger(OneTimeEventService.class);

    private final OneTimeEventRepository oneTimeEventRepository;

    private final OneTimeEventMapper oneTimeEventMapper;

    private final OneTimeEventSearchRepository oneTimeEventSearchRepository;

    public OneTimeEventService(
        OneTimeEventRepository oneTimeEventRepository,
        OneTimeEventMapper oneTimeEventMapper,
        OneTimeEventSearchRepository oneTimeEventSearchRepository
    ) {
        this.oneTimeEventRepository = oneTimeEventRepository;
        this.oneTimeEventMapper = oneTimeEventMapper;
        this.oneTimeEventSearchRepository = oneTimeEventSearchRepository;
    }

    /**
     * Save a oneTimeEvent.
     *
     * @param oneTimeEventDTO the entity to save.
     * @return the persisted entity.
     */
    public OneTimeEventDTO save(OneTimeEventDTO oneTimeEventDTO) {
        log.debug("Request to save OneTimeEvent : {}", oneTimeEventDTO);
        OneTimeEvent oneTimeEvent = oneTimeEventMapper.toEntity(oneTimeEventDTO);
        oneTimeEvent = oneTimeEventRepository.save(oneTimeEvent);
        OneTimeEventDTO result = oneTimeEventMapper.toDto(oneTimeEvent);
        oneTimeEventSearchRepository.index(oneTimeEvent);
        return result;
    }

    /**
     * Update a oneTimeEvent.
     *
     * @param oneTimeEventDTO the entity to save.
     * @return the persisted entity.
     */
    public OneTimeEventDTO update(OneTimeEventDTO oneTimeEventDTO) {
        log.debug("Request to update OneTimeEvent : {}", oneTimeEventDTO);
        OneTimeEvent oneTimeEvent = oneTimeEventMapper.toEntity(oneTimeEventDTO);
        oneTimeEvent = oneTimeEventRepository.save(oneTimeEvent);
        OneTimeEventDTO result = oneTimeEventMapper.toDto(oneTimeEvent);
        oneTimeEventSearchRepository.index(oneTimeEvent);
        return result;
    }

    /**
     * Partially update a oneTimeEvent.
     *
     * @param oneTimeEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OneTimeEventDTO> partialUpdate(OneTimeEventDTO oneTimeEventDTO) {
        log.debug("Request to partially update OneTimeEvent : {}", oneTimeEventDTO);

        return oneTimeEventRepository
            .findById(oneTimeEventDTO.getId())
            .map(existingOneTimeEvent -> {
                oneTimeEventMapper.partialUpdate(existingOneTimeEvent, oneTimeEventDTO);

                return existingOneTimeEvent;
            })
            .map(oneTimeEventRepository::save)
            .map(savedOneTimeEvent -> {
                oneTimeEventSearchRepository.index(savedOneTimeEvent);
                return savedOneTimeEvent;
            })
            .map(oneTimeEventMapper::toDto);
    }

    /**
     * Get all the oneTimeEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OneTimeEvents");
        return oneTimeEventRepository.findAll(pageable).map(oneTimeEventMapper::toDto);
    }

    /**
     * Get one oneTimeEvent by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OneTimeEventDTO> findOne(Long id) {
        log.debug("Request to get OneTimeEvent : {}", id);
        return oneTimeEventRepository.findById(id).map(oneTimeEventMapper::toDto);
    }

    /**
     * Delete the oneTimeEvent by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OneTimeEvent : {}", id);
        oneTimeEventRepository.deleteById(id);
        oneTimeEventSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the oneTimeEvent corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OneTimeEvents for query {}", query);
        return oneTimeEventSearchRepository.search(query, pageable).map(oneTimeEventMapper::toDto);
    }
}
