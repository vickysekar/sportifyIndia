package com.sportifyindia.app.web.rest;

import static com.sportifyindia.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.Charge;
import com.sportifyindia.app.domain.enumeration.BusinessEntityEnum;
import com.sportifyindia.app.repository.ChargeRepository;
import com.sportifyindia.app.repository.search.ChargeSearchRepository;
import com.sportifyindia.app.service.dto.ChargeDTO;
import com.sportifyindia.app.service.mapper.ChargeMapper;
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
 * Integration tests for the {@link ChargeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChargeResourceIT {

    private static final BusinessEntityEnum DEFAULT_BE_TYPE = BusinessEntityEnum.SUBSCRIPTION;
    private static final BusinessEntityEnum UPDATED_BE_TYPE = BusinessEntityEnum.ONETIME_EVENT;

    private static final BigDecimal DEFAULT_COMPUTED_CHARGE = new BigDecimal(1);
    private static final BigDecimal UPDATED_COMPUTED_CHARGE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_COMPUTED_DISCOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_COMPUTED_DISCOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_DISC_REASON = "AAAAAAAAAA";
    private static final String UPDATED_DISC_REASON = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_EXCHANGE_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_EXCHANGE_RATE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_FINAL_CHARGE = new BigDecimal(1);
    private static final BigDecimal UPDATED_FINAL_CHARGE = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/charges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/charges/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private ChargeMapper chargeMapper;

    @Autowired
    private ChargeSearchRepository chargeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChargeMockMvc;

    private Charge charge;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Charge createEntity(EntityManager em) {
        Charge charge = new Charge()
            .beType(DEFAULT_BE_TYPE)
            .computedCharge(DEFAULT_COMPUTED_CHARGE)
            .computedDiscount(DEFAULT_COMPUTED_DISCOUNT)
            .total(DEFAULT_TOTAL)
            .currency(DEFAULT_CURRENCY)
            .discReason(DEFAULT_DISC_REASON)
            .exchangeRate(DEFAULT_EXCHANGE_RATE)
            .finalCharge(DEFAULT_FINAL_CHARGE);
        return charge;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Charge createUpdatedEntity(EntityManager em) {
        Charge charge = new Charge()
            .beType(UPDATED_BE_TYPE)
            .computedCharge(UPDATED_COMPUTED_CHARGE)
            .computedDiscount(UPDATED_COMPUTED_DISCOUNT)
            .total(UPDATED_TOTAL)
            .currency(UPDATED_CURRENCY)
            .discReason(UPDATED_DISC_REASON)
            .exchangeRate(UPDATED_EXCHANGE_RATE)
            .finalCharge(UPDATED_FINAL_CHARGE);
        return charge;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        chargeSearchRepository.deleteAll();
        assertThat(chargeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        charge = createEntity(em);
    }

    @Test
    @Transactional
    void createCharge() throws Exception {
        int databaseSizeBeforeCreate = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        // Create the Charge
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);
        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chargeDTO)))
            .andExpect(status().isCreated());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Charge testCharge = chargeList.get(chargeList.size() - 1);
        assertThat(testCharge.getBeType()).isEqualTo(DEFAULT_BE_TYPE);
        assertThat(testCharge.getComputedCharge()).isEqualByComparingTo(DEFAULT_COMPUTED_CHARGE);
        assertThat(testCharge.getComputedDiscount()).isEqualByComparingTo(DEFAULT_COMPUTED_DISCOUNT);
        assertThat(testCharge.getTotal()).isEqualByComparingTo(DEFAULT_TOTAL);
        assertThat(testCharge.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testCharge.getDiscReason()).isEqualTo(DEFAULT_DISC_REASON);
        assertThat(testCharge.getExchangeRate()).isEqualByComparingTo(DEFAULT_EXCHANGE_RATE);
        assertThat(testCharge.getFinalCharge()).isEqualByComparingTo(DEFAULT_FINAL_CHARGE);
    }

    @Test
    @Transactional
    void createChargeWithExistingId() throws Exception {
        // Create the Charge with an existing ID
        charge.setId(1L);
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        int databaseSizeBeforeCreate = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chargeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkBeTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        // set the field null
        charge.setBeType(null);

        // Create the Charge, which fails.
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chargeDTO)))
            .andExpect(status().isBadRequest());

        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkComputedChargeIsRequired() throws Exception {
        int databaseSizeBeforeTest = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        // set the field null
        charge.setComputedCharge(null);

        // Create the Charge, which fails.
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chargeDTO)))
            .andExpect(status().isBadRequest());

        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTotalIsRequired() throws Exception {
        int databaseSizeBeforeTest = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        // set the field null
        charge.setTotal(null);

        // Create the Charge, which fails.
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chargeDTO)))
            .andExpect(status().isBadRequest());

        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        // set the field null
        charge.setCurrency(null);

        // Create the Charge, which fails.
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chargeDTO)))
            .andExpect(status().isBadRequest());

        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFinalChargeIsRequired() throws Exception {
        int databaseSizeBeforeTest = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        // set the field null
        charge.setFinalCharge(null);

        // Create the Charge, which fails.
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        restChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chargeDTO)))
            .andExpect(status().isBadRequest());

        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCharges() throws Exception {
        // Initialize the database
        chargeRepository.saveAndFlush(charge);

        // Get all the chargeList
        restChargeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(charge.getId().intValue())))
            .andExpect(jsonPath("$.[*].beType").value(hasItem(DEFAULT_BE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].computedCharge").value(hasItem(sameNumber(DEFAULT_COMPUTED_CHARGE))))
            .andExpect(jsonPath("$.[*].computedDiscount").value(hasItem(sameNumber(DEFAULT_COMPUTED_DISCOUNT))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].discReason").value(hasItem(DEFAULT_DISC_REASON)))
            .andExpect(jsonPath("$.[*].exchangeRate").value(hasItem(sameNumber(DEFAULT_EXCHANGE_RATE))))
            .andExpect(jsonPath("$.[*].finalCharge").value(hasItem(sameNumber(DEFAULT_FINAL_CHARGE))));
    }

    @Test
    @Transactional
    void getCharge() throws Exception {
        // Initialize the database
        chargeRepository.saveAndFlush(charge);

        // Get the charge
        restChargeMockMvc
            .perform(get(ENTITY_API_URL_ID, charge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(charge.getId().intValue()))
            .andExpect(jsonPath("$.beType").value(DEFAULT_BE_TYPE.toString()))
            .andExpect(jsonPath("$.computedCharge").value(sameNumber(DEFAULT_COMPUTED_CHARGE)))
            .andExpect(jsonPath("$.computedDiscount").value(sameNumber(DEFAULT_COMPUTED_DISCOUNT)))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.discReason").value(DEFAULT_DISC_REASON))
            .andExpect(jsonPath("$.exchangeRate").value(sameNumber(DEFAULT_EXCHANGE_RATE)))
            .andExpect(jsonPath("$.finalCharge").value(sameNumber(DEFAULT_FINAL_CHARGE)));
    }

    @Test
    @Transactional
    void getNonExistingCharge() throws Exception {
        // Get the charge
        restChargeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCharge() throws Exception {
        // Initialize the database
        chargeRepository.saveAndFlush(charge);

        int databaseSizeBeforeUpdate = chargeRepository.findAll().size();
        chargeSearchRepository.save(charge);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());

        // Update the charge
        Charge updatedCharge = chargeRepository.findById(charge.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCharge are not directly saved in db
        em.detach(updatedCharge);
        updatedCharge
            .beType(UPDATED_BE_TYPE)
            .computedCharge(UPDATED_COMPUTED_CHARGE)
            .computedDiscount(UPDATED_COMPUTED_DISCOUNT)
            .total(UPDATED_TOTAL)
            .currency(UPDATED_CURRENCY)
            .discReason(UPDATED_DISC_REASON)
            .exchangeRate(UPDATED_EXCHANGE_RATE)
            .finalCharge(UPDATED_FINAL_CHARGE);
        ChargeDTO chargeDTO = chargeMapper.toDto(updatedCharge);

        restChargeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chargeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(chargeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate);
        Charge testCharge = chargeList.get(chargeList.size() - 1);
        assertThat(testCharge.getBeType()).isEqualTo(UPDATED_BE_TYPE);
        assertThat(testCharge.getComputedCharge()).isEqualByComparingTo(UPDATED_COMPUTED_CHARGE);
        assertThat(testCharge.getComputedDiscount()).isEqualByComparingTo(UPDATED_COMPUTED_DISCOUNT);
        assertThat(testCharge.getTotal()).isEqualByComparingTo(UPDATED_TOTAL);
        assertThat(testCharge.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testCharge.getDiscReason()).isEqualTo(UPDATED_DISC_REASON);
        assertThat(testCharge.getExchangeRate()).isEqualByComparingTo(UPDATED_EXCHANGE_RATE);
        assertThat(testCharge.getFinalCharge()).isEqualByComparingTo(UPDATED_FINAL_CHARGE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Charge> chargeSearchList = IterableUtils.toList(chargeSearchRepository.findAll());
                Charge testChargeSearch = chargeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testChargeSearch.getBeType()).isEqualTo(UPDATED_BE_TYPE);
                assertThat(testChargeSearch.getComputedCharge()).isEqualByComparingTo(UPDATED_COMPUTED_CHARGE);
                assertThat(testChargeSearch.getComputedDiscount()).isEqualByComparingTo(UPDATED_COMPUTED_DISCOUNT);
                assertThat(testChargeSearch.getTotal()).isEqualByComparingTo(UPDATED_TOTAL);
                assertThat(testChargeSearch.getCurrency()).isEqualTo(UPDATED_CURRENCY);
                assertThat(testChargeSearch.getDiscReason()).isEqualTo(UPDATED_DISC_REASON);
                assertThat(testChargeSearch.getExchangeRate()).isEqualByComparingTo(UPDATED_EXCHANGE_RATE);
                assertThat(testChargeSearch.getFinalCharge()).isEqualByComparingTo(UPDATED_FINAL_CHARGE);
            });
    }

    @Test
    @Transactional
    void putNonExistingCharge() throws Exception {
        int databaseSizeBeforeUpdate = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        charge.setId(longCount.incrementAndGet());

        // Create the Charge
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chargeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(chargeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCharge() throws Exception {
        int databaseSizeBeforeUpdate = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        charge.setId(longCount.incrementAndGet());

        // Create the Charge
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(chargeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCharge() throws Exception {
        int databaseSizeBeforeUpdate = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        charge.setId(longCount.incrementAndGet());

        // Create the Charge
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(chargeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateChargeWithPatch() throws Exception {
        // Initialize the database
        chargeRepository.saveAndFlush(charge);

        int databaseSizeBeforeUpdate = chargeRepository.findAll().size();

        // Update the charge using partial update
        Charge partialUpdatedCharge = new Charge();
        partialUpdatedCharge.setId(charge.getId());

        partialUpdatedCharge
            .beType(UPDATED_BE_TYPE)
            .computedDiscount(UPDATED_COMPUTED_DISCOUNT)
            .currency(UPDATED_CURRENCY)
            .exchangeRate(UPDATED_EXCHANGE_RATE)
            .finalCharge(UPDATED_FINAL_CHARGE);

        restChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCharge.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCharge))
            )
            .andExpect(status().isOk());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate);
        Charge testCharge = chargeList.get(chargeList.size() - 1);
        assertThat(testCharge.getBeType()).isEqualTo(UPDATED_BE_TYPE);
        assertThat(testCharge.getComputedCharge()).isEqualByComparingTo(DEFAULT_COMPUTED_CHARGE);
        assertThat(testCharge.getComputedDiscount()).isEqualByComparingTo(UPDATED_COMPUTED_DISCOUNT);
        assertThat(testCharge.getTotal()).isEqualByComparingTo(DEFAULT_TOTAL);
        assertThat(testCharge.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testCharge.getDiscReason()).isEqualTo(DEFAULT_DISC_REASON);
        assertThat(testCharge.getExchangeRate()).isEqualByComparingTo(UPDATED_EXCHANGE_RATE);
        assertThat(testCharge.getFinalCharge()).isEqualByComparingTo(UPDATED_FINAL_CHARGE);
    }

    @Test
    @Transactional
    void fullUpdateChargeWithPatch() throws Exception {
        // Initialize the database
        chargeRepository.saveAndFlush(charge);

        int databaseSizeBeforeUpdate = chargeRepository.findAll().size();

        // Update the charge using partial update
        Charge partialUpdatedCharge = new Charge();
        partialUpdatedCharge.setId(charge.getId());

        partialUpdatedCharge
            .beType(UPDATED_BE_TYPE)
            .computedCharge(UPDATED_COMPUTED_CHARGE)
            .computedDiscount(UPDATED_COMPUTED_DISCOUNT)
            .total(UPDATED_TOTAL)
            .currency(UPDATED_CURRENCY)
            .discReason(UPDATED_DISC_REASON)
            .exchangeRate(UPDATED_EXCHANGE_RATE)
            .finalCharge(UPDATED_FINAL_CHARGE);

        restChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCharge.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCharge))
            )
            .andExpect(status().isOk());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate);
        Charge testCharge = chargeList.get(chargeList.size() - 1);
        assertThat(testCharge.getBeType()).isEqualTo(UPDATED_BE_TYPE);
        assertThat(testCharge.getComputedCharge()).isEqualByComparingTo(UPDATED_COMPUTED_CHARGE);
        assertThat(testCharge.getComputedDiscount()).isEqualByComparingTo(UPDATED_COMPUTED_DISCOUNT);
        assertThat(testCharge.getTotal()).isEqualByComparingTo(UPDATED_TOTAL);
        assertThat(testCharge.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testCharge.getDiscReason()).isEqualTo(UPDATED_DISC_REASON);
        assertThat(testCharge.getExchangeRate()).isEqualByComparingTo(UPDATED_EXCHANGE_RATE);
        assertThat(testCharge.getFinalCharge()).isEqualByComparingTo(UPDATED_FINAL_CHARGE);
    }

    @Test
    @Transactional
    void patchNonExistingCharge() throws Exception {
        int databaseSizeBeforeUpdate = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        charge.setId(longCount.incrementAndGet());

        // Create the Charge
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chargeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(chargeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCharge() throws Exception {
        int databaseSizeBeforeUpdate = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        charge.setId(longCount.incrementAndGet());

        // Create the Charge
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(chargeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCharge() throws Exception {
        int databaseSizeBeforeUpdate = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        charge.setId(longCount.incrementAndGet());

        // Create the Charge
        ChargeDTO chargeDTO = chargeMapper.toDto(charge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChargeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(chargeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Charge in the database
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCharge() throws Exception {
        // Initialize the database
        chargeRepository.saveAndFlush(charge);
        chargeRepository.save(charge);
        chargeSearchRepository.save(charge);

        int databaseSizeBeforeDelete = chargeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the charge
        restChargeMockMvc
            .perform(delete(ENTITY_API_URL_ID, charge.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Charge> chargeList = chargeRepository.findAll();
        assertThat(chargeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(chargeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCharge() throws Exception {
        // Initialize the database
        charge = chargeRepository.saveAndFlush(charge);
        chargeSearchRepository.save(charge);

        // Search the charge
        restChargeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + charge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(charge.getId().intValue())))
            .andExpect(jsonPath("$.[*].beType").value(hasItem(DEFAULT_BE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].computedCharge").value(hasItem(sameNumber(DEFAULT_COMPUTED_CHARGE))))
            .andExpect(jsonPath("$.[*].computedDiscount").value(hasItem(sameNumber(DEFAULT_COMPUTED_DISCOUNT))))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].discReason").value(hasItem(DEFAULT_DISC_REASON)))
            .andExpect(jsonPath("$.[*].exchangeRate").value(hasItem(sameNumber(DEFAULT_EXCHANGE_RATE))))
            .andExpect(jsonPath("$.[*].finalCharge").value(hasItem(sameNumber(DEFAULT_FINAL_CHARGE))));
    }
}
