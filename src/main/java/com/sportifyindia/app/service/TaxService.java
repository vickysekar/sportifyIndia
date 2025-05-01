package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Tax;
import com.sportifyindia.app.repository.TaxRepository;
import com.sportifyindia.app.repository.search.TaxSearchRepository;
import com.sportifyindia.app.service.dto.TaxDTO;
import com.sportifyindia.app.service.mapper.TaxMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.Tax}.
 */
@Service
@Transactional
public class TaxService {

    private final Logger log = LoggerFactory.getLogger(TaxService.class);

    private final TaxRepository taxRepository;

    private final TaxMapper taxMapper;

    private final TaxSearchRepository taxSearchRepository;

    public TaxService(TaxRepository taxRepository, TaxMapper taxMapper, TaxSearchRepository taxSearchRepository) {
        this.taxRepository = taxRepository;
        this.taxMapper = taxMapper;
        this.taxSearchRepository = taxSearchRepository;
    }

    /**
     * Save a tax.
     *
     * @param taxDTO the entity to save.
     * @return the persisted entity.
     */
    public TaxDTO save(TaxDTO taxDTO) {
        log.debug("Request to save Tax : {}", taxDTO);
        Tax tax = taxMapper.toEntity(taxDTO);
        tax = taxRepository.save(tax);
        TaxDTO result = taxMapper.toDto(tax);
        taxSearchRepository.index(tax);
        return result;
    }

    /**
     * Update a tax.
     *
     * @param taxDTO the entity to save.
     * @return the persisted entity.
     */
    public TaxDTO update(TaxDTO taxDTO) {
        log.debug("Request to update Tax : {}", taxDTO);
        Tax tax = taxMapper.toEntity(taxDTO);
        tax = taxRepository.save(tax);
        TaxDTO result = taxMapper.toDto(tax);
        taxSearchRepository.index(tax);
        return result;
    }

    /**
     * Partially update a tax.
     *
     * @param taxDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaxDTO> partialUpdate(TaxDTO taxDTO) {
        log.debug("Request to partially update Tax : {}", taxDTO);

        return taxRepository
            .findById(taxDTO.getId())
            .map(existingTax -> {
                taxMapper.partialUpdate(existingTax, taxDTO);

                return existingTax;
            })
            .map(taxRepository::save)
            .map(savedTax -> {
                taxSearchRepository.index(savedTax);
                return savedTax;
            })
            .map(taxMapper::toDto);
    }

    /**
     * Get all the taxes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TaxDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Taxes");
        return taxRepository.findAll(pageable).map(taxMapper::toDto);
    }

    /**
     * Get one tax by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TaxDTO> findOne(Long id) {
        log.debug("Request to get Tax : {}", id);
        return taxRepository.findById(id).map(taxMapper::toDto);
    }

    /**
     * Delete the tax by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Tax : {}", id);
        taxRepository.deleteById(id);
        taxSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the tax corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TaxDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Taxes for query {}", query);
        return taxSearchRepository.search(query, pageable).map(taxMapper::toDto);
    }
}
