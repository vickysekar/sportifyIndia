package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.OneTimeEventSubscribers;
import com.sportifyindia.app.repository.OneTimeEventSubscribersRepository;
import com.sportifyindia.app.repository.search.OneTimeEventSubscribersSearchRepository;
import com.sportifyindia.app.service.dto.OneTimeEventSubscribersDTO;
import com.sportifyindia.app.service.mapper.OneTimeEventSubscribersMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.OneTimeEventSubscribers}.
 */
@Service
@Transactional
public class OneTimeEventSubscribersService {

    private final Logger log = LoggerFactory.getLogger(OneTimeEventSubscribersService.class);

    private final OneTimeEventSubscribersRepository oneTimeEventSubscribersRepository;

    private final OneTimeEventSubscribersMapper oneTimeEventSubscribersMapper;

    private final OneTimeEventSubscribersSearchRepository oneTimeEventSubscribersSearchRepository;

    public OneTimeEventSubscribersService(
        OneTimeEventSubscribersRepository oneTimeEventSubscribersRepository,
        OneTimeEventSubscribersMapper oneTimeEventSubscribersMapper,
        OneTimeEventSubscribersSearchRepository oneTimeEventSubscribersSearchRepository
    ) {
        this.oneTimeEventSubscribersRepository = oneTimeEventSubscribersRepository;
        this.oneTimeEventSubscribersMapper = oneTimeEventSubscribersMapper;
        this.oneTimeEventSubscribersSearchRepository = oneTimeEventSubscribersSearchRepository;
    }

    /**
     * Save a oneTimeEventSubscribers.
     *
     * @param oneTimeEventSubscribersDTO the entity to save.
     * @return the persisted entity.
     */
    public OneTimeEventSubscribersDTO save(OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO) {
        log.debug("Request to save OneTimeEventSubscribers : {}", oneTimeEventSubscribersDTO);
        OneTimeEventSubscribers oneTimeEventSubscribers = oneTimeEventSubscribersMapper.toEntity(oneTimeEventSubscribersDTO);
        oneTimeEventSubscribers = oneTimeEventSubscribersRepository.save(oneTimeEventSubscribers);
        OneTimeEventSubscribersDTO result = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);
        oneTimeEventSubscribersSearchRepository.index(oneTimeEventSubscribers);
        return result;
    }

    /**
     * Update a oneTimeEventSubscribers.
     *
     * @param oneTimeEventSubscribersDTO the entity to save.
     * @return the persisted entity.
     */
    public OneTimeEventSubscribersDTO update(OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO) {
        log.debug("Request to update OneTimeEventSubscribers : {}", oneTimeEventSubscribersDTO);
        OneTimeEventSubscribers oneTimeEventSubscribers = oneTimeEventSubscribersMapper.toEntity(oneTimeEventSubscribersDTO);
        oneTimeEventSubscribers = oneTimeEventSubscribersRepository.save(oneTimeEventSubscribers);
        OneTimeEventSubscribersDTO result = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);
        oneTimeEventSubscribersSearchRepository.index(oneTimeEventSubscribers);
        return result;
    }

    /**
     * Partially update a oneTimeEventSubscribers.
     *
     * @param oneTimeEventSubscribersDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OneTimeEventSubscribersDTO> partialUpdate(OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO) {
        log.debug("Request to partially update OneTimeEventSubscribers : {}", oneTimeEventSubscribersDTO);

        return oneTimeEventSubscribersRepository
            .findById(oneTimeEventSubscribersDTO.getId())
            .map(existingOneTimeEventSubscribers -> {
                oneTimeEventSubscribersMapper.partialUpdate(existingOneTimeEventSubscribers, oneTimeEventSubscribersDTO);

                return existingOneTimeEventSubscribers;
            })
            .map(oneTimeEventSubscribersRepository::save)
            .map(savedOneTimeEventSubscribers -> {
                oneTimeEventSubscribersSearchRepository.index(savedOneTimeEventSubscribers);
                return savedOneTimeEventSubscribers;
            })
            .map(oneTimeEventSubscribersMapper::toDto);
    }

    /**
     * Get all the oneTimeEventSubscribers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventSubscribersDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OneTimeEventSubscribers");
        return oneTimeEventSubscribersRepository.findAll(pageable).map(oneTimeEventSubscribersMapper::toDto);
    }

    /**
     * Get one oneTimeEventSubscribers by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OneTimeEventSubscribersDTO> findOne(Long id) {
        log.debug("Request to get OneTimeEventSubscribers : {}", id);
        return oneTimeEventSubscribersRepository.findById(id).map(oneTimeEventSubscribersMapper::toDto);
    }

    /**
     * Delete the oneTimeEventSubscribers by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OneTimeEventSubscribers : {}", id);
        oneTimeEventSubscribersRepository.deleteById(id);
        oneTimeEventSubscribersSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the oneTimeEventSubscribers corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OneTimeEventSubscribersDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OneTimeEventSubscribers for query {}", query);
        return oneTimeEventSubscribersSearchRepository.search(query, pageable).map(oneTimeEventSubscribersMapper::toDto);
    }
}
