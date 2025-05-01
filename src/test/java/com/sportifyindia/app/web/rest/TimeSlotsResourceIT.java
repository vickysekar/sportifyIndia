package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.repository.TimeSlotsRepository;
import com.sportifyindia.app.repository.search.TimeSlotsSearchRepository;
import com.sportifyindia.app.service.dto.TimeSlotsDTO;
import com.sportifyindia.app.service.mapper.TimeSlotsMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TimeSlotsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TimeSlotsResourceIT {

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/time-slots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/time-slots/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TimeSlotsRepository timeSlotsRepository;

    @Autowired
    private TimeSlotsMapper timeSlotsMapper;

    @Autowired
    private TimeSlotsSearchRepository timeSlotsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTimeSlotsMockMvc;

    private TimeSlots timeSlots;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeSlots createEntity(EntityManager em) {
        TimeSlots timeSlots = new TimeSlots().startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME);
        return timeSlots;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TimeSlots createUpdatedEntity(EntityManager em) {
        TimeSlots timeSlots = new TimeSlots().startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
        return timeSlots;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        timeSlotsSearchRepository.deleteAll();
        assertThat(timeSlotsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        timeSlots = createEntity(em);
    }

    @Test
    @Transactional
    void createTimeSlots() throws Exception {
        int databaseSizeBeforeCreate = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        // Create the TimeSlots
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);
        restTimeSlotsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO)))
            .andExpect(status().isCreated());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TimeSlots testTimeSlots = timeSlotsList.get(timeSlotsList.size() - 1);
        assertThat(testTimeSlots.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testTimeSlots.getEndTime()).isEqualTo(DEFAULT_END_TIME);
    }

    @Test
    @Transactional
    void createTimeSlotsWithExistingId() throws Exception {
        // Create the TimeSlots with an existing ID
        timeSlots.setId(1L);
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);

        int databaseSizeBeforeCreate = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTimeSlotsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        // set the field null
        timeSlots.setStartTime(null);

        // Create the TimeSlots, which fails.
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);

        restTimeSlotsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO)))
            .andExpect(status().isBadRequest());

        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        // set the field null
        timeSlots.setEndTime(null);

        // Create the TimeSlots, which fails.
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);

        restTimeSlotsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO)))
            .andExpect(status().isBadRequest());

        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTimeSlots() throws Exception {
        // Initialize the database
        timeSlotsRepository.saveAndFlush(timeSlots);

        // Get all the timeSlotsList
        restTimeSlotsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeSlots.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }

    @Test
    @Transactional
    void getTimeSlots() throws Exception {
        // Initialize the database
        timeSlotsRepository.saveAndFlush(timeSlots);

        // Get the timeSlots
        restTimeSlotsMockMvc
            .perform(get(ENTITY_API_URL_ID, timeSlots.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(timeSlots.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTimeSlots() throws Exception {
        // Get the timeSlots
        restTimeSlotsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTimeSlots() throws Exception {
        // Initialize the database
        timeSlotsRepository.saveAndFlush(timeSlots);

        int databaseSizeBeforeUpdate = timeSlotsRepository.findAll().size();
        timeSlotsSearchRepository.save(timeSlots);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());

        // Update the timeSlots
        TimeSlots updatedTimeSlots = timeSlotsRepository.findById(timeSlots.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTimeSlots are not directly saved in db
        em.detach(updatedTimeSlots);
        updatedTimeSlots.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(updatedTimeSlots);

        restTimeSlotsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeSlotsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO))
            )
            .andExpect(status().isOk());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeUpdate);
        TimeSlots testTimeSlots = timeSlotsList.get(timeSlotsList.size() - 1);
        assertThat(testTimeSlots.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testTimeSlots.getEndTime()).isEqualTo(UPDATED_END_TIME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TimeSlots> timeSlotsSearchList = IterableUtils.toList(timeSlotsSearchRepository.findAll());
                TimeSlots testTimeSlotsSearch = timeSlotsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTimeSlotsSearch.getStartTime()).isEqualTo(UPDATED_START_TIME);
                assertThat(testTimeSlotsSearch.getEndTime()).isEqualTo(UPDATED_END_TIME);
            });
    }

    @Test
    @Transactional
    void putNonExistingTimeSlots() throws Exception {
        int databaseSizeBeforeUpdate = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        timeSlots.setId(longCount.incrementAndGet());

        // Create the TimeSlots
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeSlotsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, timeSlotsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTimeSlots() throws Exception {
        int databaseSizeBeforeUpdate = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        timeSlots.setId(longCount.incrementAndGet());

        // Create the TimeSlots
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeSlotsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTimeSlots() throws Exception {
        int databaseSizeBeforeUpdate = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        timeSlots.setId(longCount.incrementAndGet());

        // Create the TimeSlots
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeSlotsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTimeSlotsWithPatch() throws Exception {
        // Initialize the database
        timeSlotsRepository.saveAndFlush(timeSlots);

        int databaseSizeBeforeUpdate = timeSlotsRepository.findAll().size();

        // Update the timeSlots using partial update
        TimeSlots partialUpdatedTimeSlots = new TimeSlots();
        partialUpdatedTimeSlots.setId(timeSlots.getId());

        partialUpdatedTimeSlots.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restTimeSlotsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeSlots.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimeSlots))
            )
            .andExpect(status().isOk());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeUpdate);
        TimeSlots testTimeSlots = timeSlotsList.get(timeSlotsList.size() - 1);
        assertThat(testTimeSlots.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testTimeSlots.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void fullUpdateTimeSlotsWithPatch() throws Exception {
        // Initialize the database
        timeSlotsRepository.saveAndFlush(timeSlots);

        int databaseSizeBeforeUpdate = timeSlotsRepository.findAll().size();

        // Update the timeSlots using partial update
        TimeSlots partialUpdatedTimeSlots = new TimeSlots();
        partialUpdatedTimeSlots.setId(timeSlots.getId());

        partialUpdatedTimeSlots.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restTimeSlotsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTimeSlots.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTimeSlots))
            )
            .andExpect(status().isOk());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeUpdate);
        TimeSlots testTimeSlots = timeSlotsList.get(timeSlotsList.size() - 1);
        assertThat(testTimeSlots.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testTimeSlots.getEndTime()).isEqualTo(UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void patchNonExistingTimeSlots() throws Exception {
        int databaseSizeBeforeUpdate = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        timeSlots.setId(longCount.incrementAndGet());

        // Create the TimeSlots
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTimeSlotsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, timeSlotsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTimeSlots() throws Exception {
        int databaseSizeBeforeUpdate = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        timeSlots.setId(longCount.incrementAndGet());

        // Create the TimeSlots
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeSlotsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTimeSlots() throws Exception {
        int databaseSizeBeforeUpdate = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        timeSlots.setId(longCount.incrementAndGet());

        // Create the TimeSlots
        TimeSlotsDTO timeSlotsDTO = timeSlotsMapper.toDto(timeSlots);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTimeSlotsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(timeSlotsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TimeSlots in the database
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTimeSlots() throws Exception {
        // Initialize the database
        timeSlotsRepository.saveAndFlush(timeSlots);
        timeSlotsRepository.save(timeSlots);
        timeSlotsSearchRepository.save(timeSlots);

        int databaseSizeBeforeDelete = timeSlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the timeSlots
        restTimeSlotsMockMvc
            .perform(delete(ENTITY_API_URL_ID, timeSlots.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TimeSlots> timeSlotsList = timeSlotsRepository.findAll();
        assertThat(timeSlotsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(timeSlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTimeSlots() throws Exception {
        // Initialize the database
        timeSlots = timeSlotsRepository.saveAndFlush(timeSlots);
        timeSlotsSearchRepository.save(timeSlots);

        // Search the timeSlots
        restTimeSlotsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + timeSlots.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(timeSlots.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }
}
