package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.SubscriptionAvailableDay;
import com.sportifyindia.app.repository.SubscriptionAvailableDayRepository;
import com.sportifyindia.app.repository.search.SubscriptionAvailableDaySearchRepository;
import com.sportifyindia.app.service.dto.SubscriptionAvailableDayDTO;
import com.sportifyindia.app.service.mapper.SubscriptionAvailableDayMapper;
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
 * Integration tests for the {@link SubscriptionAvailableDayResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubscriptionAvailableDayResourceIT {

    private static final String DEFAULT_DAYS_OF_WEEK = "AAAAAAAAAA";
    private static final String UPDATED_DAYS_OF_WEEK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/subscription-available-days";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/subscription-available-days/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SubscriptionAvailableDayRepository subscriptionAvailableDayRepository;

    @Autowired
    private SubscriptionAvailableDayMapper subscriptionAvailableDayMapper;

    @Autowired
    private SubscriptionAvailableDaySearchRepository subscriptionAvailableDaySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubscriptionAvailableDayMockMvc;

    private SubscriptionAvailableDay subscriptionAvailableDay;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubscriptionAvailableDay createEntity(EntityManager em) {
        SubscriptionAvailableDay subscriptionAvailableDay = new SubscriptionAvailableDay().daysOfWeek(DEFAULT_DAYS_OF_WEEK);
        return subscriptionAvailableDay;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubscriptionAvailableDay createUpdatedEntity(EntityManager em) {
        SubscriptionAvailableDay subscriptionAvailableDay = new SubscriptionAvailableDay().daysOfWeek(UPDATED_DAYS_OF_WEEK);
        return subscriptionAvailableDay;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        subscriptionAvailableDaySearchRepository.deleteAll();
        assertThat(subscriptionAvailableDaySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        subscriptionAvailableDay = createEntity(em);
    }

    @Test
    @Transactional
    void createSubscriptionAvailableDay() throws Exception {
        int databaseSizeBeforeCreate = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        // Create the SubscriptionAvailableDay
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);
        restSubscriptionAvailableDayMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SubscriptionAvailableDay testSubscriptionAvailableDay = subscriptionAvailableDayList.get(subscriptionAvailableDayList.size() - 1);
        assertThat(testSubscriptionAvailableDay.getDaysOfWeek()).isEqualTo(DEFAULT_DAYS_OF_WEEK);
    }

    @Test
    @Transactional
    void createSubscriptionAvailableDayWithExistingId() throws Exception {
        // Create the SubscriptionAvailableDay with an existing ID
        subscriptionAvailableDay.setId(1L);
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);

        int databaseSizeBeforeCreate = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubscriptionAvailableDayMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDaysOfWeekIsRequired() throws Exception {
        int databaseSizeBeforeTest = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        // set the field null
        subscriptionAvailableDay.setDaysOfWeek(null);

        // Create the SubscriptionAvailableDay, which fails.
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);

        restSubscriptionAvailableDayMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isBadRequest());

        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSubscriptionAvailableDays() throws Exception {
        // Initialize the database
        subscriptionAvailableDayRepository.saveAndFlush(subscriptionAvailableDay);

        // Get all the subscriptionAvailableDayList
        restSubscriptionAvailableDayMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscriptionAvailableDay.getId().intValue())))
            .andExpect(jsonPath("$.[*].daysOfWeek").value(hasItem(DEFAULT_DAYS_OF_WEEK)));
    }

    @Test
    @Transactional
    void getSubscriptionAvailableDay() throws Exception {
        // Initialize the database
        subscriptionAvailableDayRepository.saveAndFlush(subscriptionAvailableDay);

        // Get the subscriptionAvailableDay
        restSubscriptionAvailableDayMockMvc
            .perform(get(ENTITY_API_URL_ID, subscriptionAvailableDay.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subscriptionAvailableDay.getId().intValue()))
            .andExpect(jsonPath("$.daysOfWeek").value(DEFAULT_DAYS_OF_WEEK));
    }

    @Test
    @Transactional
    void getNonExistingSubscriptionAvailableDay() throws Exception {
        // Get the subscriptionAvailableDay
        restSubscriptionAvailableDayMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubscriptionAvailableDay() throws Exception {
        // Initialize the database
        subscriptionAvailableDayRepository.saveAndFlush(subscriptionAvailableDay);

        int databaseSizeBeforeUpdate = subscriptionAvailableDayRepository.findAll().size();
        subscriptionAvailableDaySearchRepository.save(subscriptionAvailableDay);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());

        // Update the subscriptionAvailableDay
        SubscriptionAvailableDay updatedSubscriptionAvailableDay = subscriptionAvailableDayRepository
            .findById(subscriptionAvailableDay.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedSubscriptionAvailableDay are not directly saved in db
        em.detach(updatedSubscriptionAvailableDay);
        updatedSubscriptionAvailableDay.daysOfWeek(UPDATED_DAYS_OF_WEEK);
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(updatedSubscriptionAvailableDay);

        restSubscriptionAvailableDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscriptionAvailableDayDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isOk());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeUpdate);
        SubscriptionAvailableDay testSubscriptionAvailableDay = subscriptionAvailableDayList.get(subscriptionAvailableDayList.size() - 1);
        assertThat(testSubscriptionAvailableDay.getDaysOfWeek()).isEqualTo(UPDATED_DAYS_OF_WEEK);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SubscriptionAvailableDay> subscriptionAvailableDaySearchList = IterableUtils.toList(
                    subscriptionAvailableDaySearchRepository.findAll()
                );
                SubscriptionAvailableDay testSubscriptionAvailableDaySearch = subscriptionAvailableDaySearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testSubscriptionAvailableDaySearch.getDaysOfWeek()).isEqualTo(UPDATED_DAYS_OF_WEEK);
            });
    }

    @Test
    @Transactional
    void putNonExistingSubscriptionAvailableDay() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        subscriptionAvailableDay.setId(longCount.incrementAndGet());

        // Create the SubscriptionAvailableDay
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscriptionAvailableDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscriptionAvailableDayDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubscriptionAvailableDay() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        subscriptionAvailableDay.setId(longCount.incrementAndGet());

        // Create the SubscriptionAvailableDay
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionAvailableDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubscriptionAvailableDay() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        subscriptionAvailableDay.setId(longCount.incrementAndGet());

        // Create the SubscriptionAvailableDay
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionAvailableDayMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSubscriptionAvailableDayWithPatch() throws Exception {
        // Initialize the database
        subscriptionAvailableDayRepository.saveAndFlush(subscriptionAvailableDay);

        int databaseSizeBeforeUpdate = subscriptionAvailableDayRepository.findAll().size();

        // Update the subscriptionAvailableDay using partial update
        SubscriptionAvailableDay partialUpdatedSubscriptionAvailableDay = new SubscriptionAvailableDay();
        partialUpdatedSubscriptionAvailableDay.setId(subscriptionAvailableDay.getId());

        restSubscriptionAvailableDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscriptionAvailableDay.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubscriptionAvailableDay))
            )
            .andExpect(status().isOk());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeUpdate);
        SubscriptionAvailableDay testSubscriptionAvailableDay = subscriptionAvailableDayList.get(subscriptionAvailableDayList.size() - 1);
        assertThat(testSubscriptionAvailableDay.getDaysOfWeek()).isEqualTo(DEFAULT_DAYS_OF_WEEK);
    }

    @Test
    @Transactional
    void fullUpdateSubscriptionAvailableDayWithPatch() throws Exception {
        // Initialize the database
        subscriptionAvailableDayRepository.saveAndFlush(subscriptionAvailableDay);

        int databaseSizeBeforeUpdate = subscriptionAvailableDayRepository.findAll().size();

        // Update the subscriptionAvailableDay using partial update
        SubscriptionAvailableDay partialUpdatedSubscriptionAvailableDay = new SubscriptionAvailableDay();
        partialUpdatedSubscriptionAvailableDay.setId(subscriptionAvailableDay.getId());

        partialUpdatedSubscriptionAvailableDay.daysOfWeek(UPDATED_DAYS_OF_WEEK);

        restSubscriptionAvailableDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscriptionAvailableDay.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubscriptionAvailableDay))
            )
            .andExpect(status().isOk());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeUpdate);
        SubscriptionAvailableDay testSubscriptionAvailableDay = subscriptionAvailableDayList.get(subscriptionAvailableDayList.size() - 1);
        assertThat(testSubscriptionAvailableDay.getDaysOfWeek()).isEqualTo(UPDATED_DAYS_OF_WEEK);
    }

    @Test
    @Transactional
    void patchNonExistingSubscriptionAvailableDay() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        subscriptionAvailableDay.setId(longCount.incrementAndGet());

        // Create the SubscriptionAvailableDay
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscriptionAvailableDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subscriptionAvailableDayDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubscriptionAvailableDay() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        subscriptionAvailableDay.setId(longCount.incrementAndGet());

        // Create the SubscriptionAvailableDay
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionAvailableDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubscriptionAvailableDay() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        subscriptionAvailableDay.setId(longCount.incrementAndGet());

        // Create the SubscriptionAvailableDay
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = subscriptionAvailableDayMapper.toDto(subscriptionAvailableDay);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionAvailableDayMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionAvailableDayDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubscriptionAvailableDay in the database
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSubscriptionAvailableDay() throws Exception {
        // Initialize the database
        subscriptionAvailableDayRepository.saveAndFlush(subscriptionAvailableDay);
        subscriptionAvailableDayRepository.save(subscriptionAvailableDay);
        subscriptionAvailableDaySearchRepository.save(subscriptionAvailableDay);

        int databaseSizeBeforeDelete = subscriptionAvailableDayRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the subscriptionAvailableDay
        restSubscriptionAvailableDayMockMvc
            .perform(delete(ENTITY_API_URL_ID, subscriptionAvailableDay.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SubscriptionAvailableDay> subscriptionAvailableDayList = subscriptionAvailableDayRepository.findAll();
        assertThat(subscriptionAvailableDayList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionAvailableDaySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSubscriptionAvailableDay() throws Exception {
        // Initialize the database
        subscriptionAvailableDay = subscriptionAvailableDayRepository.saveAndFlush(subscriptionAvailableDay);
        subscriptionAvailableDaySearchRepository.save(subscriptionAvailableDay);

        // Search the subscriptionAvailableDay
        restSubscriptionAvailableDayMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + subscriptionAvailableDay.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscriptionAvailableDay.getId().intValue())))
            .andExpect(jsonPath("$.[*].daysOfWeek").value(hasItem(DEFAULT_DAYS_OF_WEEK)));
    }
}
