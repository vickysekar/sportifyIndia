package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.SubscriptionPlan;
import com.sportifyindia.app.repository.SubscriptionPlanRepository;
import com.sportifyindia.app.repository.search.SubscriptionPlanSearchRepository;
import com.sportifyindia.app.service.dto.SubscriptionPlanDTO;
import com.sportifyindia.app.service.mapper.SubscriptionPlanMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.SubscriptionPlan}.
 */
@Service
@Transactional
public class SubscriptionPlanService {

    private final Logger log = LoggerFactory.getLogger(SubscriptionPlanService.class);

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    private final SubscriptionPlanMapper subscriptionPlanMapper;

    private final SubscriptionPlanSearchRepository subscriptionPlanSearchRepository;

    public SubscriptionPlanService(
        SubscriptionPlanRepository subscriptionPlanRepository,
        SubscriptionPlanMapper subscriptionPlanMapper,
        SubscriptionPlanSearchRepository subscriptionPlanSearchRepository
    ) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.subscriptionPlanMapper = subscriptionPlanMapper;
        this.subscriptionPlanSearchRepository = subscriptionPlanSearchRepository;
    }

    /**
     * Save a subscriptionPlan.
     *
     * @param subscriptionPlanDTO the entity to save.
     * @return the persisted entity.
     */
    public SubscriptionPlanDTO save(SubscriptionPlanDTO subscriptionPlanDTO) {
        log.debug("Request to save SubscriptionPlan : {}", subscriptionPlanDTO);
        SubscriptionPlan subscriptionPlan = subscriptionPlanMapper.toEntity(subscriptionPlanDTO);
        subscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
        SubscriptionPlanDTO result = subscriptionPlanMapper.toDto(subscriptionPlan);
        subscriptionPlanSearchRepository.index(subscriptionPlan);
        return result;
    }

    /**
     * Update a subscriptionPlan.
     *
     * @param subscriptionPlanDTO the entity to save.
     * @return the persisted entity.
     */
    public SubscriptionPlanDTO update(SubscriptionPlanDTO subscriptionPlanDTO) {
        log.debug("Request to update SubscriptionPlan : {}", subscriptionPlanDTO);
        SubscriptionPlan subscriptionPlan = subscriptionPlanMapper.toEntity(subscriptionPlanDTO);
        subscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
        SubscriptionPlanDTO result = subscriptionPlanMapper.toDto(subscriptionPlan);
        subscriptionPlanSearchRepository.index(subscriptionPlan);
        return result;
    }

    /**
     * Partially update a subscriptionPlan.
     *
     * @param subscriptionPlanDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SubscriptionPlanDTO> partialUpdate(SubscriptionPlanDTO subscriptionPlanDTO) {
        log.debug("Request to partially update SubscriptionPlan : {}", subscriptionPlanDTO);

        return subscriptionPlanRepository
            .findById(subscriptionPlanDTO.getId())
            .map(existingSubscriptionPlan -> {
                subscriptionPlanMapper.partialUpdate(existingSubscriptionPlan, subscriptionPlanDTO);

                return existingSubscriptionPlan;
            })
            .map(subscriptionPlanRepository::save)
            .map(savedSubscriptionPlan -> {
                subscriptionPlanSearchRepository.index(savedSubscriptionPlan);
                return savedSubscriptionPlan;
            })
            .map(subscriptionPlanMapper::toDto);
    }

    /**
     * Get all the subscriptionPlans.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionPlanDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SubscriptionPlans");
        return subscriptionPlanRepository.findAll(pageable).map(subscriptionPlanMapper::toDto);
    }

    /**
     * Get all the subscriptionPlans with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SubscriptionPlanDTO> findAllWithEagerRelationships(Pageable pageable) {
        return subscriptionPlanRepository.findAllWithEagerRelationships(pageable).map(subscriptionPlanMapper::toDto);
    }

    /**
     * Get one subscriptionPlan by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SubscriptionPlanDTO> findOne(Long id) {
        log.debug("Request to get SubscriptionPlan : {}", id);
        return subscriptionPlanRepository.findOneWithEagerRelationships(id).map(subscriptionPlanMapper::toDto);
    }

    /**
     * Delete the subscriptionPlan by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SubscriptionPlan : {}", id);
        subscriptionPlanRepository.deleteById(id);
        subscriptionPlanSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the subscriptionPlan corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SubscriptionPlanDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SubscriptionPlans for query {}", query);
        return subscriptionPlanSearchRepository.search(query, pageable).map(subscriptionPlanMapper::toDto);
    }
}
