package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.UtilityAvailableDays;
import com.sportifyindia.app.repository.UtilityAvailableDaysRepository;
import com.sportifyindia.app.repository.search.UtilityAvailableDaysSearchRepository;
import com.sportifyindia.app.service.dto.UtilityAvailableDaysDTO;
import com.sportifyindia.app.service.mapper.UtilityAvailableDaysMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link UtilityAvailableDaysResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UtilityAvailableDaysResourceIT {

    private static final String DEFAULT_DAYS_OF_WEEK = "AAAAAAAAAA";
    private static final String UPDATED_DAYS_OF_WEEK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/utility-available-days";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/utility-available-days/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UtilityAvailableDaysRepository utilityAvailableDaysRepository;

    @Autowired
    private UtilityAvailableDaysMapper utilityAvailableDaysMapper;

    @Autowired
    private UtilityAvailableDaysSearchRepository utilityAvailableDaysSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUtilityAvailableDaysMockMvc;

    private UtilityAvailableDays utilityAvailableDays;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UtilityAvailableDays createEntity(EntityManager em) {
        UtilityAvailableDays utilityAvailableDays = new UtilityAvailableDays().daysOfWeek(DEFAULT_DAYS_OF_WEEK);
        return utilityAvailableDays;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UtilityAvailableDays createUpdatedEntity(EntityManager em) {
        UtilityAvailableDays utilityAvailableDays = new UtilityAvailableDays().daysOfWeek(UPDATED_DAYS_OF_WEEK);
        return utilityAvailableDays;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        utilityAvailableDaysSearchRepository.deleteAll();
        assertThat(utilityAvailableDaysSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        utilityAvailableDays = createEntity(em);
    }

    @Test
    @Transactional
    void createUtilityAvailableDays() throws Exception {
        int databaseSizeBeforeCreate = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        // Create the UtilityAvailableDays
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(utilityAvailableDays);
        restUtilityAvailableDaysMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isCreated());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        UtilityAvailableDays testUtilityAvailableDays = utilityAvailableDaysList.get(utilityAvailableDaysList.size() - 1);
        assertThat(testUtilityAvailableDays.getDaysOfWeek()).isEqualTo(DEFAULT_DAYS_OF_WEEK);
    }

    @Test
    @Transactional
    void createUtilityAvailableDaysWithExistingId() throws Exception {
        // Create the UtilityAvailableDays with an existing ID
        utilityAvailableDays.setId(1L);
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(utilityAvailableDays);

        int databaseSizeBeforeCreate = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUtilityAvailableDaysMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDaysOfWeekIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        // set the field null
        utilityAvailableDays.setDaysOfWeek(null);

        // Create the UtilityAvailableDays, which fails.
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(utilityAvailableDays);

        restUtilityAvailableDaysMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUtilityAvailableDays() throws Exception {
        // Initialize the database
        utilityAvailableDaysRepository.saveAndFlush(utilityAvailableDays);

        // Get all the utilityAvailableDaysList
        restUtilityAvailableDaysMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilityAvailableDays.getId().intValue())))
            .andExpect(jsonPath("$.[*].daysOfWeek").value(hasItem(DEFAULT_DAYS_OF_WEEK)));
    }

    @Test
    @Transactional
    void getUtilityAvailableDays() throws Exception {
        // Initialize the database
        utilityAvailableDaysRepository.saveAndFlush(utilityAvailableDays);

        // Get the utilityAvailableDays
        restUtilityAvailableDaysMockMvc
            .perform(get(ENTITY_API_URL_ID, utilityAvailableDays.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(utilityAvailableDays.getId().intValue()))
            .andExpect(jsonPath("$.daysOfWeek").value(DEFAULT_DAYS_OF_WEEK));
    }

    @Test
    @Transactional
    void getNonExistingUtilityAvailableDays() throws Exception {
        // Get the utilityAvailableDays
        restUtilityAvailableDaysMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUtilityAvailableDays() throws Exception {
        // Initialize the database
        utilityAvailableDaysRepository.saveAndFlush(utilityAvailableDays);

        int databaseSizeBeforeUpdate = utilityAvailableDaysRepository.findAll().size();
        utilityAvailableDaysSearchRepository.save(utilityAvailableDays);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());

        // Update the utilityAvailableDays
        UtilityAvailableDays updatedUtilityAvailableDays = utilityAvailableDaysRepository
            .findById(utilityAvailableDays.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedUtilityAvailableDays are not directly saved in db
        em.detach(updatedUtilityAvailableDays);
        updatedUtilityAvailableDays.daysOfWeek(UPDATED_DAYS_OF_WEEK);
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(updatedUtilityAvailableDays);

        restUtilityAvailableDaysMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilityAvailableDaysDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isOk());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeUpdate);
        UtilityAvailableDays testUtilityAvailableDays = utilityAvailableDaysList.get(utilityAvailableDaysList.size() - 1);
        assertThat(testUtilityAvailableDays.getDaysOfWeek()).isEqualTo(UPDATED_DAYS_OF_WEEK);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UtilityAvailableDays> utilityAvailableDaysSearchList = IterableUtils.toList(
                    utilityAvailableDaysSearchRepository.findAll()
                );
                UtilityAvailableDays testUtilityAvailableDaysSearch = utilityAvailableDaysSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testUtilityAvailableDaysSearch.getDaysOfWeek()).isEqualTo(UPDATED_DAYS_OF_WEEK);
            });
    }

    @Test
    @Transactional
    void putNonExistingUtilityAvailableDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        utilityAvailableDays.setId(longCount.incrementAndGet());

        // Create the UtilityAvailableDays
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(utilityAvailableDays);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilityAvailableDaysMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilityAvailableDaysDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUtilityAvailableDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        utilityAvailableDays.setId(longCount.incrementAndGet());

        // Create the UtilityAvailableDays
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(utilityAvailableDays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityAvailableDaysMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUtilityAvailableDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        utilityAvailableDays.setId(longCount.incrementAndGet());

        // Create the UtilityAvailableDays
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(utilityAvailableDays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityAvailableDaysMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUtilityAvailableDaysWithPatch() throws Exception {
        // Initialize the database
        utilityAvailableDaysRepository.saveAndFlush(utilityAvailableDays);

        int databaseSizeBeforeUpdate = utilityAvailableDaysRepository.findAll().size();

        // Update the utilityAvailableDays using partial update
        UtilityAvailableDays partialUpdatedUtilityAvailableDays = new UtilityAvailableDays();
        partialUpdatedUtilityAvailableDays.setId(utilityAvailableDays.getId());

        restUtilityAvailableDaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtilityAvailableDays.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilityAvailableDays))
            )
            .andExpect(status().isOk());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeUpdate);
        UtilityAvailableDays testUtilityAvailableDays = utilityAvailableDaysList.get(utilityAvailableDaysList.size() - 1);
        assertThat(testUtilityAvailableDays.getDaysOfWeek()).isEqualTo(DEFAULT_DAYS_OF_WEEK);
    }

    @Test
    @Transactional
    void fullUpdateUtilityAvailableDaysWithPatch() throws Exception {
        // Initialize the database
        utilityAvailableDaysRepository.saveAndFlush(utilityAvailableDays);

        int databaseSizeBeforeUpdate = utilityAvailableDaysRepository.findAll().size();

        // Update the utilityAvailableDays using partial update
        UtilityAvailableDays partialUpdatedUtilityAvailableDays = new UtilityAvailableDays();
        partialUpdatedUtilityAvailableDays.setId(utilityAvailableDays.getId());

        partialUpdatedUtilityAvailableDays.daysOfWeek(UPDATED_DAYS_OF_WEEK);

        restUtilityAvailableDaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtilityAvailableDays.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilityAvailableDays))
            )
            .andExpect(status().isOk());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeUpdate);
        UtilityAvailableDays testUtilityAvailableDays = utilityAvailableDaysList.get(utilityAvailableDaysList.size() - 1);
        assertThat(testUtilityAvailableDays.getDaysOfWeek()).isEqualTo(UPDATED_DAYS_OF_WEEK);
    }

    @Test
    @Transactional
    void patchNonExistingUtilityAvailableDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        utilityAvailableDays.setId(longCount.incrementAndGet());

        // Create the UtilityAvailableDays
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(utilityAvailableDays);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilityAvailableDaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, utilityAvailableDaysDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUtilityAvailableDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        utilityAvailableDays.setId(longCount.incrementAndGet());

        // Create the UtilityAvailableDays
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(utilityAvailableDays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityAvailableDaysMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUtilityAvailableDays() throws Exception {
        int databaseSizeBeforeUpdate = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        utilityAvailableDays.setId(longCount.incrementAndGet());

        // Create the UtilityAvailableDays
        UtilityAvailableDaysDTO utilityAvailableDaysDTO = utilityAvailableDaysMapper.toDto(utilityAvailableDays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityAvailableDaysMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityAvailableDaysDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UtilityAvailableDays in the database
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUtilityAvailableDays() throws Exception {
        // Initialize the database
        utilityAvailableDaysRepository.saveAndFlush(utilityAvailableDays);
        utilityAvailableDaysRepository.save(utilityAvailableDays);
        utilityAvailableDaysSearchRepository.save(utilityAvailableDays);

        int databaseSizeBeforeDelete = utilityAvailableDaysRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the utilityAvailableDays
        restUtilityAvailableDaysMockMvc
            .perform(delete(ENTITY_API_URL_ID, utilityAvailableDays.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UtilityAvailableDays> utilityAvailableDaysList = utilityAvailableDaysRepository.findAll();
        assertThat(utilityAvailableDaysList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityAvailableDaysSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUtilityAvailableDays() throws Exception {
        // Initialize the database
        utilityAvailableDays = utilityAvailableDaysRepository.saveAndFlush(utilityAvailableDays);
        utilityAvailableDaysSearchRepository.save(utilityAvailableDays);

        // Search the utilityAvailableDays
        restUtilityAvailableDaysMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + utilityAvailableDays.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilityAvailableDays.getId().intValue())))
            .andExpect(jsonPath("$.[*].daysOfWeek").value(hasItem(DEFAULT_DAYS_OF_WEEK)));
    }
}
