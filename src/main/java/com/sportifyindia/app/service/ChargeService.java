package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Charge;
import com.sportifyindia.app.repository.ChargeRepository;
import com.sportifyindia.app.repository.search.ChargeSearchRepository;
import com.sportifyindia.app.service.dto.ChargeDTO;
import com.sportifyindia.app.service.mapper.ChargeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.Charge}.
 */
@Service
@Transactional
public class ChargeService {

    private final Logger log = LoggerFactory.getLogger(ChargeService.class);

    private final ChargeRepository chargeRepository;

    private final ChargeMapper chargeMapper;

    private final ChargeSearchRepository chargeSearchRepository;

    public ChargeService(ChargeRepository chargeRepository, ChargeMapper chargeMapper, ChargeSearchRepository chargeSearchRepository) {
        this.chargeRepository = chargeRepository;
        this.chargeMapper = chargeMapper;
        this.chargeSearchRepository = chargeSearchRepository;
    }

    /**
     * Save a charge.
     *
     * @param chargeDTO the entity to save.
     * @return the persisted entity.
     */
    public ChargeDTO save(ChargeDTO chargeDTO) {
        log.debug("Request to save Charge : {}", chargeDTO);
        Charge charge = chargeMapper.toEntity(chargeDTO);
        charge = chargeRepository.save(charge);
        ChargeDTO result = chargeMapper.toDto(charge);
        chargeSearchRepository.index(charge);
        return result;
    }

    /**
     * Update a charge.
     *
     * @param chargeDTO the entity to save.
     * @return the persisted entity.
     */
    public ChargeDTO update(ChargeDTO chargeDTO) {
        log.debug("Request to update Charge : {}", chargeDTO);
        Charge charge = chargeMapper.toEntity(chargeDTO);
        charge = chargeRepository.save(charge);
        ChargeDTO result = chargeMapper.toDto(charge);
        chargeSearchRepository.index(charge);
        return result;
    }

    /**
     * Partially update a charge.
     *
     * @param chargeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ChargeDTO> partialUpdate(ChargeDTO chargeDTO) {
        log.debug("Request to partially update Charge : {}", chargeDTO);

        return chargeRepository
            .findById(chargeDTO.getId())
            .map(existingCharge -> {
                chargeMapper.partialUpdate(existingCharge, chargeDTO);

                return existingCharge;
            })
            .map(chargeRepository::save)
            .map(savedCharge -> {
                chargeSearchRepository.index(savedCharge);
                return savedCharge;
            })
            .map(chargeMapper::toDto);
    }

    /**
     * Get all the charges.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ChargeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Charges");
        return chargeRepository.findAll(pageable).map(chargeMapper::toDto);
    }

    /**
     * Get one charge by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ChargeDTO> findOne(Long id) {
        log.debug("Request to get Charge : {}", id);
        return chargeRepository.findById(id).map(chargeMapper::toDto);
    }

    /**
     * Delete the charge by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Charge : {}", id);
        chargeRepository.deleteById(id);
        chargeSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the charge corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ChargeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Charges for query {}", query);
        return chargeSearchRepository.search(query, pageable).map(chargeMapper::toDto);
    }
}
