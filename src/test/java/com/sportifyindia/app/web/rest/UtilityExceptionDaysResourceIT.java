package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.UtilityExceptionDays;
import com.sportifyindia.app.repository.UtilityExceptionDaysRepository;
import com.sportifyindia.app.repository.search.UtilityExceptionDaysSearchRepository;
import com.sportifyindia.app.service.dto.UtilityExceptionDaysDTO;
import com.sportifyindia.app.service.mapper.UtilityExceptionDaysMapper;
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
 * Integration tests for the {@link UtilityExceptionDaysResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UtilityExceptionDaysResourceIT {

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NESTED_TIMESLOTS = "AAAAAAAAAA";
    private static final String UPDATED_NESTED_TIMESLOTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/utility-exception-days";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/utility-exception-days/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UtilityExceptionDaysRepository utilityExceptionDaysRepository;

    @Autowired
    private UtilityExceptionDaysMapper utilityExceptionDaysMapper;

    @Autowired
    private UtilityExceptionDaysSearchRepository utilityExceptionDaysSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUtilityExceptionDaysMockMvc;

    private UtilityExceptionDays utilityExceptionDays;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UtilityExceptionDays createEntity(EntityManager em) {
        UtilityExceptionDays utilityExceptionDays = new UtilityExceptionDays()
            .reason(DEFAULT_REASON)
            .date(DEFAULT_DATE)
            .nestedTimeslots(DEFAULT_NESTED_TIMESLOTS);
        return utilityExceptionDays;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UtilityExceptionDays createUpdatedEntity(EntityManager em) {
        UtilityExceptionDays utilityExceptionDays = new UtilityExceptionDays()
            .reason(UPDATED_REASON)
            .date(UPDATED_DATE)
            .nestedTimeslots(UPDATED_NESTED_TIMESLOTS);
        return utilityExceptionDays;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        utilityExceptionDaysSearchRepository.deleteAll();
        assertThat(utilityExceptionDaysSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        utilityExceptionDays = createEntity(em);
    }

    @Test
    @Transactional
    void createUtilityExceptionDays() throws Exception {
        int databaseSizeBeforeCreate = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        // Create the UtilityExceptionDays
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);
        restUtilityExceptionDaysMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isCreated());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        UtilityExceptionDays testUtilityExceptionDays = utilityExceptionDaysList.get(utilityExceptionDaysList.size() - 1);
        assertThat(testUtilityExceptionDays.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testUtilityExceptionDays.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testUtilityExceptionDays.getNestedTimeslots()).isEqualTo(DEFAULT_NESTED_TIMESLOTS);
    }

    @Test
    @Transactional
    void createUtilityExceptionDaysWithExistingId() throws Exception {
        // Create the UtilityExceptionDays with an existing ID
        utilityExceptionDays.setId(1L);
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);

        int databaseSizeBeforeCreate = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUtilityExceptionDaysMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        // set the field null
        utilityExceptionDays.setReason(null);

        // Create the UtilityExceptionDays, which fails.
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);

        restUtilityExceptionDaysMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        // set the field null
        utilityExceptionDays.setDate(null);

        // Create the UtilityExceptionDays, which fails.
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);

        restUtilityExceptionDaysMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUtilityExceptionDays() throws Exception {
        // Initialize the database
        utilityExceptionDaysRepository.saveAndFlush(utilityExceptionDays);

        // Get all the utilityExceptionDaysList
        restUtilityExceptionDaysMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilityExceptionDays.getId().intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].nestedTimeslots").value(hasItem(DEFAULT_NESTED_TIMESLOTS)));
    }

    @Test
    @Transactional
    void getUtilityExceptionDays() throws Exception {
        // Initialize the database
        utilityExceptionDaysRepository.saveAndFlush(utilityExceptionDays);

        // Get the utilityExceptionDays
        restUtilityExceptionDaysMockMvc
            .perform(get(ENTITY_API_URL_ID, utilityExceptionDays.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(utilityExceptionDays.getId().intValue()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.nestedTimeslots").value(DEFAULT_NESTED_TIMESLOTS));
    }

    @Test
    @Transactional
    void getNonExistingUtilityExceptionDays() throws Exception {
        // Get the utilityExceptionDays
        restUtilityExceptionDaysMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUtilityExceptionDays() throws Exception {
        // Initialize the database
        utilityExceptionDaysRepository.saveAndFlush(utilityExceptionDays);

        int databaseSizeBeforeUpdate = utilityExceptionDaysRepository.findAll().size();
        utilityExceptionDaysSearchRepository.save(utilityExceptionDays);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());

        // Update the utilityExceptionDays
        UtilityExceptionDays updatedUtilityExceptionDays = utilityExceptionDaysRepository
            .findById(utilityExceptionDays.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedUtilityExceptionDays are not directly saved in db
        em.detach(updatedUtilityExceptionDays);
        updatedUtilityExceptionDays.reason(UPDATED_REASON).date(UPDATED_DATE).nestedTimeslots(UPDATED_NESTED_TIMESLOTS);
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(updatedUtilityExceptionDays);

        restUtilityExceptionDaysMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilityExceptionDaysDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isOk());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeUpdate);
        UtilityExceptionDays testUtilityExceptionDays = utilityExceptionDaysList.get(utilityExceptionDaysList.size() - 1);
        assertThat(testUtilityExceptionDays.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testUtilityExceptionDays.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testUtilityExceptionDays.getNestedTimeslots()).isEqualTo(UPDATED_NESTED_TIMESLOTS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UtilityExceptionDays> utilityExceptionDaysSearchList = IterableUtils.toList(
                    utilityExceptionDaysSearchRepository.findAll()
                );
                UtilityExceptionDays testUtilityExceptionDaysSearch = utilityExceptionDaysSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testUtilityExceptionDaysSearch.getReason()).isEqualTo(UPDATED_REASON);
                assertThat(testUtilityExceptionDaysSearch.getDate()).isEqualTo(UPDATED_DATE);
                assertThat(testUtilityExceptionDaysSearch.getNestedTimeslots()).isEqualTo(UPDATED_NESTED_TIMESLOTS);
            });
    }

    @Test
    @Transactional
    void putNonExistingUtilityExceptionDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        utilityExceptionDays.setId(longCount.incrementAndGet());

        // Create the UtilityExceptionDays
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilityExceptionDaysMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilityExceptionDaysDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUtilityExceptionDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        utilityExceptionDays.setId(longCount.incrementAndGet());

        // Create the UtilityExceptionDays
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityExceptionDaysMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUtilityExceptionDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        utilityExceptionDays.setId(longCount.incrementAndGet());

        // Create the UtilityExceptionDays
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityExceptionDaysMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUtilityExceptionDaysWithPatch() throws Exception {
        // Initialize the database
        utilityExceptionDaysRepository.saveAndFlush(utilityExceptionDays);

        int databaseSizeBeforeUpdate = utilityExceptionDaysRepository.findAll().size();

        // Update the utilityExceptionDays using partial update
        UtilityExceptionDays partialUpdatedUtilityExceptionDays = new UtilityExceptionDays();
        partialUpdatedUtilityExceptionDays.setId(utilityExceptionDays.getId());

        partialUpdatedUtilityExceptionDays.date(UPDATED_DATE);

        restUtilityExceptionDaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtilityExceptionDays.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilityExceptionDays))
            )
            .andExpect(status().isOk());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeUpdate);
        UtilityExceptionDays testUtilityExceptionDays = utilityExceptionDaysList.get(utilityExceptionDaysList.size() - 1);
        assertThat(testUtilityExceptionDays.getReason()).isEqualTo(DEFAULT_REASON);
        assertThat(testUtilityExceptionDays.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testUtilityExceptionDays.getNestedTimeslots()).isEqualTo(DEFAULT_NESTED_TIMESLOTS);
    }

    @Test
    @Transactional
    void fullUpdateUtilityExceptionDaysWithPatch() throws Exception {
        // Initialize the database
        utilityExceptionDaysRepository.saveAndFlush(utilityExceptionDays);

        int databaseSizeBeforeUpdate = utilityExceptionDaysRepository.findAll().size();

        // Update the utilityExceptionDays using partial update
        UtilityExceptionDays partialUpdatedUtilityExceptionDays = new UtilityExceptionDays();
        partialUpdatedUtilityExceptionDays.setId(utilityExceptionDays.getId());

        partialUpdatedUtilityExceptionDays.reason(UPDATED_REASON).date(UPDATED_DATE).nestedTimeslots(UPDATED_NESTED_TIMESLOTS);

        restUtilityExceptionDaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtilityExceptionDays.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilityExceptionDays))
            )
            .andExpect(status().isOk());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeUpdate);
        UtilityExceptionDays testUtilityExceptionDays = utilityExceptionDaysList.get(utilityExceptionDaysList.size() - 1);
        assertThat(testUtilityExceptionDays.getReason()).isEqualTo(UPDATED_REASON);
        assertThat(testUtilityExceptionDays.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testUtilityExceptionDays.getNestedTimeslots()).isEqualTo(UPDATED_NESTED_TIMESLOTS);
    }

    @Test
    @Transactional
    void patchNonExistingUtilityExceptionDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        utilityExceptionDays.setId(longCount.incrementAndGet());

        // Create the UtilityExceptionDays
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilityExceptionDaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, utilityExceptionDaysDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUtilityExceptionDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        utilityExceptionDays.setId(longCount.incrementAndGet());

        // Create the UtilityExceptionDays
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityExceptionDaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUtilityExceptionDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        utilityExceptionDays.setId(longCount.incrementAndGet());

        // Create the UtilityExceptionDays
        UtilityExceptionDaysDTO utilityExceptionDaysDTO = utilityExceptionDaysMapper.toDto(utilityExceptionDays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityExceptionDaysMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityExceptionDaysDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UtilityExceptionDays in the database
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUtilityExceptionDays() throws Exception {
        // Initialize the database
        utilityExceptionDaysRepository.saveAndFlush(utilityExceptionDays);
        utilityExceptionDaysRepository.save(utilityExceptionDays);
        utilityExceptionDaysSearchRepository.save(utilityExceptionDays);

        int databaseSizeBeforeDelete = utilityExceptionDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the utilityExceptionDays
        restUtilityExceptionDaysMockMvc
            .perform(delete(ENTITY_API_URL_ID, utilityExceptionDays.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UtilityExceptionDays> utilityExceptionDaysList = utilityExceptionDaysRepository.findAll();
        assertThat(utilityExceptionDaysList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityExceptionDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUtilityExceptionDays() throws Exception {
        // Initialize the database
        utilityExceptionDays = utilityExceptionDaysRepository.saveAndFlush(utilityExceptionDays);
        utilityExceptionDaysSearchRepository.save(utilityExceptionDays);

        // Search the utilityExceptionDays
        restUtilityExceptionDaysMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + utilityExceptionDays.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilityExceptionDays.getId().intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].nestedTimeslots").value(hasItem(DEFAULT_NESTED_TIMESLOTS)));
    }
}
