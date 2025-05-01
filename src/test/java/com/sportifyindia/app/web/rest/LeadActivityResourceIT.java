package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.LeadActivity;
import com.sportifyindia.app.repository.LeadActivityRepository;
import com.sportifyindia.app.repository.search.LeadActivitySearchRepository;
import com.sportifyindia.app.service.dto.LeadActivityDTO;
import com.sportifyindia.app.service.mapper.LeadActivityMapper;
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
 * Integration tests for the {@link LeadActivityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LeadActivityResourceIT {

    private static final String DEFAULT_ACTIVITY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVITY_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/lead-activities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/lead-activities/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LeadActivityRepository leadActivityRepository;

    @Autowired
    private LeadActivityMapper leadActivityMapper;

    @Autowired
    private LeadActivitySearchRepository leadActivitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLeadActivityMockMvc;

    private LeadActivity leadActivity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LeadActivity createEntity(EntityManager em) {
        LeadActivity leadActivity = new LeadActivity().activityType(DEFAULT_ACTIVITY_TYPE).description(DEFAULT_DESCRIPTION);
        return leadActivity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LeadActivity createUpdatedEntity(EntityManager em) {
        LeadActivity leadActivity = new LeadActivity().activityType(UPDATED_ACTIVITY_TYPE).description(UPDATED_DESCRIPTION);
        return leadActivity;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        leadActivitySearchRepository.deleteAll();
        assertThat(leadActivitySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        leadActivity = createEntity(em);
    }

    @Test
    @Transactional
    void createLeadActivity() throws Exception {
        int databaseSizeBeforeCreate = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        // Create the LeadActivity
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(leadActivity);
        restLeadActivityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isCreated());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        LeadActivity testLeadActivity = leadActivityList.get(leadActivityList.size() - 1);
        assertThat(testLeadActivity.getActivityType()).isEqualTo(DEFAULT_ACTIVITY_TYPE);
        assertThat(testLeadActivity.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createLeadActivityWithExistingId() throws Exception {
        // Create the LeadActivity with an existing ID
        leadActivity.setId(1L);
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(leadActivity);

        int databaseSizeBeforeCreate = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeadActivityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkActivityTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        // set the field null
        leadActivity.setActivityType(null);

        // Create the LeadActivity, which fails.
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(leadActivity);

        restLeadActivityMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isBadRequest());

        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllLeadActivities() throws Exception {
        // Initialize the database
        leadActivityRepository.saveAndFlush(leadActivity);

        // Get all the leadActivityList
        restLeadActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(leadActivity.getId().intValue())))
            .andExpect(jsonPath("$.[*].activityType").value(hasItem(DEFAULT_ACTIVITY_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getLeadActivity() throws Exception {
        // Initialize the database
        leadActivityRepository.saveAndFlush(leadActivity);

        // Get the leadActivity
        restLeadActivityMockMvc
            .perform(get(ENTITY_API_URL_ID, leadActivity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(leadActivity.getId().intValue()))
            .andExpect(jsonPath("$.activityType").value(DEFAULT_ACTIVITY_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingLeadActivity() throws Exception {
        // Get the leadActivity
        restLeadActivityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLeadActivity() throws Exception {
        // Initialize the database
        leadActivityRepository.saveAndFlush(leadActivity);

        int databaseSizeBeforeUpdate = leadActivityRepository.findAll().size();
        leadActivitySearchRepository.save(leadActivity);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());

        // Update the leadActivity
        LeadActivity updatedLeadActivity = leadActivityRepository.findById(leadActivity.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLeadActivity are not directly saved in db
        em.detach(updatedLeadActivity);
        updatedLeadActivity.activityType(UPDATED_ACTIVITY_TYPE).description(UPDATED_DESCRIPTION);
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(updatedLeadActivity);

        restLeadActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, leadActivityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isOk());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeUpdate);
        LeadActivity testLeadActivity = leadActivityList.get(leadActivityList.size() - 1);
        assertThat(testLeadActivity.getActivityType()).isEqualTo(UPDATED_ACTIVITY_TYPE);
        assertThat(testLeadActivity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<LeadActivity> leadActivitySearchList = IterableUtils.toList(leadActivitySearchRepository.findAll());
                LeadActivity testLeadActivitySearch = leadActivitySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testLeadActivitySearch.getActivityType()).isEqualTo(UPDATED_ACTIVITY_TYPE);
                assertThat(testLeadActivitySearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingLeadActivity() throws Exception {
        int databaseSizeBeforeUpdate = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        leadActivity.setId(longCount.incrementAndGet());

        // Create the LeadActivity
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(leadActivity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeadActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, leadActivityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchLeadActivity() throws Exception {
        int databaseSizeBeforeUpdate = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        leadActivity.setId(longCount.incrementAndGet());

        // Create the LeadActivity
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(leadActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeadActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLeadActivity() throws Exception {
        int databaseSizeBeforeUpdate = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        leadActivity.setId(longCount.incrementAndGet());

        // Create the LeadActivity
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(leadActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeadActivityMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateLeadActivityWithPatch() throws Exception {
        // Initialize the database
        leadActivityRepository.saveAndFlush(leadActivity);

        int databaseSizeBeforeUpdate = leadActivityRepository.findAll().size();

        // Update the leadActivity using partial update
        LeadActivity partialUpdatedLeadActivity = new LeadActivity();
        partialUpdatedLeadActivity.setId(leadActivity.getId());

        restLeadActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeadActivity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLeadActivity))
            )
            .andExpect(status().isOk());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeUpdate);
        LeadActivity testLeadActivity = leadActivityList.get(leadActivityList.size() - 1);
        assertThat(testLeadActivity.getActivityType()).isEqualTo(DEFAULT_ACTIVITY_TYPE);
        assertThat(testLeadActivity.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateLeadActivityWithPatch() throws Exception {
        // Initialize the database
        leadActivityRepository.saveAndFlush(leadActivity);

        int databaseSizeBeforeUpdate = leadActivityRepository.findAll().size();

        // Update the leadActivity using partial update
        LeadActivity partialUpdatedLeadActivity = new LeadActivity();
        partialUpdatedLeadActivity.setId(leadActivity.getId());

        partialUpdatedLeadActivity.activityType(UPDATED_ACTIVITY_TYPE).description(UPDATED_DESCRIPTION);

        restLeadActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeadActivity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLeadActivity))
            )
            .andExpect(status().isOk());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeUpdate);
        LeadActivity testLeadActivity = leadActivityList.get(leadActivityList.size() - 1);
        assertThat(testLeadActivity.getActivityType()).isEqualTo(UPDATED_ACTIVITY_TYPE);
        assertThat(testLeadActivity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingLeadActivity() throws Exception {
        int databaseSizeBeforeUpdate = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        leadActivity.setId(longCount.incrementAndGet());

        // Create the LeadActivity
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(leadActivity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeadActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, leadActivityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLeadActivity() throws Exception {
        int databaseSizeBeforeUpdate = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        leadActivity.setId(longCount.incrementAndGet());

        // Create the LeadActivity
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(leadActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeadActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLeadActivity() throws Exception {
        int databaseSizeBeforeUpdate = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        leadActivity.setId(longCount.incrementAndGet());

        // Create the LeadActivity
        LeadActivityDTO leadActivityDTO = leadActivityMapper.toDto(leadActivity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeadActivityMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(leadActivityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LeadActivity in the database
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteLeadActivity() throws Exception {
        // Initialize the database
        leadActivityRepository.saveAndFlush(leadActivity);
        leadActivityRepository.save(leadActivity);
        leadActivitySearchRepository.save(leadActivity);

        int databaseSizeBeforeDelete = leadActivityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the leadActivity
        restLeadActivityMockMvc
            .perform(delete(ENTITY_API_URL_ID, leadActivity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LeadActivity> leadActivityList = leadActivityRepository.findAll();
        assertThat(leadActivityList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(leadActivitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchLeadActivity() throws Exception {
        // Initialize the database
        leadActivity = leadActivityRepository.saveAndFlush(leadActivity);
        leadActivitySearchRepository.save(leadActivity);

        // Search the leadActivity
        restLeadActivityMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + leadActivity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(leadActivity.getId().intValue())))
            .andExpect(jsonPath("$.[*].activityType").value(hasItem(DEFAULT_ACTIVITY_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
