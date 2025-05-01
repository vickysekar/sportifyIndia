package com.sportifyindia.app.service;

import com.sportifyindia.app.domain.Notes;
import com.sportifyindia.app.repository.NotesRepository;
import com.sportifyindia.app.repository.search.NotesSearchRepository;
import com.sportifyindia.app.service.dto.NotesDTO;
import com.sportifyindia.app.service.mapper.NotesMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.sportifyindia.app.domain.Notes}.
 */
@Service
@Transactional
public class NotesService {

    private final Logger log = LoggerFactory.getLogger(NotesService.class);

    private final NotesRepository notesRepository;

    private final NotesMapper notesMapper;

    private final NotesSearchRepository notesSearchRepository;

    public NotesService(NotesRepository notesRepository, NotesMapper notesMapper, NotesSearchRepository notesSearchRepository) {
        this.notesRepository = notesRepository;
        this.notesMapper = notesMapper;
        this.notesSearchRepository = notesSearchRepository;
    }

    /**
     * Save a notes.
     *
     * @param notesDTO the entity to save.
     * @return the persisted entity.
     */
    public NotesDTO save(NotesDTO notesDTO) {
        log.debug("Request to save Notes : {}", notesDTO);
        Notes notes = notesMapper.toEntity(notesDTO);
        notes = notesRepository.save(notes);
        NotesDTO result = notesMapper.toDto(notes);
        notesSearchRepository.index(notes);
        return result;
    }

    /**
     * Update a notes.
     *
     * @param notesDTO the entity to save.
     * @return the persisted entity.
     */
    public NotesDTO update(NotesDTO notesDTO) {
        log.debug("Request to update Notes : {}", notesDTO);
        Notes notes = notesMapper.toEntity(notesDTO);
        notes = notesRepository.save(notes);
        NotesDTO result = notesMapper.toDto(notes);
        notesSearchRepository.index(notes);
        return result;
    }

    /**
     * Partially update a notes.
     *
     * @param notesDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NotesDTO> partialUpdate(NotesDTO notesDTO) {
        log.debug("Request to partially update Notes : {}", notesDTO);

        return notesRepository
            .findById(notesDTO.getId())
            .map(existingNotes -> {
                notesMapper.partialUpdate(existingNotes, notesDTO);

                return existingNotes;
            })
            .map(notesRepository::save)
            .map(savedNotes -> {
                notesSearchRepository.index(savedNotes);
                return savedNotes;
            })
            .map(notesMapper::toDto);
    }

    /**
     * Get all the notes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NotesDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Notes");
        return notesRepository.findAll(pageable).map(notesMapper::toDto);
    }

    /**
     * Get one notes by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NotesDTO> findOne(Long id) {
        log.debug("Request to get Notes : {}", id);
        return notesRepository.findById(id).map(notesMapper::toDto);
    }

    /**
     * Delete the notes by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Notes : {}", id);
        notesRepository.deleteById(id);
        notesSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the notes corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NotesDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Notes for query {}", query);
        return notesSearchRepository.search(query, pageable).map(notesMapper::toDto);
    }
}
