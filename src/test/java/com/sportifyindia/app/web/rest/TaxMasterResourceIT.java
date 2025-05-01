package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.TaxMaster;
import com.sportifyindia.app.domain.enumeration.TaxSourceEnum;
import com.sportifyindia.app.repository.TaxMasterRepository;
import com.sportifyindia.app.repository.search.TaxMasterSearchRepository;
import com.sportifyindia.app.service.dto.TaxMasterDTO;
import com.sportifyindia.app.service.mapper.TaxMasterMapper;
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
 * Integration tests for the {@link TaxMasterResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaxMasterResourceIT {

    private static final String DEFAULT_TAX_SLAB = "AAAAAAAAAA";
    private static final String UPDATED_TAX_SLAB = "BBBBBBBBBB";

    private static final String DEFAULT_TAX_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TAX_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String DEFAULT_TAX_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TAX_TYPE = "BBBBBBBBBB";

    private static final TaxSourceEnum DEFAULT_TAX_SOURCE = TaxSourceEnum.FACILITY;
    private static final TaxSourceEnum UPDATED_TAX_SOURCE = TaxSourceEnum.USER;

    private static final String ENTITY_API_URL = "/api/tax-masters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/tax-masters/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaxMasterRepository taxMasterRepository;

    @Autowired
    private TaxMasterMapper taxMasterMapper;

    @Autowired
    private TaxMasterSearchRepository taxMasterSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaxMasterMockMvc;

    private TaxMaster taxMaster;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaxMaster createEntity(EntityManager em) {
        TaxMaster taxMaster = new TaxMaster()
            .taxSlab(DEFAULT_TAX_SLAB)
            .taxName(DEFAULT_TAX_NAME)
            .isActive(DEFAULT_IS_ACTIVE)
            .taxType(DEFAULT_TAX_TYPE)
            .taxSource(DEFAULT_TAX_SOURCE);
        return taxMaster;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaxMaster createUpdatedEntity(EntityManager em) {
        TaxMaster taxMaster = new TaxMaster()
            .taxSlab(UPDATED_TAX_SLAB)
            .taxName(UPDATED_TAX_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .taxType(UPDATED_TAX_TYPE)
            .taxSource(UPDATED_TAX_SOURCE);
        return taxMaster;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        taxMasterSearchRepository.deleteAll();
        assertThat(taxMasterSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        taxMaster = createEntity(em);
    }

    @Test
    @Transactional
    void createTaxMaster() throws Exception {
        int databaseSizeBeforeCreate = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        // Create the TaxMaster
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);
        restTaxMasterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxMasterDTO)))
            .andExpect(status().isCreated());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TaxMaster testTaxMaster = taxMasterList.get(taxMasterList.size() - 1);
        assertThat(testTaxMaster.getTaxSlab()).isEqualTo(DEFAULT_TAX_SLAB);
        assertThat(testTaxMaster.getTaxName()).isEqualTo(DEFAULT_TAX_NAME);
        assertThat(testTaxMaster.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
        assertThat(testTaxMaster.getTaxType()).isEqualTo(DEFAULT_TAX_TYPE);
        assertThat(testTaxMaster.getTaxSource()).isEqualTo(DEFAULT_TAX_SOURCE);
    }

    @Test
    @Transactional
    void createTaxMasterWithExistingId() throws Exception {
        // Create the TaxMaster with an existing ID
        taxMaster.setId(1L);
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        int databaseSizeBeforeCreate = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaxMasterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxMasterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTaxSlabIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        // set the field null
        taxMaster.setTaxSlab(null);

        // Create the TaxMaster, which fails.
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        restTaxMasterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxMasterDTO)))
            .andExpect(status().isBadRequest());

        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTaxNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        // set the field null
        taxMaster.setTaxName(null);

        // Create the TaxMaster, which fails.
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        restTaxMasterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxMasterDTO)))
            .andExpect(status().isBadRequest());

        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        // set the field null
        taxMaster.setIsActive(null);

        // Create the TaxMaster, which fails.
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        restTaxMasterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxMasterDTO)))
            .andExpect(status().isBadRequest());

        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTaxTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        // set the field null
        taxMaster.setTaxType(null);

        // Create the TaxMaster, which fails.
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        restTaxMasterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxMasterDTO)))
            .andExpect(status().isBadRequest());

        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTaxSourceIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        // set the field null
        taxMaster.setTaxSource(null);

        // Create the TaxMaster, which fails.
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        restTaxMasterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxMasterDTO)))
            .andExpect(status().isBadRequest());

        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTaxMasters() throws Exception {
        // Initialize the database
        taxMasterRepository.saveAndFlush(taxMaster);

        // Get all the taxMasterList
        restTaxMasterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taxMaster.getId().intValue())))
            .andExpect(jsonPath("$.[*].taxSlab").value(hasItem(DEFAULT_TAX_SLAB)))
            .andExpect(jsonPath("$.[*].taxName").value(hasItem(DEFAULT_TAX_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].taxType").value(hasItem(DEFAULT_TAX_TYPE)))
            .andExpect(jsonPath("$.[*].taxSource").value(hasItem(DEFAULT_TAX_SOURCE.toString())));
    }

    @Test
    @Transactional
    void getTaxMaster() throws Exception {
        // Initialize the database
        taxMasterRepository.saveAndFlush(taxMaster);

        // Get the taxMaster
        restTaxMasterMockMvc
            .perform(get(ENTITY_API_URL_ID, taxMaster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taxMaster.getId().intValue()))
            .andExpect(jsonPath("$.taxSlab").value(DEFAULT_TAX_SLAB))
            .andExpect(jsonPath("$.taxName").value(DEFAULT_TAX_NAME))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.taxType").value(DEFAULT_TAX_TYPE))
            .andExpect(jsonPath("$.taxSource").value(DEFAULT_TAX_SOURCE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTaxMaster() throws Exception {
        // Get the taxMaster
        restTaxMasterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaxMaster() throws Exception {
        // Initialize the database
        taxMasterRepository.saveAndFlush(taxMaster);

        int databaseSizeBeforeUpdate = taxMasterRepository.findAll().size();
        taxMasterSearchRepository.save(taxMaster);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());

        // Update the taxMaster
        TaxMaster updatedTaxMaster = taxMasterRepository.findById(taxMaster.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTaxMaster are not directly saved in db
        em.detach(updatedTaxMaster);
        updatedTaxMaster
            .taxSlab(UPDATED_TAX_SLAB)
            .taxName(UPDATED_TAX_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .taxType(UPDATED_TAX_TYPE)
            .taxSource(UPDATED_TAX_SOURCE);
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(updatedTaxMaster);

        restTaxMasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taxMasterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taxMasterDTO))
            )
            .andExpect(status().isOk());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeUpdate);
        TaxMaster testTaxMaster = taxMasterList.get(taxMasterList.size() - 1);
        assertThat(testTaxMaster.getTaxSlab()).isEqualTo(UPDATED_TAX_SLAB);
        assertThat(testTaxMaster.getTaxName()).isEqualTo(UPDATED_TAX_NAME);
        assertThat(testTaxMaster.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testTaxMaster.getTaxType()).isEqualTo(UPDATED_TAX_TYPE);
        assertThat(testTaxMaster.getTaxSource()).isEqualTo(UPDATED_TAX_SOURCE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TaxMaster> taxMasterSearchList = IterableUtils.toList(taxMasterSearchRepository.findAll());
                TaxMaster testTaxMasterSearch = taxMasterSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTaxMasterSearch.getTaxSlab()).isEqualTo(UPDATED_TAX_SLAB);
                assertThat(testTaxMasterSearch.getTaxName()).isEqualTo(UPDATED_TAX_NAME);
                assertThat(testTaxMasterSearch.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
                assertThat(testTaxMasterSearch.getTaxType()).isEqualTo(UPDATED_TAX_TYPE);
                assertThat(testTaxMasterSearch.getTaxSource()).isEqualTo(UPDATED_TAX_SOURCE);
            });
    }

    @Test
    @Transactional
    void putNonExistingTaxMaster() throws Exception {
        int databaseSizeBeforeUpdate = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        taxMaster.setId(longCount.incrementAndGet());

        // Create the TaxMaster
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaxMasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taxMasterDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taxMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaxMaster() throws Exception {
        int databaseSizeBeforeUpdate = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        taxMaster.setId(longCount.incrementAndGet());

        // Create the TaxMaster
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaxMasterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taxMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaxMaster() throws Exception {
        int databaseSizeBeforeUpdate = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        taxMaster.setId(longCount.incrementAndGet());

        // Create the TaxMaster
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaxMasterMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxMasterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTaxMasterWithPatch() throws Exception {
        // Initialize the database
        taxMasterRepository.saveAndFlush(taxMaster);

        int databaseSizeBeforeUpdate = taxMasterRepository.findAll().size();

        // Update the taxMaster using partial update
        TaxMaster partialUpdatedTaxMaster = new TaxMaster();
        partialUpdatedTaxMaster.setId(taxMaster.getId());

        partialUpdatedTaxMaster.taxSlab(UPDATED_TAX_SLAB).taxName(UPDATED_TAX_NAME).isActive(UPDATED_IS_ACTIVE).taxType(UPDATED_TAX_TYPE);

        restTaxMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaxMaster.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaxMaster))
            )
            .andExpect(status().isOk());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeUpdate);
        TaxMaster testTaxMaster = taxMasterList.get(taxMasterList.size() - 1);
        assertThat(testTaxMaster.getTaxSlab()).isEqualTo(UPDATED_TAX_SLAB);
        assertThat(testTaxMaster.getTaxName()).isEqualTo(UPDATED_TAX_NAME);
        assertThat(testTaxMaster.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testTaxMaster.getTaxType()).isEqualTo(UPDATED_TAX_TYPE);
        assertThat(testTaxMaster.getTaxSource()).isEqualTo(DEFAULT_TAX_SOURCE);
    }

    @Test
    @Transactional
    void fullUpdateTaxMasterWithPatch() throws Exception {
        // Initialize the database
        taxMasterRepository.saveAndFlush(taxMaster);

        int databaseSizeBeforeUpdate = taxMasterRepository.findAll().size();

        // Update the taxMaster using partial update
        TaxMaster partialUpdatedTaxMaster = new TaxMaster();
        partialUpdatedTaxMaster.setId(taxMaster.getId());

        partialUpdatedTaxMaster
            .taxSlab(UPDATED_TAX_SLAB)
            .taxName(UPDATED_TAX_NAME)
            .isActive(UPDATED_IS_ACTIVE)
            .taxType(UPDATED_TAX_TYPE)
            .taxSource(UPDATED_TAX_SOURCE);

        restTaxMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaxMaster.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaxMaster))
            )
            .andExpect(status().isOk());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeUpdate);
        TaxMaster testTaxMaster = taxMasterList.get(taxMasterList.size() - 1);
        assertThat(testTaxMaster.getTaxSlab()).isEqualTo(UPDATED_TAX_SLAB);
        assertThat(testTaxMaster.getTaxName()).isEqualTo(UPDATED_TAX_NAME);
        assertThat(testTaxMaster.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
        assertThat(testTaxMaster.getTaxType()).isEqualTo(UPDATED_TAX_TYPE);
        assertThat(testTaxMaster.getTaxSource()).isEqualTo(UPDATED_TAX_SOURCE);
    }

    @Test
    @Transactional
    void patchNonExistingTaxMaster() throws Exception {
        int databaseSizeBeforeUpdate = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        taxMaster.setId(longCount.incrementAndGet());

        // Create the TaxMaster
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaxMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taxMasterDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taxMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaxMaster() throws Exception {
        int databaseSizeBeforeUpdate = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        taxMaster.setId(longCount.incrementAndGet());

        // Create the TaxMaster
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaxMasterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taxMasterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaxMaster() throws Exception {
        int databaseSizeBeforeUpdate = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        taxMaster.setId(longCount.incrementAndGet());

        // Create the TaxMaster
        TaxMasterDTO taxMasterDTO = taxMasterMapper.toDto(taxMaster);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaxMasterMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(taxMasterDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaxMaster in the database
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTaxMaster() throws Exception {
        // Initialize the database
        taxMasterRepository.saveAndFlush(taxMaster);
        taxMasterRepository.save(taxMaster);
        taxMasterSearchRepository.save(taxMaster);

        int databaseSizeBeforeDelete = taxMasterRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the taxMaster
        restTaxMasterMockMvc
            .perform(delete(ENTITY_API_URL_ID, taxMaster.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TaxMaster> taxMasterList = taxMasterRepository.findAll();
        assertThat(taxMasterList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxMasterSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTaxMaster() throws Exception {
        // Initialize the database
        taxMaster = taxMasterRepository.saveAndFlush(taxMaster);
        taxMasterSearchRepository.save(taxMaster);

        // Search the taxMaster
        restTaxMasterMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + taxMaster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taxMaster.getId().intValue())))
            .andExpect(jsonPath("$.[*].taxSlab").value(hasItem(DEFAULT_TAX_SLAB)))
            .andExpect(jsonPath("$.[*].taxName").value(hasItem(DEFAULT_TAX_NAME)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].taxType").value(hasItem(DEFAULT_TAX_TYPE)))
            .andExpect(jsonPath("$.[*].taxSource").value(hasItem(DEFAULT_TAX_SOURCE.toString())));
    }
}
