package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.SaleLead;
import com.sportifyindia.app.domain.enumeration.CustomerTypeEnum;
import com.sportifyindia.app.domain.enumeration.LeadStatusEnum;
import com.sportifyindia.app.repository.SaleLeadRepository;
import com.sportifyindia.app.repository.search.SaleLeadSearchRepository;
import com.sportifyindia.app.service.dto.SaleLeadDTO;
import com.sportifyindia.app.service.mapper.SaleLeadMapper;
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
 * Integration tests for the {@link SaleLeadResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SaleLeadResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final CustomerTypeEnum DEFAULT_CUSTOMER_TYPE = CustomerTypeEnum.INDIVIDUAL;
    private static final CustomerTypeEnum UPDATED_CUSTOMER_TYPE = CustomerTypeEnum.BUSINESS;

    private static final String DEFAULT_LEAD_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_LEAD_SOURCE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LeadStatusEnum DEFAULT_LEAD_STATUS = LeadStatusEnum.NEW;
    private static final LeadStatusEnum UPDATED_LEAD_STATUS = LeadStatusEnum.IN_PROGRESS;

    private static final Instant DEFAULT_DEAL_EXPIRY_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEAL_EXPIRY_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/sale-leads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/sale-leads/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SaleLeadRepository saleLeadRepository;

    @Autowired
    private SaleLeadMapper saleLeadMapper;

    @Autowired
    private SaleLeadSearchRepository saleLeadSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSaleLeadMockMvc;

    private SaleLead saleLead;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleLead createEntity(EntityManager em) {
        SaleLead saleLead = new SaleLead()
            .fullName(DEFAULT_FULL_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .title(DEFAULT_TITLE)
            .customerType(DEFAULT_CUSTOMER_TYPE)
            .leadSource(DEFAULT_LEAD_SOURCE)
            .description(DEFAULT_DESCRIPTION)
            .leadStatus(DEFAULT_LEAD_STATUS)
            .dealExpiryDate(DEFAULT_DEAL_EXPIRY_DATE);
        return saleLead;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SaleLead createUpdatedEntity(EntityManager em) {
        SaleLead saleLead = new SaleLead()
            .fullName(UPDATED_FULL_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .title(UPDATED_TITLE)
            .customerType(UPDATED_CUSTOMER_TYPE)
            .leadSource(UPDATED_LEAD_SOURCE)
            .description(UPDATED_DESCRIPTION)
            .leadStatus(UPDATED_LEAD_STATUS)
            .dealExpiryDate(UPDATED_DEAL_EXPIRY_DATE);
        return saleLead;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        saleLeadSearchRepository.deleteAll();
        assertThat(saleLeadSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        saleLead = createEntity(em);
    }

    @Test
    @Transactional
    void createSaleLead() throws Exception {
        int databaseSizeBeforeCreate = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        // Create the SaleLead
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);
        restSaleLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isCreated());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        SaleLead testSaleLead = saleLeadList.get(saleLeadList.size() - 1);
        assertThat(testSaleLead.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testSaleLead.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testSaleLead.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testSaleLead.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testSaleLead.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSaleLead.getCustomerType()).isEqualTo(DEFAULT_CUSTOMER_TYPE);
        assertThat(testSaleLead.getLeadSource()).isEqualTo(DEFAULT_LEAD_SOURCE);
        assertThat(testSaleLead.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSaleLead.getLeadStatus()).isEqualTo(DEFAULT_LEAD_STATUS);
        assertThat(testSaleLead.getDealExpiryDate()).isEqualTo(DEFAULT_DEAL_EXPIRY_DATE);
    }

    @Test
    @Transactional
    void createSaleLeadWithExistingId() throws Exception {
        // Create the SaleLead with an existing ID
        saleLead.setId(1L);
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        int databaseSizeBeforeCreate = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSaleLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        // set the field null
        saleLead.setFullName(null);

        // Create the SaleLead, which fails.
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        restSaleLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isBadRequest());

        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        // set the field null
        saleLead.setLastName(null);

        // Create the SaleLead, which fails.
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        restSaleLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isBadRequest());

        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        // set the field null
        saleLead.setEmail(null);

        // Create the SaleLead, which fails.
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        restSaleLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isBadRequest());

        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        // set the field null
        saleLead.setPhoneNumber(null);

        // Create the SaleLead, which fails.
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        restSaleLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isBadRequest());

        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCustomerTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        // set the field null
        saleLead.setCustomerType(null);

        // Create the SaleLead, which fails.
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        restSaleLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isBadRequest());

        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLeadStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        // set the field null
        saleLead.setLeadStatus(null);

        // Create the SaleLead, which fails.
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        restSaleLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isBadRequest());

        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDealExpiryDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        // set the field null
        saleLead.setDealExpiryDate(null);

        // Create the SaleLead, which fails.
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        restSaleLeadMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isBadRequest());

        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSaleLeads() throws Exception {
        // Initialize the database
        saleLeadRepository.saveAndFlush(saleLead);

        // Get all the saleLeadList
        restSaleLeadMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleLead.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].customerType").value(hasItem(DEFAULT_CUSTOMER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].leadSource").value(hasItem(DEFAULT_LEAD_SOURCE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].leadStatus").value(hasItem(DEFAULT_LEAD_STATUS.toString())))
            .andExpect(jsonPath("$.[*].dealExpiryDate").value(hasItem(DEFAULT_DEAL_EXPIRY_DATE.toString())));
    }

    @Test
    @Transactional
    void getSaleLead() throws Exception {
        // Initialize the database
        saleLeadRepository.saveAndFlush(saleLead);

        // Get the saleLead
        restSaleLeadMockMvc
            .perform(get(ENTITY_API_URL_ID, saleLead.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(saleLead.getId().intValue()))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.customerType").value(DEFAULT_CUSTOMER_TYPE.toString()))
            .andExpect(jsonPath("$.leadSource").value(DEFAULT_LEAD_SOURCE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.leadStatus").value(DEFAULT_LEAD_STATUS.toString()))
            .andExpect(jsonPath("$.dealExpiryDate").value(DEFAULT_DEAL_EXPIRY_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSaleLead() throws Exception {
        // Get the saleLead
        restSaleLeadMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSaleLead() throws Exception {
        // Initialize the database
        saleLeadRepository.saveAndFlush(saleLead);

        int databaseSizeBeforeUpdate = saleLeadRepository.findAll().size();
        saleLeadSearchRepository.save(saleLead);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());

        // Update the saleLead
        SaleLead updatedSaleLead = saleLeadRepository.findById(saleLead.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSaleLead are not directly saved in db
        em.detach(updatedSaleLead);
        updatedSaleLead
            .fullName(UPDATED_FULL_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .title(UPDATED_TITLE)
            .customerType(UPDATED_CUSTOMER_TYPE)
            .leadSource(UPDATED_LEAD_SOURCE)
            .description(UPDATED_DESCRIPTION)
            .leadStatus(UPDATED_LEAD_STATUS)
            .dealExpiryDate(UPDATED_DEAL_EXPIRY_DATE);
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(updatedSaleLead);

        restSaleLeadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleLeadDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(saleLeadDTO))
            )
            .andExpect(status().isOk());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeUpdate);
        SaleLead testSaleLead = saleLeadList.get(saleLeadList.size() - 1);
        assertThat(testSaleLead.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testSaleLead.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSaleLead.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testSaleLead.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testSaleLead.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSaleLead.getCustomerType()).isEqualTo(UPDATED_CUSTOMER_TYPE);
        assertThat(testSaleLead.getLeadSource()).isEqualTo(UPDATED_LEAD_SOURCE);
        assertThat(testSaleLead.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSaleLead.getLeadStatus()).isEqualTo(UPDATED_LEAD_STATUS);
        assertThat(testSaleLead.getDealExpiryDate()).isEqualTo(UPDATED_DEAL_EXPIRY_DATE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SaleLead> saleLeadSearchList = IterableUtils.toList(saleLeadSearchRepository.findAll());
                SaleLead testSaleLeadSearch = saleLeadSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSaleLeadSearch.getFullName()).isEqualTo(UPDATED_FULL_NAME);
                assertThat(testSaleLeadSearch.getLastName()).isEqualTo(UPDATED_LAST_NAME);
                assertThat(testSaleLeadSearch.getEmail()).isEqualTo(UPDATED_EMAIL);
                assertThat(testSaleLeadSearch.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
                assertThat(testSaleLeadSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testSaleLeadSearch.getCustomerType()).isEqualTo(UPDATED_CUSTOMER_TYPE);
                assertThat(testSaleLeadSearch.getLeadSource()).isEqualTo(UPDATED_LEAD_SOURCE);
                assertThat(testSaleLeadSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testSaleLeadSearch.getLeadStatus()).isEqualTo(UPDATED_LEAD_STATUS);
                assertThat(testSaleLeadSearch.getDealExpiryDate()).isEqualTo(UPDATED_DEAL_EXPIRY_DATE);
            });
    }

    @Test
    @Transactional
    void putNonExistingSaleLead() throws Exception {
        int databaseSizeBeforeUpdate = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        saleLead.setId(longCount.incrementAndGet());

        // Create the SaleLead
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleLeadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, saleLeadDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(saleLeadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSaleLead() throws Exception {
        int databaseSizeBeforeUpdate = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        saleLead.setId(longCount.incrementAndGet());

        // Create the SaleLead
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleLeadMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(saleLeadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSaleLead() throws Exception {
        int databaseSizeBeforeUpdate = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        saleLead.setId(longCount.incrementAndGet());

        // Create the SaleLead
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleLeadMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(saleLeadDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSaleLeadWithPatch() throws Exception {
        // Initialize the database
        saleLeadRepository.saveAndFlush(saleLead);

        int databaseSizeBeforeUpdate = saleLeadRepository.findAll().size();

        // Update the saleLead using partial update
        SaleLead partialUpdatedSaleLead = new SaleLead();
        partialUpdatedSaleLead.setId(saleLead.getId());

        partialUpdatedSaleLead
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .leadStatus(UPDATED_LEAD_STATUS)
            .dealExpiryDate(UPDATED_DEAL_EXPIRY_DATE);

        restSaleLeadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleLead.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSaleLead))
            )
            .andExpect(status().isOk());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeUpdate);
        SaleLead testSaleLead = saleLeadList.get(saleLeadList.size() - 1);
        assertThat(testSaleLead.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testSaleLead.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSaleLead.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testSaleLead.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testSaleLead.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSaleLead.getCustomerType()).isEqualTo(DEFAULT_CUSTOMER_TYPE);
        assertThat(testSaleLead.getLeadSource()).isEqualTo(DEFAULT_LEAD_SOURCE);
        assertThat(testSaleLead.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSaleLead.getLeadStatus()).isEqualTo(UPDATED_LEAD_STATUS);
        assertThat(testSaleLead.getDealExpiryDate()).isEqualTo(UPDATED_DEAL_EXPIRY_DATE);
    }

    @Test
    @Transactional
    void fullUpdateSaleLeadWithPatch() throws Exception {
        // Initialize the database
        saleLeadRepository.saveAndFlush(saleLead);

        int databaseSizeBeforeUpdate = saleLeadRepository.findAll().size();

        // Update the saleLead using partial update
        SaleLead partialUpdatedSaleLead = new SaleLead();
        partialUpdatedSaleLead.setId(saleLead.getId());

        partialUpdatedSaleLead
            .fullName(UPDATED_FULL_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .title(UPDATED_TITLE)
            .customerType(UPDATED_CUSTOMER_TYPE)
            .leadSource(UPDATED_LEAD_SOURCE)
            .description(UPDATED_DESCRIPTION)
            .leadStatus(UPDATED_LEAD_STATUS)
            .dealExpiryDate(UPDATED_DEAL_EXPIRY_DATE);

        restSaleLeadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSaleLead.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSaleLead))
            )
            .andExpect(status().isOk());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeUpdate);
        SaleLead testSaleLead = saleLeadList.get(saleLeadList.size() - 1);
        assertThat(testSaleLead.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testSaleLead.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testSaleLead.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testSaleLead.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testSaleLead.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSaleLead.getCustomerType()).isEqualTo(UPDATED_CUSTOMER_TYPE);
        assertThat(testSaleLead.getLeadSource()).isEqualTo(UPDATED_LEAD_SOURCE);
        assertThat(testSaleLead.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSaleLead.getLeadStatus()).isEqualTo(UPDATED_LEAD_STATUS);
        assertThat(testSaleLead.getDealExpiryDate()).isEqualTo(UPDATED_DEAL_EXPIRY_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingSaleLead() throws Exception {
        int databaseSizeBeforeUpdate = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        saleLead.setId(longCount.incrementAndGet());

        // Create the SaleLead
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSaleLeadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, saleLeadDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(saleLeadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSaleLead() throws Exception {
        int databaseSizeBeforeUpdate = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        saleLead.setId(longCount.incrementAndGet());

        // Create the SaleLead
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleLeadMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(saleLeadDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSaleLead() throws Exception {
        int databaseSizeBeforeUpdate = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        saleLead.setId(longCount.incrementAndGet());

        // Create the SaleLead
        SaleLeadDTO saleLeadDTO = saleLeadMapper.toDto(saleLead);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSaleLeadMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(saleLeadDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SaleLead in the database
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSaleLead() throws Exception {
        // Initialize the database
        saleLeadRepository.saveAndFlush(saleLead);
        saleLeadRepository.save(saleLead);
        saleLeadSearchRepository.save(saleLead);

        int databaseSizeBeforeDelete = saleLeadRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the saleLead
        restSaleLeadMockMvc
            .perform(delete(ENTITY_API_URL_ID, saleLead.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SaleLead> saleLeadList = saleLeadRepository.findAll();
        assertThat(saleLeadList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(saleLeadSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSaleLead() throws Exception {
        // Initialize the database
        saleLead = saleLeadRepository.saveAndFlush(saleLead);
        saleLeadSearchRepository.save(saleLead);

        // Search the saleLead
        restSaleLeadMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + saleLead.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(saleLead.getId().intValue())))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].customerType").value(hasItem(DEFAULT_CUSTOMER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].leadSource").value(hasItem(DEFAULT_LEAD_SOURCE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].leadStatus").value(hasItem(DEFAULT_LEAD_STATUS.toString())))
            .andExpect(jsonPath("$.[*].dealExpiryDate").value(hasItem(DEFAULT_DEAL_EXPIRY_DATE.toString())));
    }
}
