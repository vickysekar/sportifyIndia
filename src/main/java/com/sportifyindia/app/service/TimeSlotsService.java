package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.repository.TimeSlotsRepository;
import com.sportifyindia.app.repository.search.TimeSlotsSearchRepository;
import com.sportifyindia.app.service.dto.TimeSlotsDTO;
import com.sportifyindia.app.service.mapper.TimeSlotsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.TimeSlots}.
 */
@Service
@Transactional
public class TimeSlotsService {

    private final Logger log = LoggerFactory.getLogger(TimeSlotsService.class);

    private final TimeSlotsRepository timeSlotsRepository;

    private final TimeSlotsMapper timeSlotsMapper;

    private final TimeSlotsSearchRepository timeSlotsSearchRepository;

    public TimeSlotsService(
        TimeSlotsRepository timeSlotsRepository,
        TimeSlotsMapper timeSlotsMapper,
        TimeSlotsSearchRepository timeSlotsSearchRepository
    ) {
        this.timeSlotsRepository = timeSlotsRepository;
        this.timeSlotsMapper = timeSlotsMapper;
        this.timeSlotsSearchRepository = timeSlotsSearchRepository;
    }

    /**
     * Save a timeSlots.
     *
     * @param timeSlotsDTO the entity to save.
     * @return the persisted entity.
     */
    public TimeSlotsDTO save(TimeSlotsDTO timeSlotsDTO) {
        log.debug("Request to save TimeSlots : {}", timeSlotsDTO);
        TimeSlots timeSlots = timeSlotsMapper.toEntity(timeSlotsDTO);
        timeSlots = timeSlotsRepository.save(timeSlots);
        TimeSlotsDTO result = timeSlotsMapper.toDto(timeSlots);
        timeSlotsSearchRepository.index(timeSlots);
        return result;
    }

    /**
     * Update a timeSlots.
     *
     * @param timeSlotsDTO the entity to save.
     * @return the persisted entity.
     */
    public TimeSlotsDTO update(TimeSlotsDTO timeSlotsDTO) {
        log.debug("Request to update TimeSlots : {}", timeSlotsDTO);
        TimeSlots timeSlots = timeSlotsMapper.toEntity(timeSlotsDTO);
        timeSlots = timeSlotsRepository.save(timeSlots);
        TimeSlotsDTO result = timeSlotsMapper.toDto(timeSlots);
        timeSlotsSearchRepository.index(timeSlots);
        return result;
    }

    /**
     * Partially update a timeSlots.
     *
     * @param timeSlotsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TimeSlotsDTO> partialUpdate(TimeSlotsDTO timeSlotsDTO) {
        log.debug("Request to partially update TimeSlots : {}", timeSlotsDTO);

        return timeSlotsRepository
            .findById(timeSlotsDTO.getId())
            .map(existingTimeSlots -> {
                timeSlotsMapper.partialUpdate(existingTimeSlots, timeSlotsDTO);

                return existingTimeSlots;
            })
            .map(timeSlotsRepository::save)
            .map(savedTimeSlots -> {
                timeSlotsSearchRepository.index(savedTimeSlots);
                return savedTimeSlots;
            })
            .map(timeSlotsMapper::toDto);
    }

    /**
     * Get all the timeSlots.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TimeSlotsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TimeSlots");
        return timeSlotsRepository.findAll(pageable).map(timeSlotsMapper::toDto);
    }

    /**
     * Get one timeSlots by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TimeSlotsDTO> findOne(Long id) {
        log.debug("Request to get TimeSlots : {}", id);
        return timeSlotsRepository.findById(id).map(timeSlotsMapper::toDto);
    }

    /**
     * Delete the timeSlots by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TimeSlots : {}", id);
        timeSlotsRepository.deleteById(id);
        timeSlotsSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the timeSlots corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TimeSlotsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TimeSlots for query {}", query);
        return timeSlotsSearchRepository.search(query, pageable).map(timeSlotsMapper::toDto);
    }
}
