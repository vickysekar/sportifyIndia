package com.sportifyindia.app.web.rest;

import static com.sportifyindia.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.Tax;
import com.sportifyindia.app.domain.enumeration.TaxSourceEnum;
import com.sportifyindia.app.repository.TaxRepository;
import com.sportifyindia.app.repository.search.TaxSearchRepository;
import com.sportifyindia.app.service.dto.TaxDTO;
import com.sportifyindia.app.service.mapper.TaxMapper;
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
 * Integration tests for the {@link TaxResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaxResourceIT {

    private static final BigDecimal DEFAULT_NET_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_NET_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_COMPUTED_SLAB = "AAAAAAAAAA";
    private static final String UPDATED_COMPUTED_SLAB = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_COMPUTED_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_COMPUTED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_TAX_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TAX_TYPE = "BBBBBBBBBB";

    private static final TaxSourceEnum DEFAULT_TAX_SOURCE = TaxSourceEnum.FACILITY;
    private static final TaxSourceEnum UPDATED_TAX_SOURCE = TaxSourceEnum.USER;

    private static final String ENTITY_API_URL = "/api/taxes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/taxes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private TaxMapper taxMapper;

    @Autowired
    private TaxSearchRepository taxSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaxMockMvc;

    private Tax tax;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tax createEntity(EntityManager em) {
        Tax tax = new Tax()
            .netAmount(DEFAULT_NET_AMOUNT)
            .computedSlab(DEFAULT_COMPUTED_SLAB)
            .computedAmount(DEFAULT_COMPUTED_AMOUNT)
            .taxType(DEFAULT_TAX_TYPE)
            .taxSource(DEFAULT_TAX_SOURCE);
        return tax;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tax createUpdatedEntity(EntityManager em) {
        Tax tax = new Tax()
            .netAmount(UPDATED_NET_AMOUNT)
            .computedSlab(UPDATED_COMPUTED_SLAB)
            .computedAmount(UPDATED_COMPUTED_AMOUNT)
            .taxType(UPDATED_TAX_TYPE)
            .taxSource(UPDATED_TAX_SOURCE);
        return tax;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        taxSearchRepository.deleteAll();
        assertThat(taxSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        tax = createEntity(em);
    }

    @Test
    @Transactional
    void createTax() throws Exception {
        int databaseSizeBeforeCreate = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        // Create the Tax
        TaxDTO taxDTO = taxMapper.toDto(tax);
        restTaxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxDTO)))
            .andExpect(status().isCreated());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Tax testTax = taxList.get(taxList.size() - 1);
        assertThat(testTax.getNetAmount()).isEqualByComparingTo(DEFAULT_NET_AMOUNT);
        assertThat(testTax.getComputedSlab()).isEqualTo(DEFAULT_COMPUTED_SLAB);
        assertThat(testTax.getComputedAmount()).isEqualByComparingTo(DEFAULT_COMPUTED_AMOUNT);
        assertThat(testTax.getTaxType()).isEqualTo(DEFAULT_TAX_TYPE);
        assertThat(testTax.getTaxSource()).isEqualTo(DEFAULT_TAX_SOURCE);
    }

    @Test
    @Transactional
    void createTaxWithExistingId() throws Exception {
        // Create the Tax with an existing ID
        tax.setId(1L);
        TaxDTO taxDTO = taxMapper.toDto(tax);

        int databaseSizeBeforeCreate = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNetAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        // set the field null
        tax.setNetAmount(null);

        // Create the Tax, which fails.
        TaxDTO taxDTO = taxMapper.toDto(tax);

        restTaxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxDTO)))
            .andExpect(status().isBadRequest());

        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkComputedSlabIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        // set the field null
        tax.setComputedSlab(null);

        // Create the Tax, which fails.
        TaxDTO taxDTO = taxMapper.toDto(tax);

        restTaxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxDTO)))
            .andExpect(status().isBadRequest());

        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkComputedAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        // set the field null
        tax.setComputedAmount(null);

        // Create the Tax, which fails.
        TaxDTO taxDTO = taxMapper.toDto(tax);

        restTaxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxDTO)))
            .andExpect(status().isBadRequest());

        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTaxTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        // set the field null
        tax.setTaxType(null);

        // Create the Tax, which fails.
        TaxDTO taxDTO = taxMapper.toDto(tax);

        restTaxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxDTO)))
            .andExpect(status().isBadRequest());

        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTaxSourceIsRequired() throws Exception {
        int databaseSizeBeforeTest = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        // set the field null
        tax.setTaxSource(null);

        // Create the Tax, which fails.
        TaxDTO taxDTO = taxMapper.toDto(tax);

        restTaxMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxDTO)))
            .andExpect(status().isBadRequest());

        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTaxes() throws Exception {
        // Initialize the database
        taxRepository.saveAndFlush(tax);

        // Get all the taxList
        restTaxMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tax.getId().intValue())))
            .andExpect(jsonPath("$.[*].netAmount").value(hasItem(sameNumber(DEFAULT_NET_AMOUNT))))
            .andExpect(jsonPath("$.[*].computedSlab").value(hasItem(DEFAULT_COMPUTED_SLAB)))
            .andExpect(jsonPath("$.[*].computedAmount").value(hasItem(sameNumber(DEFAULT_COMPUTED_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxType").value(hasItem(DEFAULT_TAX_TYPE)))
            .andExpect(jsonPath("$.[*].taxSource").value(hasItem(DEFAULT_TAX_SOURCE.toString())));
    }

    @Test
    @Transactional
    void getTax() throws Exception {
        // Initialize the database
        taxRepository.saveAndFlush(tax);

        // Get the tax
        restTaxMockMvc
            .perform(get(ENTITY_API_URL_ID, tax.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tax.getId().intValue()))
            .andExpect(jsonPath("$.netAmount").value(sameNumber(DEFAULT_NET_AMOUNT)))
            .andExpect(jsonPath("$.computedSlab").value(DEFAULT_COMPUTED_SLAB))
            .andExpect(jsonPath("$.computedAmount").value(sameNumber(DEFAULT_COMPUTED_AMOUNT)))
            .andExpect(jsonPath("$.taxType").value(DEFAULT_TAX_TYPE))
            .andExpect(jsonPath("$.taxSource").value(DEFAULT_TAX_SOURCE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTax() throws Exception {
        // Get the tax
        restTaxMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTax() throws Exception {
        // Initialize the database
        taxRepository.saveAndFlush(tax);

        int databaseSizeBeforeUpdate = taxRepository.findAll().size();
        taxSearchRepository.save(tax);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());

        // Update the tax
        Tax updatedTax = taxRepository.findById(tax.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTax are not directly saved in db
        em.detach(updatedTax);
        updatedTax
            .netAmount(UPDATED_NET_AMOUNT)
            .computedSlab(UPDATED_COMPUTED_SLAB)
            .computedAmount(UPDATED_COMPUTED_AMOUNT)
            .taxType(UPDATED_TAX_TYPE)
            .taxSource(UPDATED_TAX_SOURCE);
        TaxDTO taxDTO = taxMapper.toDto(updatedTax);

        restTaxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taxDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taxDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeUpdate);
        Tax testTax = taxList.get(taxList.size() - 1);
        assertThat(testTax.getNetAmount()).isEqualByComparingTo(UPDATED_NET_AMOUNT);
        assertThat(testTax.getComputedSlab()).isEqualTo(UPDATED_COMPUTED_SLAB);
        assertThat(testTax.getComputedAmount()).isEqualByComparingTo(UPDATED_COMPUTED_AMOUNT);
        assertThat(testTax.getTaxType()).isEqualTo(UPDATED_TAX_TYPE);
        assertThat(testTax.getTaxSource()).isEqualTo(UPDATED_TAX_SOURCE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Tax> taxSearchList = IterableUtils.toList(taxSearchRepository.findAll());
                Tax testTaxSearch = taxSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTaxSearch.getNetAmount()).isEqualByComparingTo(UPDATED_NET_AMOUNT);
                assertThat(testTaxSearch.getComputedSlab()).isEqualTo(UPDATED_COMPUTED_SLAB);
                assertThat(testTaxSearch.getComputedAmount()).isEqualByComparingTo(UPDATED_COMPUTED_AMOUNT);
                assertThat(testTaxSearch.getTaxType()).isEqualTo(UPDATED_TAX_TYPE);
                assertThat(testTaxSearch.getTaxSource()).isEqualTo(UPDATED_TAX_SOURCE);
            });
    }

    @Test
    @Transactional
    void putNonExistingTax() throws Exception {
        int databaseSizeBeforeUpdate = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        tax.setId(longCount.incrementAndGet());

        // Create the Tax
        TaxDTO taxDTO = taxMapper.toDto(tax);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taxDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taxDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTax() throws Exception {
        int databaseSizeBeforeUpdate = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        tax.setId(longCount.incrementAndGet());

        // Create the Tax
        TaxDTO taxDTO = taxMapper.toDto(tax);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaxMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taxDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTax() throws Exception {
        int databaseSizeBeforeUpdate = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        tax.setId(longCount.incrementAndGet());

        // Create the Tax
        TaxDTO taxDTO = taxMapper.toDto(tax);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaxMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taxDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTaxWithPatch() throws Exception {
        // Initialize the database
        taxRepository.saveAndFlush(tax);

        int databaseSizeBeforeUpdate = taxRepository.findAll().size();

        // Update the tax using partial update
        Tax partialUpdatedTax = new Tax();
        partialUpdatedTax.setId(tax.getId());

        partialUpdatedTax.netAmount(UPDATED_NET_AMOUNT).computedSlab(UPDATED_COMPUTED_SLAB);

        restTaxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTax.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTax))
            )
            .andExpect(status().isOk());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeUpdate);
        Tax testTax = taxList.get(taxList.size() - 1);
        assertThat(testTax.getNetAmount()).isEqualByComparingTo(UPDATED_NET_AMOUNT);
        assertThat(testTax.getComputedSlab()).isEqualTo(UPDATED_COMPUTED_SLAB);
        assertThat(testTax.getComputedAmount()).isEqualByComparingTo(DEFAULT_COMPUTED_AMOUNT);
        assertThat(testTax.getTaxType()).isEqualTo(DEFAULT_TAX_TYPE);
        assertThat(testTax.getTaxSource()).isEqualTo(DEFAULT_TAX_SOURCE);
    }

    @Test
    @Transactional
    void fullUpdateTaxWithPatch() throws Exception {
        // Initialize the database
        taxRepository.saveAndFlush(tax);

        int databaseSizeBeforeUpdate = taxRepository.findAll().size();

        // Update the tax using partial update
        Tax partialUpdatedTax = new Tax();
        partialUpdatedTax.setId(tax.getId());

        partialUpdatedTax
            .netAmount(UPDATED_NET_AMOUNT)
            .computedSlab(UPDATED_COMPUTED_SLAB)
            .computedAmount(UPDATED_COMPUTED_AMOUNT)
            .taxType(UPDATED_TAX_TYPE)
            .taxSource(UPDATED_TAX_SOURCE);

        restTaxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTax.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTax))
            )
            .andExpect(status().isOk());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeUpdate);
        Tax testTax = taxList.get(taxList.size() - 1);
        assertThat(testTax.getNetAmount()).isEqualByComparingTo(UPDATED_NET_AMOUNT);
        assertThat(testTax.getComputedSlab()).isEqualTo(UPDATED_COMPUTED_SLAB);
        assertThat(testTax.getComputedAmount()).isEqualByComparingTo(UPDATED_COMPUTED_AMOUNT);
        assertThat(testTax.getTaxType()).isEqualTo(UPDATED_TAX_TYPE);
        assertThat(testTax.getTaxSource()).isEqualTo(UPDATED_TAX_SOURCE);
    }

    @Test
    @Transactional
    void patchNonExistingTax() throws Exception {
        int databaseSizeBeforeUpdate = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        tax.setId(longCount.incrementAndGet());

        // Create the Tax
        TaxDTO taxDTO = taxMapper.toDto(tax);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taxDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taxDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTax() throws Exception {
        int databaseSizeBeforeUpdate = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        tax.setId(longCount.incrementAndGet());

        // Create the Tax
        TaxDTO taxDTO = taxMapper.toDto(tax);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaxMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taxDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTax() throws Exception {
        int databaseSizeBeforeUpdate = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        tax.setId(longCount.incrementAndGet());

        // Create the Tax
        TaxDTO taxDTO = taxMapper.toDto(tax);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaxMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(taxDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tax in the database
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTax() throws Exception {
        // Initialize the database
        taxRepository.saveAndFlush(tax);
        taxRepository.save(tax);
        taxSearchRepository.save(tax);

        int databaseSizeBeforeDelete = taxRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the tax
        restTaxMockMvc.perform(delete(ENTITY_API_URL_ID, tax.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tax> taxList = taxRepository.findAll();
        assertThat(taxList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(taxSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTax() throws Exception {
        // Initialize the database
        tax = taxRepository.saveAndFlush(tax);
        taxSearchRepository.save(tax);

        // Search the tax
        restTaxMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + tax.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tax.getId().intValue())))
            .andExpect(jsonPath("$.[*].netAmount").value(hasItem(sameNumber(DEFAULT_NET_AMOUNT))))
            .andExpect(jsonPath("$.[*].computedSlab").value(hasItem(DEFAULT_COMPUTED_SLAB)))
            .andExpect(jsonPath("$.[*].computedAmount").value(hasItem(sameNumber(DEFAULT_COMPUTED_AMOUNT))))
            .andExpect(jsonPath("$.[*].taxType").value(hasItem(DEFAULT_TAX_TYPE)))
            .andExpect(jsonPath("$.[*].taxSource").value(hasItem(DEFAULT_TAX_SOURCE.toString())));
    }
}
