package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.TaxMaster;
import com.sportifyindia.app.repository.TaxMasterRepository;
import com.sportifyindia.app.repository.search.TaxMasterSearchRepository;
import com.sportifyindia.app.service.dto.TaxMasterDTO;
import com.sportifyindia.app.service.mapper.TaxMasterMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.TaxMaster}.
 */
@Service
@Transactional
public class TaxMasterService {

    private final Logger log = LoggerFactory.getLogger(TaxMasterService.class);

    private final TaxMasterRepository taxMasterRepository;

    private final TaxMasterMapper taxMasterMapper;

    private final TaxMasterSearchRepository taxMasterSearchRepository;

    public TaxMasterService(
        TaxMasterRepository taxMasterRepository,
        TaxMasterMapper taxMasterMapper,
        TaxMasterSearchRepository taxMasterSearchRepository
    ) {
        this.taxMasterRepository = taxMasterRepository;
        this.taxMasterMapper = taxMasterMapper;
        this.taxMasterSearchRepository = taxMasterSearchRepository;
    }

    /**
     * Save a taxMaster.
     *
     * @param taxMasterDTO the entity to save.
     * @return the persisted entity.
     */
    public TaxMasterDTO save(TaxMasterDTO taxMasterDTO) {
        log.debug("Request to save TaxMaster : {}", taxMasterDTO);
        TaxMaster taxMaster = taxMasterMapper.toEntity(taxMasterDTO);
        taxMaster = taxMasterRepository.save(taxMaster);
        TaxMasterDTO result = taxMasterMapper.toDto(taxMaster);
        taxMasterSearchRepository.index(taxMaster);
        return result;
    }

    /**
     * Update a taxMaster.
     *
     * @param taxMasterDTO the entity to save.
     * @return the persisted entity.
     */
    public TaxMasterDTO update(TaxMasterDTO taxMasterDTO) {
        log.debug("Request to update TaxMaster : {}", taxMasterDTO);
        TaxMaster taxMaster = taxMasterMapper.toEntity(taxMasterDTO);
        taxMaster = taxMasterRepository.save(taxMaster);
        TaxMasterDTO result = taxMasterMapper.toDto(taxMaster);
        taxMasterSearchRepository.index(taxMaster);
        return result;
    }

    /**
     * Partially update a taxMaster.
     *
     * @param taxMasterDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaxMasterDTO> partialUpdate(TaxMasterDTO taxMasterDTO) {
        log.debug("Request to partially update TaxMaster : {}", taxMasterDTO);

        return taxMasterRepository
            .findById(taxMasterDTO.getId())
            .map(existingTaxMaster -> {
                taxMasterMapper.partialUpdate(existingTaxMaster, taxMasterDTO);

                return existingTaxMaster;
            })
            .map(taxMasterRepository::save)
            .map(savedTaxMaster -> {
                taxMasterSearchRepository.index(savedTaxMaster);
                return savedTaxMaster;
            })
            .map(taxMasterMapper::toDto);
    }

    /**
     * Get all the taxMasters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TaxMasterDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TaxMasters");
        return taxMasterRepository.findAll(pageable).map(taxMasterMapper::toDto);
    }

    /**
     * Get one taxMaster by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TaxMasterDTO> findOne(Long id) {
        log.debug("Request to get TaxMaster : {}", id);
        return taxMasterRepository.findById(id).map(taxMasterMapper::toDto);
    }

    /**
     * Delete the taxMaster by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TaxMaster : {}", id);
        taxMasterRepository.deleteById(id);
        taxMasterSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the taxMaster corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TaxMasterDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TaxMasters for query {}", query);
        return taxMasterSearchRepository.search(query, pageable).map(taxMasterMapper::toDto);
    }
}
