package com.sportifyindia.app.web.rest;

import static com.sportifyindia.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.OneTimeEventSubscribers;
import com.sportifyindia.app.domain.enumeration.SubscriptionStatusEnum;
import com.sportifyindia.app.repository.OneTimeEventSubscribersRepository;
import com.sportifyindia.app.repository.search.OneTimeEventSubscribersSearchRepository;
import com.sportifyindia.app.service.dto.OneTimeEventSubscribersDTO;
import com.sportifyindia.app.service.mapper.OneTimeEventSubscribersMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link OneTimeEventSubscribersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OneTimeEventSubscribersResourceIT {

    private static final BigDecimal DEFAULT_PAID_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PAID_AMOUNT = new BigDecimal(2);

    private static final SubscriptionStatusEnum DEFAULT_STATUS = SubscriptionStatusEnum.PENDING;
    private static final SubscriptionStatusEnum UPDATED_STATUS = SubscriptionStatusEnum.CONFIRMED;

    private static final String ENTITY_API_URL = "/api/one-time-event-subscribers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/one-time-event-subscribers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OneTimeEventSubscribersRepository oneTimeEventSubscribersRepository;

    @Autowired
    private OneTimeEventSubscribersMapper oneTimeEventSubscribersMapper;

    @Autowired
    private OneTimeEventSubscribersSearchRepository oneTimeEventSubscribersSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOneTimeEventSubscribersMockMvc;

    private OneTimeEventSubscribers oneTimeEventSubscribers;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OneTimeEventSubscribers createEntity(EntityManager em) {
        OneTimeEventSubscribers oneTimeEventSubscribers = new OneTimeEventSubscribers()
            .paidAmount(DEFAULT_PAID_AMOUNT)
            .status(DEFAULT_STATUS);
        return oneTimeEventSubscribers;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OneTimeEventSubscribers createUpdatedEntity(EntityManager em) {
        OneTimeEventSubscribers oneTimeEventSubscribers = new OneTimeEventSubscribers()
            .paidAmount(UPDATED_PAID_AMOUNT)
            .status(UPDATED_STATUS);
        return oneTimeEventSubscribers;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        oneTimeEventSubscribersSearchRepository.deleteAll();
        assertThat(oneTimeEventSubscribersSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        oneTimeEventSubscribers = createEntity(em);
    }

    @Test
    @Transactional
    void createOneTimeEventSubscribers() throws Exception {
        int databaseSizeBeforeCreate = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        // Create the OneTimeEventSubscribers
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);
        restOneTimeEventSubscribersMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isCreated());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        OneTimeEventSubscribers testOneTimeEventSubscribers = oneTimeEventSubscribersList.get(oneTimeEventSubscribersList.size() - 1);
        assertThat(testOneTimeEventSubscribers.getPaidAmount()).isEqualByComparingTo(DEFAULT_PAID_AMOUNT);
        assertThat(testOneTimeEventSubscribers.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createOneTimeEventSubscribersWithExistingId() throws Exception {
        // Create the OneTimeEventSubscribers with an existing ID
        oneTimeEventSubscribers.setId(1L);
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);

        int databaseSizeBeforeCreate = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOneTimeEventSubscribersMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPaidAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        // set the field null
        oneTimeEventSubscribers.setPaidAmount(null);

        // Create the OneTimeEventSubscribers, which fails.
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);

        restOneTimeEventSubscribersMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        // set the field null
        oneTimeEventSubscribers.setStatus(null);

        // Create the OneTimeEventSubscribers, which fails.
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);

        restOneTimeEventSubscribersMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOneTimeEventSubscribers() throws Exception {
        // Initialize the database
        oneTimeEventSubscribersRepository.saveAndFlush(oneTimeEventSubscribers);

        // Get all the oneTimeEventSubscribersList
        restOneTimeEventSubscribersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oneTimeEventSubscribers.getId().intValue())))
            .andExpect(jsonPath("$.[*].paidAmount").value(hasItem(sameNumber(DEFAULT_PAID_AMOUNT))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getOneTimeEventSubscribers() throws Exception {
        // Initialize the database
        oneTimeEventSubscribersRepository.saveAndFlush(oneTimeEventSubscribers);

        // Get the oneTimeEventSubscribers
        restOneTimeEventSubscribersMockMvc
            .perform(get(ENTITY_API_URL_ID, oneTimeEventSubscribers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(oneTimeEventSubscribers.getId().intValue()))
            .andExpect(jsonPath("$.paidAmount").value(sameNumber(DEFAULT_PAID_AMOUNT)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingOneTimeEventSubscribers() throws Exception {
        // Get the oneTimeEventSubscribers
        restOneTimeEventSubscribersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOneTimeEventSubscribers() throws Exception {
        // Initialize the database
        oneTimeEventSubscribersRepository.saveAndFlush(oneTimeEventSubscribers);

        int databaseSizeBeforeUpdate = oneTimeEventSubscribersRepository.findAll().size();
        oneTimeEventSubscribersSearchRepository.save(oneTimeEventSubscribers);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());

        // Update the oneTimeEventSubscribers
        OneTimeEventSubscribers updatedOneTimeEventSubscribers = oneTimeEventSubscribersRepository
            .findById(oneTimeEventSubscribers.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedOneTimeEventSubscribers are not directly saved in db
        em.detach(updatedOneTimeEventSubscribers);
        updatedOneTimeEventSubscribers.paidAmount(UPDATED_PAID_AMOUNT).status(UPDATED_STATUS);
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(updatedOneTimeEventSubscribers);

        restOneTimeEventSubscribersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, oneTimeEventSubscribersDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isOk());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeUpdate);
        OneTimeEventSubscribers testOneTimeEventSubscribers = oneTimeEventSubscribersList.get(oneTimeEventSubscribersList.size() - 1);
        assertThat(testOneTimeEventSubscribers.getPaidAmount()).isEqualByComparingTo(UPDATED_PAID_AMOUNT);
        assertThat(testOneTimeEventSubscribers.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<OneTimeEventSubscribers> oneTimeEventSubscribersSearchList = IterableUtils.toList(
                    oneTimeEventSubscribersSearchRepository.findAll()
                );
                OneTimeEventSubscribers testOneTimeEventSubscribersSearch = oneTimeEventSubscribersSearchList.get(
                    searchDatabaseSizeAfter - 1
                );
                assertThat(testOneTimeEventSubscribersSearch.getPaidAmount()).isEqualByComparingTo(UPDATED_PAID_AMOUNT);
                assertThat(testOneTimeEventSubscribersSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingOneTimeEventSubscribers() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        oneTimeEventSubscribers.setId(longCount.incrementAndGet());

        // Create the OneTimeEventSubscribers
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOneTimeEventSubscribersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, oneTimeEventSubscribersDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOneTimeEventSubscribers() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        oneTimeEventSubscribers.setId(longCount.incrementAndGet());

        // Create the OneTimeEventSubscribers
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOneTimeEventSubscribersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOneTimeEventSubscribers() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        oneTimeEventSubscribers.setId(longCount.incrementAndGet());

        // Create the OneTimeEventSubscribers
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOneTimeEventSubscribersMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOneTimeEventSubscribersWithPatch() throws Exception {
        // Initialize the database
        oneTimeEventSubscribersRepository.saveAndFlush(oneTimeEventSubscribers);

        int databaseSizeBeforeUpdate = oneTimeEventSubscribersRepository.findAll().size();

        // Update the oneTimeEventSubscribers using partial update
        OneTimeEventSubscribers partialUpdatedOneTimeEventSubscribers = new OneTimeEventSubscribers();
        partialUpdatedOneTimeEventSubscribers.setId(oneTimeEventSubscribers.getId());

        partialUpdatedOneTimeEventSubscribers.paidAmount(UPDATED_PAID_AMOUNT);

        restOneTimeEventSubscribersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOneTimeEventSubscribers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOneTimeEventSubscribers))
            )
            .andExpect(status().isOk());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeUpdate);
        OneTimeEventSubscribers testOneTimeEventSubscribers = oneTimeEventSubscribersList.get(oneTimeEventSubscribersList.size() - 1);
        assertThat(testOneTimeEventSubscribers.getPaidAmount()).isEqualByComparingTo(UPDATED_PAID_AMOUNT);
        assertThat(testOneTimeEventSubscribers.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateOneTimeEventSubscribersWithPatch() throws Exception {
        // Initialize the database
        oneTimeEventSubscribersRepository.saveAndFlush(oneTimeEventSubscribers);

        int databaseSizeBeforeUpdate = oneTimeEventSubscribersRepository.findAll().size();

        // Update the oneTimeEventSubscribers using partial update
        OneTimeEventSubscribers partialUpdatedOneTimeEventSubscribers = new OneTimeEventSubscribers();
        partialUpdatedOneTimeEventSubscribers.setId(oneTimeEventSubscribers.getId());

        partialUpdatedOneTimeEventSubscribers.paidAmount(UPDATED_PAID_AMOUNT).status(UPDATED_STATUS);

        restOneTimeEventSubscribersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOneTimeEventSubscribers.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOneTimeEventSubscribers))
            )
            .andExpect(status().isOk());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeUpdate);
        OneTimeEventSubscribers testOneTimeEventSubscribers = oneTimeEventSubscribersList.get(oneTimeEventSubscribersList.size() - 1);
        assertThat(testOneTimeEventSubscribers.getPaidAmount()).isEqualByComparingTo(UPDATED_PAID_AMOUNT);
        assertThat(testOneTimeEventSubscribers.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingOneTimeEventSubscribers() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        oneTimeEventSubscribers.setId(longCount.incrementAndGet());

        // Create the OneTimeEventSubscribers
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOneTimeEventSubscribersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, oneTimeEventSubscribersDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOneTimeEventSubscribers() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        oneTimeEventSubscribers.setId(longCount.incrementAndGet());

        // Create the OneTimeEventSubscribers
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOneTimeEventSubscribersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOneTimeEventSubscribers() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        oneTimeEventSubscribers.setId(longCount.incrementAndGet());

        // Create the OneTimeEventSubscribers
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = oneTimeEventSubscribersMapper.toDto(oneTimeEventSubscribers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOneTimeEventSubscribersMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventSubscribersDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OneTimeEventSubscribers in the database
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOneTimeEventSubscribers() throws Exception {
        // Initialize the database
        oneTimeEventSubscribersRepository.saveAndFlush(oneTimeEventSubscribers);
        oneTimeEventSubscribersRepository.save(oneTimeEventSubscribers);
        oneTimeEventSubscribersSearchRepository.save(oneTimeEventSubscribers);

        int databaseSizeBeforeDelete = oneTimeEventSubscribersRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the oneTimeEventSubscribers
        restOneTimeEventSubscribersMockMvc
            .perform(delete(ENTITY_API_URL_ID, oneTimeEventSubscribers.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OneTimeEventSubscribers> oneTimeEventSubscribersList = oneTimeEventSubscribersRepository.findAll();
        assertThat(oneTimeEventSubscribersList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSubscribersSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOneTimeEventSubscribers() throws Exception {
        // Initialize the database
        oneTimeEventSubscribers = oneTimeEventSubscribersRepository.saveAndFlush(oneTimeEventSubscribers);
        oneTimeEventSubscribersSearchRepository.save(oneTimeEventSubscribers);

        // Search the oneTimeEventSubscribers
        restOneTimeEventSubscribersMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + oneTimeEventSubscribers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oneTimeEventSubscribers.getId().intValue())))
            .andExpect(jsonPath("$.[*].paidAmount").value(hasItem(sameNumber(DEFAULT_PAID_AMOUNT))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
