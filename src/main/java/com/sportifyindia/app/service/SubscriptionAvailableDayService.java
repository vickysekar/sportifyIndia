package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.SubscriptionAvailableDay;
import com.sportifyindia.app.repository.SubscriptionAvailableDayRepository;
import com.sportifyindia.app.repository.search.SubscriptionAvailableDaySearchRepository;
import com.sportifyindia.app.service.dto.SubscriptionAvailableDayDTO;
import com.sportifyindia.app.service.mapper.SubscriptionAvailableDayMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.SubscriptionAvailableDay}.
 */
@Service
@Transactional
public class SubscriptionAvailableDayService {

    private final Logger log = LoggerFactory.getLogger(SubscriptionAvailableDayService.class);

    private final SubscriptionAvailableDayRepository subscriptionAvailableDayRepository;

    private final SubscriptionAvailableDayMapper subscriptionAvailableDayMapper;

    private final SubscriptionAvailableDaySearchRepository subscriptionAvailableDaySearchRepository;

    public SubscriptionAvailableDayService(
        SubscriptionAvailableDayRepository subscriptionAvailableDayRepository,
        SubscriptionAvailableDayMapper subscriptionAvailableDayMapper,
        SubscriptionAvailableDaySearchRepository subscriptionAvailableDaySearchRepository
    ) {
        this.subscriptionAvailableDayRepository = subscriptionAvailableDayRepository;
        this.subscriptionAvailableDayMapper = subscriptionAvailableDayMapper;
        this.subscriptionAvailableDaySearchRepository = subscriptionAvailableDaySearchRepository;
    }

    /**
     * Save a subscriptionAvailableDay.
     *
     * @param subscriptionAvailableDayDTO the entity to save.
     * @return the persisted entity.
     */
    public SubscriptionAvailableDayDTO save(SubscriptionAvailableDayDTO subscriptionAvailableDayDTO) {
        log.debug("Request to save SubscriptionAvailableDay : {}", subscriptionAvailableDayDTO);
        SubscriptionAvailableDay subscriptionAvailableDay = subscriptionAvailableDayMapper.toEntity(subscriptionAvailableDayDTO);
        subscriptionAvailableDay = subscriptionAvailableDayRepository.save(subscriptionAvailableDay);
        SubscriptionAvailableDayDTO result = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);
        subscriptionAvailableDaySearchRepository.index(subscriptionAvailableDay);
        return result;
    }

    /**
     * Update a subscriptionAvailableDay.
     *
     * @param subscriptionAvailableDayDTO the entity to save.
     * @return the persisted entity.
     */
    public SubscriptionAvailableDayDTO update(SubscriptionAvailableDayDTO subscriptionAvailableDayDTO) {
        log.debug("Request to update SubscriptionAvailableDay : {}", subscriptionAvailableDayDTO);
        SubscriptionAvailableDay subscriptionAvailableDay = subscriptionAvailableDayMapper.toEntity(subscriptionAvailableDayDTO);
        subscriptionAvailableDay = subscriptionAvailableDayRepository.save(subscriptionAvailableDay);
        SubscriptionAvailableDayDTO result = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);
        subscriptionAvailableDaySearchRepository.index(subscriptionAvailableDay);
        return result;
    }

    /**
     * Partially update a subscriptionAvailableDay.
     *
     * @param subscriptionAvailableDayDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SubscriptionAvailableDayDTO> partialUpdate(SubscriptionAvailableDayDTO subscriptionAvailableDayDTO) {
        log.debug("Request to partially update SubscriptionAvailableDay : {}", subscriptionAvailableDayDTO);

        return subscriptionAvailableDayRepository
            .findById(subscriptionAvailableDayDTO.getId())
            .map(existingSubscriptionAvailableDay -> {
                subscriptionAvailableDayMapper.partialUpdate(existingSubscriptionAvailableDay, subscriptionAvailableDayDTO);

                return existingSubscriptionAvailableDay;
            })
            .map(subscriptionAvailableDayRepository::save)
            .map(savedSubscriptionAvailableDay -> {
                subscriptionAvailableDaySearchRepository.index(savedSubscriptionAvailableDay);
                return savedSubscriptionAvailableDay;
            })
            .map(subscriptionAvailableDayMapper::toDto);
    }

    /**
     * Get all the subscriptionAvailableDays.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionAvailableDayDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SubscriptionAvailableDays");
        return subscriptionAvailableDayRepository.findAll(pageable).map(subscriptionAvailableDayMapper::toDto);
    }

    /**
     * Get one subscriptionAvailableDay by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SubscriptionAvailableDayDTO> findOne(Long id) {
        log.debug("Request to get SubscriptionAvailableDay : {}", id);
        return subscriptionAvailableDayRepository.findById(id).map(subscriptionAvailableDayMapper::toDto);
    }

    /**
     * Delete the subscriptionAvailableDay by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SubscriptionAvailableDay : {}", id);
        subscriptionAvailableDayRepository.deleteById(id);
        subscriptionAvailableDaySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the subscriptionAvailableDay corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionAvailableDayDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SubscriptionAvailableDays for query {}", query);
        return subscriptionAvailableDaySearchRepository.search(query, pageable).map(subscriptionAvailableDayMapper::toDto);
    }
}
