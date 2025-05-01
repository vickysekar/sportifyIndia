package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Discount;
import com.sportifyindia.app.repository.DiscountRepository;
import com.sportifyindia.app.repository.search.DiscountSearchRepository;
import com.sportifyindia.app.service.dto.DiscountDTO;
import com.sportifyindia.app.service.mapper.DiscountMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.Discount}.
 */
@Service
@Transactional
public class DiscountService {

    private final Logger log = LoggerFactory.getLogger(DiscountService.class);

    private final DiscountRepository discountRepository;

    private final DiscountMapper discountMapper;

    private final DiscountSearchRepository discountSearchRepository;

    public DiscountService(
        DiscountRepository discountRepository,
        DiscountMapper discountMapper,
        DiscountSearchRepository discountSearchRepository
    ) {
        this.discountRepository = discountRepository;
        this.discountMapper = discountMapper;
        this.discountSearchRepository = discountSearchRepository;
    }

    /**
     * Save a discount.
     *
     * @param discountDTO the entity to save.
     * @return the persisted entity.
     */
    public DiscountDTO save(DiscountDTO discountDTO) {
        log.debug("Request to save Discount : {}", discountDTO);
        Discount discount = discountMapper.toEntity(discountDTO);
        discount = discountRepository.save(discount);
        DiscountDTO result = discountMapper.toDto(discount);
        discountSearchRepository.index(discount);
        return result;
    }

    /**
     * Update a discount.
     *
     * @param discountDTO the entity to save.
     * @return the persisted entity.
     */
    public DiscountDTO update(DiscountDTO discountDTO) {
        log.debug("Request to update Discount : {}", discountDTO);
        Discount discount = discountMapper.toEntity(discountDTO);
        discount = discountRepository.save(discount);
        DiscountDTO result = discountMapper.toDto(discount);
        discountSearchRepository.index(discount);
        return result;
    }

    /**
     * Partially update a discount.
     *
     * @param discountDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DiscountDTO> partialUpdate(DiscountDTO discountDTO) {
        log.debug("Request to partially update Discount : {}", discountDTO);

        return discountRepository
            .findById(discountDTO.getId())
            .map(existingDiscount -> {
                discountMapper.partialUpdate(existingDiscount, discountDTO);

                return existingDiscount;
            })
            .map(discountRepository::save)
            .map(savedDiscount -> {
                discountSearchRepository.index(savedDiscount);
                return savedDiscount;
            })
            .map(discountMapper::toDto);
    }

    /**
     * Get all the discounts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DiscountDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Discounts");
        return discountRepository.findAll(pageable).map(discountMapper::toDto);
    }

    /**
     * Get one discount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DiscountDTO> findOne(Long id) {
        log.debug("Request to get Discount : {}", id);
        return discountRepository.findById(id).map(discountMapper::toDto);
    }

    /**
     * Delete the discount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Discount : {}", id);
        discountRepository.deleteById(id);
        discountSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the discount corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<DiscountDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Discounts for query {}", query);
        return discountSearchRepository.search(query, pageable).map(discountMapper::toDto);
    }
}
