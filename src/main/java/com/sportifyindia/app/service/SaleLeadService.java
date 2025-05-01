package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.SaleLead;
import com.sportifyindia.app.repository.SaleLeadRepository;
import com.sportifyindia.app.repository.search.SaleLeadSearchRepository;
import com.sportifyindia.app.service.dto.SaleLeadDTO;
import com.sportifyindia.app.service.mapper.SaleLeadMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.SaleLead}.
 */
@Service
@Transactional
public class SaleLeadService {

    private final Logger log = LoggerFactory.getLogger(SaleLeadService.class);

    private final SaleLeadRepository saleLeadRepository;

    private final SaleLeadMapper saleLeadMapper;

    private final SaleLeadSearchRepository saleLeadSearchRepository;

    public SaleLeadService(
        SaleLeadRepository saleLeadRepository,
        SaleLeadMapper saleLeadMapper,
        SaleLeadSearchRepository saleLeadSearchRepository
    ) {
        this.saleLeadRepository = saleLeadRepository;
        this.saleLeadMapper = saleLeadMapper;
        this.saleLeadSearchRepository = saleLeadSearchRepository;
    }

    /**
     * Save a saleLead.
     *
     * @param saleLeadDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleLeadDTO save(SaleLeadDTO saleLeadDTO) {
        log.debug("Request to save SaleLead : {}", saleLeadDTO);
        SaleLead saleLead = saleLeadMapper.toEntity(saleLeadDTO);
        saleLead = saleLeadRepository.save(saleLead);
        SaleLeadDTO result = saleLeadMapper.toDto(saleLead);
        saleLeadSearchRepository.index(saleLead);
        return result;
    }

    /**
     * Update a saleLead.
     *
     * @param saleLeadDTO the entity to save.
     * @return the persisted entity.
     */
    public SaleLeadDTO update(SaleLeadDTO saleLeadDTO) {
        log.debug("Request to update SaleLead : {}", saleLeadDTO);
        SaleLead saleLead = saleLeadMapper.toEntity(saleLeadDTO);
        saleLead = saleLeadRepository.save(saleLead);
        SaleLeadDTO result = saleLeadMapper.toDto(saleLead);
        saleLeadSearchRepository.index(saleLead);
        return result;
    }

    /**
     * Partially update a saleLead.
     *
     * @param saleLeadDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SaleLeadDTO> partialUpdate(SaleLeadDTO saleLeadDTO) {
        log.debug("Request to partially update SaleLead : {}", saleLeadDTO);

        return saleLeadRepository
            .findById(saleLeadDTO.getId())
            .map(existingSaleLead -> {
                saleLeadMapper.partialUpdate(existingSaleLead, saleLeadDTO);

                return existingSaleLead;
            })
            .map(saleLeadRepository::save)
            .map(savedSaleLead -> {
                saleLeadSearchRepository.index(savedSaleLead);
                return savedSaleLead;
            })
            .map(saleLeadMapper::toDto);
    }

    /**
     * Get all the saleLeads.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleLeadDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SaleLeads");
        return saleLeadRepository.findAll(pageable).map(saleLeadMapper::toDto);
    }

    /**
     * Get one saleLead by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SaleLeadDTO> findOne(Long id) {
        log.debug("Request to get SaleLead : {}", id);
        return saleLeadRepository.findById(id).map(saleLeadMapper::toDto);
    }

    /**
     * Delete the saleLead by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SaleLead : {}", id);
        saleLeadRepository.deleteById(id);
        saleLeadSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the saleLead corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SaleLeadDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SaleLeads for query {}", query);
        return saleLeadSearchRepository.search(query, pageable).map(saleLeadMapper::toDto);
    }
}
