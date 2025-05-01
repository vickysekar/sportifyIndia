package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.LeadActivity;
import com.sportifyindia.app.repository.LeadActivityRepository;
import com.sportifyindia.app.repository.search.LeadActivitySearchRepository;
import com.sportifyindia.app.service.dto.LeadActivityDTO;
import com.sportifyindia.app.service.mapper.LeadActivityMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.LeadActivity}.
 */
@Service
@Transactional
public class LeadActivityService {

    private final Logger log = LoggerFactory.getLogger(LeadActivityService.class);

    private final LeadActivityRepository leadActivityRepository;

    private final LeadActivityMapper leadActivityMapper;

    private final LeadActivitySearchRepository leadActivitySearchRepository;

    public LeadActivityService(
        LeadActivityRepository leadActivityRepository,
        LeadActivityMapper leadActivityMapper,
        LeadActivitySearchRepository leadActivitySearchRepository
    ) {
        this.leadActivityRepository = leadActivityRepository;
        this.leadActivityMapper = leadActivityMapper;
        this.leadActivitySearchRepository = leadActivitySearchRepository;
    }

    /**
     * Save a leadActivity.
     *
     * @param leadActivityDTO the entity to save.
     * @return the persisted entity.
     */
    public LeadActivityDTO save(LeadActivityDTO leadActivityDTO) {
        log.debug("Request to save LeadActivity : {}", leadActivityDTO);
        LeadActivity leadActivity = leadActivityMapper.toEntity(leadActivityDTO);
        leadActivity = leadActivityRepository.save(leadActivity);
        LeadActivityDTO result = leadActivityMapper.toDto(leadActivity);
        leadActivitySearchRepository.index(leadActivity);
        return result;
    }

    /**
     * Update a leadActivity.
     *
     * @param leadActivityDTO the entity to save.
     * @return the persisted entity.
     */
    public LeadActivityDTO update(LeadActivityDTO leadActivityDTO) {
        log.debug("Request to update LeadActivity : {}", leadActivityDTO);
        LeadActivity leadActivity = leadActivityMapper.toEntity(leadActivityDTO);
        leadActivity = leadActivityRepository.save(leadActivity);
        LeadActivityDTO result = leadActivityMapper.toDto(leadActivity);
        leadActivitySearchRepository.index(leadActivity);
        return result;
    }

    /**
     * Partially update a leadActivity.
     *
     * @param leadActivityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LeadActivityDTO> partialUpdate(LeadActivityDTO leadActivityDTO) {
        log.debug("Request to partially update LeadActivity : {}", leadActivityDTO);

        return leadActivityRepository
            .findById(leadActivityDTO.getId())
            .map(existingLeadActivity -> {
                leadActivityMapper.partialUpdate(existingLeadActivity, leadActivityDTO);

                return existingLeadActivity;
            })
            .map(leadActivityRepository::save)
            .map(savedLeadActivity -> {
                leadActivitySearchRepository.index(savedLeadActivity);
                return savedLeadActivity;
            })
            .map(leadActivityMapper::toDto);
    }

    /**
     * Get all the leadActivities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LeadActivityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all LeadActivities");
        return leadActivityRepository.findAll(pageable).map(leadActivityMapper::toDto);
    }

    /**
     * Get one leadActivity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LeadActivityDTO> findOne(Long id) {
        log.debug("Request to get LeadActivity : {}", id);
        return leadActivityRepository.findById(id).map(leadActivityMapper::toDto);
    }

    /**
     * Delete the leadActivity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete LeadActivity : {}", id);
        leadActivityRepository.deleteById(id);
        leadActivitySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the leadActivity corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LeadActivityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of LeadActivities for query {}", query);
        return leadActivitySearchRepository.search(query, pageable).map(leadActivityMapper::toDto);
    }
}
