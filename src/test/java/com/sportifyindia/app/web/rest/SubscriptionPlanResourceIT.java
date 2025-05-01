package com.sportifyindia.app.web.rest;

import static com.sportifyindia.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.SubscriptionPlan;
import com.sportifyindia.app.domain.enumeration.SubscriptionPlanStatusEnum;
import com.sportifyindia.app.repository.SubscriptionPlanRepository;
import com.sportifyindia.app.repository.search.SubscriptionPlanSearchRepository;
import com.sportifyindia.app.service.SubscriptionPlanService;
import com.sportifyindia.app.service.dto.SubscriptionPlanDTO;
import com.sportifyindia.app.service.mapper.SubscriptionPlanMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SubscriptionPlanResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SubscriptionPlanResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_VALIDITY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_VALIDITY_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_VALIDITY_PERIOD = 1;
    private static final Integer UPDATED_VALIDITY_PERIOD = 2;

    private static final Integer DEFAULT_NO_OF_PAUSE_DAYS = 1;
    private static final Integer UPDATED_NO_OF_PAUSE_DAYS = 2;

    private static final Integer DEFAULT_SESSION_LIMIT = 1;
    private static final Integer UPDATED_SESSION_LIMIT = 2;

    private static final SubscriptionPlanStatusEnum DEFAULT_STATUS = SubscriptionPlanStatusEnum.ACTIVE;
    private static final SubscriptionPlanStatusEnum UPDATED_STATUS = SubscriptionPlanStatusEnum.EXPIRED;

    private static final String ENTITY_API_URL = "/api/subscription-plans";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/subscription-plans/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepositoryMock;

    @Autowired
    private SubscriptionPlanMapper subscriptionPlanMapper;

    @Mock
    private SubscriptionPlanService subscriptionPlanServiceMock;

    @Autowired
    private SubscriptionPlanSearchRepository subscriptionPlanSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubscriptionPlanMockMvc;

    private SubscriptionPlan subscriptionPlan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubscriptionPlan createEntity(EntityManager em) {
        SubscriptionPlan subscriptionPlan = new SubscriptionPlan()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .amount(DEFAULT_AMOUNT)
            .validityType(DEFAULT_VALIDITY_TYPE)
            .validityPeriod(DEFAULT_VALIDITY_PERIOD)
            .noOfPauseDays(DEFAULT_NO_OF_PAUSE_DAYS)
            .sessionLimit(DEFAULT_SESSION_LIMIT)
            .status(DEFAULT_STATUS);
        return subscriptionPlan;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SubscriptionPlan createUpdatedEntity(EntityManager em) {
        SubscriptionPlan subscriptionPlan = new SubscriptionPlan()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .validityType(UPDATED_VALIDITY_TYPE)
            .validityPeriod(UPDATED_VALIDITY_PERIOD)
            .noOfPauseDays(UPDATED_NO_OF_PAUSE_DAYS)
            .sessionLimit(UPDATED_SESSION_LIMIT)
            .status(UPDATED_STATUS);
        return subscriptionPlan;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        subscriptionPlanSearchRepository.deleteAll();
        assertThat(subscriptionPlanSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        subscriptionPlan = createEntity(em);
    }

    @Test
    @Transactional
    void createSubscriptionPlan() throws Exception {
        int databaseSizeBeforeCreate = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        // Create the SubscriptionPlan
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);
        restSubscriptionPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SubscriptionPlan testSubscriptionPlan = subscriptionPlanList.get(subscriptionPlanList.size() - 1);
        assertThat(testSubscriptionPlan.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSubscriptionPlan.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSubscriptionPlan.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testSubscriptionPlan.getValidityType()).isEqualTo(DEFAULT_VALIDITY_TYPE);
        assertThat(testSubscriptionPlan.getValidityPeriod()).isEqualTo(DEFAULT_VALIDITY_PERIOD);
        assertThat(testSubscriptionPlan.getNoOfPauseDays()).isEqualTo(DEFAULT_NO_OF_PAUSE_DAYS);
        assertThat(testSubscriptionPlan.getSessionLimit()).isEqualTo(DEFAULT_SESSION_LIMIT);
        assertThat(testSubscriptionPlan.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createSubscriptionPlanWithExistingId() throws Exception {
        // Create the SubscriptionPlan with an existing ID
        subscriptionPlan.setId(1L);
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        int databaseSizeBeforeCreate = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubscriptionPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        // set the field null
        subscriptionPlan.setName(null);

        // Create the SubscriptionPlan, which fails.
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        restSubscriptionPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        // set the field null
        subscriptionPlan.setAmount(null);

        // Create the SubscriptionPlan, which fails.
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        restSubscriptionPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkValidityTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        // set the field null
        subscriptionPlan.setValidityType(null);

        // Create the SubscriptionPlan, which fails.
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        restSubscriptionPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkValidityPeriodIsRequired() throws Exception {
        int databaseSizeBeforeTest = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        // set the field null
        subscriptionPlan.setValidityPeriod(null);

        // Create the SubscriptionPlan, which fails.
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        restSubscriptionPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNoOfPauseDaysIsRequired() throws Exception {
        int databaseSizeBeforeTest = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        // set the field null
        subscriptionPlan.setNoOfPauseDays(null);

        // Create the SubscriptionPlan, which fails.
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        restSubscriptionPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSessionLimitIsRequired() throws Exception {
        int databaseSizeBeforeTest = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        // set the field null
        subscriptionPlan.setSessionLimit(null);

        // Create the SubscriptionPlan, which fails.
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        restSubscriptionPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        // set the field null
        subscriptionPlan.setStatus(null);

        // Create the SubscriptionPlan, which fails.
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        restSubscriptionPlanMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSubscriptionPlans() throws Exception {
        // Initialize the database
        subscriptionPlanRepository.saveAndFlush(subscriptionPlan);

        // Get all the subscriptionPlanList
        restSubscriptionPlanMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscriptionPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].validityType").value(hasItem(DEFAULT_VALIDITY_TYPE)))
            .andExpect(jsonPath("$.[*].validityPeriod").value(hasItem(DEFAULT_VALIDITY_PERIOD)))
            .andExpect(jsonPath("$.[*].noOfPauseDays").value(hasItem(DEFAULT_NO_OF_PAUSE_DAYS)))
            .andExpect(jsonPath("$.[*].sessionLimit").value(hasItem(DEFAULT_SESSION_LIMIT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubscriptionPlansWithEagerRelationshipsIsEnabled() throws Exception {
        when(subscriptionPlanServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSubscriptionPlanMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(subscriptionPlanServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSubscriptionPlansWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(subscriptionPlanServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSubscriptionPlanMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(subscriptionPlanRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSubscriptionPlan() throws Exception {
        // Initialize the database
        subscriptionPlanRepository.saveAndFlush(subscriptionPlan);

        // Get the subscriptionPlan
        restSubscriptionPlanMockMvc
            .perform(get(ENTITY_API_URL_ID, subscriptionPlan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subscriptionPlan.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.validityType").value(DEFAULT_VALIDITY_TYPE))
            .andExpect(jsonPath("$.validityPeriod").value(DEFAULT_VALIDITY_PERIOD))
            .andExpect(jsonPath("$.noOfPauseDays").value(DEFAULT_NO_OF_PAUSE_DAYS))
            .andExpect(jsonPath("$.sessionLimit").value(DEFAULT_SESSION_LIMIT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSubscriptionPlan() throws Exception {
        // Get the subscriptionPlan
        restSubscriptionPlanMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubscriptionPlan() throws Exception {
        // Initialize the database
        subscriptionPlanRepository.saveAndFlush(subscriptionPlan);

        int databaseSizeBeforeUpdate = subscriptionPlanRepository.findAll().size();
        subscriptionPlanSearchRepository.save(subscriptionPlan);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());

        // Update the subscriptionPlan
        SubscriptionPlan updatedSubscriptionPlan = subscriptionPlanRepository.findById(subscriptionPlan.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSubscriptionPlan are not directly saved in db
        em.detach(updatedSubscriptionPlan);
        updatedSubscriptionPlan
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .validityType(UPDATED_VALIDITY_TYPE)
            .validityPeriod(UPDATED_VALIDITY_PERIOD)
            .noOfPauseDays(UPDATED_NO_OF_PAUSE_DAYS)
            .sessionLimit(UPDATED_SESSION_LIMIT)
            .status(UPDATED_STATUS);
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(updatedSubscriptionPlan);

        restSubscriptionPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscriptionPlanDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isOk());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeUpdate);
        SubscriptionPlan testSubscriptionPlan = subscriptionPlanList.get(subscriptionPlanList.size() - 1);
        assertThat(testSubscriptionPlan.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSubscriptionPlan.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSubscriptionPlan.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testSubscriptionPlan.getValidityType()).isEqualTo(UPDATED_VALIDITY_TYPE);
        assertThat(testSubscriptionPlan.getValidityPeriod()).isEqualTo(UPDATED_VALIDITY_PERIOD);
        assertThat(testSubscriptionPlan.getNoOfPauseDays()).isEqualTo(UPDATED_NO_OF_PAUSE_DAYS);
        assertThat(testSubscriptionPlan.getSessionLimit()).isEqualTo(UPDATED_SESSION_LIMIT);
        assertThat(testSubscriptionPlan.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SubscriptionPlan> subscriptionPlanSearchList = IterableUtils.toList(subscriptionPlanSearchRepository.findAll());
                SubscriptionPlan testSubscriptionPlanSearch = subscriptionPlanSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSubscriptionPlanSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testSubscriptionPlanSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testSubscriptionPlanSearch.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
                assertThat(testSubscriptionPlanSearch.getValidityType()).isEqualTo(UPDATED_VALIDITY_TYPE);
                assertThat(testSubscriptionPlanSearch.getValidityPeriod()).isEqualTo(UPDATED_VALIDITY_PERIOD);
                assertThat(testSubscriptionPlanSearch.getNoOfPauseDays()).isEqualTo(UPDATED_NO_OF_PAUSE_DAYS);
                assertThat(testSubscriptionPlanSearch.getSessionLimit()).isEqualTo(UPDATED_SESSION_LIMIT);
                assertThat(testSubscriptionPlanSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingSubscriptionPlan() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        subscriptionPlan.setId(longCount.incrementAndGet());

        // Create the SubscriptionPlan
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscriptionPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscriptionPlanDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubscriptionPlan() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        subscriptionPlan.setId(longCount.incrementAndGet());

        // Create the SubscriptionPlan
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionPlanMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubscriptionPlan() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        subscriptionPlan.setId(longCount.incrementAndGet());

        // Create the SubscriptionPlan
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionPlanMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSubscriptionPlanWithPatch() throws Exception {
        // Initialize the database
        subscriptionPlanRepository.saveAndFlush(subscriptionPlan);

        int databaseSizeBeforeUpdate = subscriptionPlanRepository.findAll().size();

        // Update the subscriptionPlan using partial update
        SubscriptionPlan partialUpdatedSubscriptionPlan = new SubscriptionPlan();
        partialUpdatedSubscriptionPlan.setId(subscriptionPlan.getId());

        partialUpdatedSubscriptionPlan
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .validityType(UPDATED_VALIDITY_TYPE)
            .sessionLimit(UPDATED_SESSION_LIMIT)
            .status(UPDATED_STATUS);

        restSubscriptionPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscriptionPlan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubscriptionPlan))
            )
            .andExpect(status().isOk());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeUpdate);
        SubscriptionPlan testSubscriptionPlan = subscriptionPlanList.get(subscriptionPlanList.size() - 1);
        assertThat(testSubscriptionPlan.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSubscriptionPlan.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSubscriptionPlan.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testSubscriptionPlan.getValidityType()).isEqualTo(UPDATED_VALIDITY_TYPE);
        assertThat(testSubscriptionPlan.getValidityPeriod()).isEqualTo(DEFAULT_VALIDITY_PERIOD);
        assertThat(testSubscriptionPlan.getNoOfPauseDays()).isEqualTo(DEFAULT_NO_OF_PAUSE_DAYS);
        assertThat(testSubscriptionPlan.getSessionLimit()).isEqualTo(UPDATED_SESSION_LIMIT);
        assertThat(testSubscriptionPlan.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateSubscriptionPlanWithPatch() throws Exception {
        // Initialize the database
        subscriptionPlanRepository.saveAndFlush(subscriptionPlan);

        int databaseSizeBeforeUpdate = subscriptionPlanRepository.findAll().size();

        // Update the subscriptionPlan using partial update
        SubscriptionPlan partialUpdatedSubscriptionPlan = new SubscriptionPlan();
        partialUpdatedSubscriptionPlan.setId(subscriptionPlan.getId());

        partialUpdatedSubscriptionPlan
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .validityType(UPDATED_VALIDITY_TYPE)
            .validityPeriod(UPDATED_VALIDITY_PERIOD)
            .noOfPauseDays(UPDATED_NO_OF_PAUSE_DAYS)
            .sessionLimit(UPDATED_SESSION_LIMIT)
            .status(UPDATED_STATUS);

        restSubscriptionPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscriptionPlan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubscriptionPlan))
            )
            .andExpect(status().isOk());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeUpdate);
        SubscriptionPlan testSubscriptionPlan = subscriptionPlanList.get(subscriptionPlanList.size() - 1);
        assertThat(testSubscriptionPlan.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSubscriptionPlan.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSubscriptionPlan.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testSubscriptionPlan.getValidityType()).isEqualTo(UPDATED_VALIDITY_TYPE);
        assertThat(testSubscriptionPlan.getValidityPeriod()).isEqualTo(UPDATED_VALIDITY_PERIOD);
        assertThat(testSubscriptionPlan.getNoOfPauseDays()).isEqualTo(UPDATED_NO_OF_PAUSE_DAYS);
        assertThat(testSubscriptionPlan.getSessionLimit()).isEqualTo(UPDATED_SESSION_LIMIT);
        assertThat(testSubscriptionPlan.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingSubscriptionPlan() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        subscriptionPlan.setId(longCount.incrementAndGet());

        // Create the SubscriptionPlan
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscriptionPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subscriptionPlanDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubscriptionPlan() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        subscriptionPlan.setId(longCount.incrementAndGet());

        // Create the SubscriptionPlan
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionPlanMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubscriptionPlan() throws Exception {
        int databaseSizeBeforeUpdate = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        subscriptionPlan.setId(longCount.incrementAndGet());

        // Create the SubscriptionPlan
        SubscriptionPlanDTO subscriptionPlanDTO = subscriptionPlanMapper.toDto(subscriptionPlan);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscriptionPlanMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subscriptionPlanDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SubscriptionPlan in the database
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSubscriptionPlan() throws Exception {
        // Initialize the database
        subscriptionPlanRepository.saveAndFlush(subscriptionPlan);
        subscriptionPlanRepository.save(subscriptionPlan);
        subscriptionPlanSearchRepository.save(subscriptionPlan);

        int databaseSizeBeforeDelete = subscriptionPlanRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the subscriptionPlan
        restSubscriptionPlanMockMvc
            .perform(delete(ENTITY_API_URL_ID, subscriptionPlan.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SubscriptionPlan> subscriptionPlanList = subscriptionPlanRepository.findAll();
        assertThat(subscriptionPlanList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(subscriptionPlanSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSubscriptionPlan() throws Exception {
        // Initialize the database
        subscriptionPlan = subscriptionPlanRepository.saveAndFlush(subscriptionPlan);
        subscriptionPlanSearchRepository.save(subscriptionPlan);

        // Search the subscriptionPlan
        restSubscriptionPlanMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + subscriptionPlan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscriptionPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].validityType").value(hasItem(DEFAULT_VALIDITY_TYPE)))
            .andExpect(jsonPath("$.[*].validityPeriod").value(hasItem(DEFAULT_VALIDITY_PERIOD)))
            .andExpect(jsonPath("$.[*].noOfPauseDays").value(hasItem(DEFAULT_NO_OF_PAUSE_DAYS)))
            .andExpect(jsonPath("$.[*].sessionLimit").value(hasItem(DEFAULT_SESSION_LIMIT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
