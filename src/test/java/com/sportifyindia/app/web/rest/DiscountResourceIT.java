package com.sportifyindia.app.web.rest;

import static com.sportifyindia.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.Discount;
import com.sportifyindia.app.domain.enumeration.BusinessEntityEnum;
import com.sportifyindia.app.domain.enumeration.DiscountTypeEnum;
import com.sportifyindia.app.repository.DiscountRepository;
import com.sportifyindia.app.repository.search.DiscountSearchRepository;
import com.sportifyindia.app.service.dto.DiscountDTO;
import com.sportifyindia.app.service.mapper.DiscountMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link DiscountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DiscountResourceIT {

    private static final String DEFAULT_DISCOUNT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_DISCOUNT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DISCOUNT_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_DISCOUNT_TEXT = "BBBBBBBBBB";

    private static final DiscountTypeEnum DEFAULT_DISCOUNT_TYPE = DiscountTypeEnum.PERCENTAGE;
    private static final DiscountTypeEnum UPDATED_DISCOUNT_TYPE = DiscountTypeEnum.FIXED_AMOUNT;

    private static final BigDecimal DEFAULT_DISCOUNT_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_DISCOUNT_VALUE = new BigDecimal(2);

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BusinessEntityEnum DEFAULT_BE_TYPE = BusinessEntityEnum.SUBSCRIPTION;
    private static final BusinessEntityEnum UPDATED_BE_TYPE = BusinessEntityEnum.ONETIME_EVENT;

    private static final String ENTITY_API_URL = "/api/discounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/discounts/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountMapper discountMapper;

    @Autowired
    private DiscountSearchRepository discountSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDiscountMockMvc;

    private Discount discount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discount createEntity(EntityManager em) {
        Discount discount = new Discount()
            .discountCode(DEFAULT_DISCOUNT_CODE)
            .discountText(DEFAULT_DISCOUNT_TEXT)
            .discountType(DEFAULT_DISCOUNT_TYPE)
            .discountValue(DEFAULT_DISCOUNT_VALUE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .beType(DEFAULT_BE_TYPE);
        return discount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Discount createUpdatedEntity(EntityManager em) {
        Discount discount = new Discount()
            .discountCode(UPDATED_DISCOUNT_CODE)
            .discountText(UPDATED_DISCOUNT_TEXT)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .beType(UPDATED_BE_TYPE);
        return discount;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        discountSearchRepository.deleteAll();
        assertThat(discountSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        discount = createEntity(em);
    }

    @Test
    @Transactional
    void createDiscount() throws Exception {
        int databaseSizeBeforeCreate = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);
        restDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(discountDTO)))
            .andExpect(status().isCreated());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Discount testDiscount = discountList.get(discountList.size() - 1);
        assertThat(testDiscount.getDiscountCode()).isEqualTo(DEFAULT_DISCOUNT_CODE);
        assertThat(testDiscount.getDiscountText()).isEqualTo(DEFAULT_DISCOUNT_TEXT);
        assertThat(testDiscount.getDiscountType()).isEqualTo(DEFAULT_DISCOUNT_TYPE);
        assertThat(testDiscount.getDiscountValue()).isEqualByComparingTo(DEFAULT_DISCOUNT_VALUE);
        assertThat(testDiscount.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testDiscount.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testDiscount.getBeType()).isEqualTo(DEFAULT_BE_TYPE);
    }

    @Test
    @Transactional
    void createDiscountWithExistingId() throws Exception {
        // Create the Discount with an existing ID
        discount.setId(1L);
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        int databaseSizeBeforeCreate = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(discountDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDiscountCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        // set the field null
        discount.setDiscountCode(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        restDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(discountDTO)))
            .andExpect(status().isBadRequest());

        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDiscountTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        // set the field null
        discount.setDiscountType(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        restDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(discountDTO)))
            .andExpect(status().isBadRequest());

        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDiscountValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        // set the field null
        discount.setDiscountValue(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        restDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(discountDTO)))
            .andExpect(status().isBadRequest());

        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        // set the field null
        discount.setStartDate(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        restDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(discountDTO)))
            .andExpect(status().isBadRequest());

        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        // set the field null
        discount.setEndDate(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        restDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(discountDTO)))
            .andExpect(status().isBadRequest());

        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkBeTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        // set the field null
        discount.setBeType(null);

        // Create the Discount, which fails.
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        restDiscountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(discountDTO)))
            .andExpect(status().isBadRequest());

        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDiscounts() throws Exception {
        // Initialize the database
        discountRepository.saveAndFlush(discount);

        // Get all the discountList
        restDiscountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(discount.getId().intValue())))
            .andExpect(jsonPath("$.[*].discountCode").value(hasItem(DEFAULT_DISCOUNT_CODE)))
            .andExpect(jsonPath("$.[*].discountText").value(hasItem(DEFAULT_DISCOUNT_TEXT)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].beType").value(hasItem(DEFAULT_BE_TYPE.toString())));
    }

    @Test
    @Transactional
    void getDiscount() throws Exception {
        // Initialize the database
        discountRepository.saveAndFlush(discount);

        // Get the discount
        restDiscountMockMvc
            .perform(get(ENTITY_API_URL_ID, discount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(discount.getId().intValue()))
            .andExpect(jsonPath("$.discountCode").value(DEFAULT_DISCOUNT_CODE))
            .andExpect(jsonPath("$.discountText").value(DEFAULT_DISCOUNT_TEXT))
            .andExpect(jsonPath("$.discountType").value(DEFAULT_DISCOUNT_TYPE.toString()))
            .andExpect(jsonPath("$.discountValue").value(sameNumber(DEFAULT_DISCOUNT_VALUE)))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.beType").value(DEFAULT_BE_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDiscount() throws Exception {
        // Get the discount
        restDiscountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDiscount() throws Exception {
        // Initialize the database
        discountRepository.saveAndFlush(discount);

        int databaseSizeBeforeUpdate = discountRepository.findAll().size();
        discountSearchRepository.save(discount);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());

        // Update the discount
        Discount updatedDiscount = discountRepository.findById(discount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDiscount are not directly saved in db
        em.detach(updatedDiscount);
        updatedDiscount
            .discountCode(UPDATED_DISCOUNT_CODE)
            .discountText(UPDATED_DISCOUNT_TEXT)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .beType(UPDATED_BE_TYPE);
        DiscountDTO discountDTO = discountMapper.toDto(updatedDiscount);

        restDiscountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, discountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(discountDTO))
            )
            .andExpect(status().isOk());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeUpdate);
        Discount testDiscount = discountList.get(discountList.size() - 1);
        assertThat(testDiscount.getDiscountCode()).isEqualTo(UPDATED_DISCOUNT_CODE);
        assertThat(testDiscount.getDiscountText()).isEqualTo(UPDATED_DISCOUNT_TEXT);
        assertThat(testDiscount.getDiscountType()).isEqualTo(UPDATED_DISCOUNT_TYPE);
        assertThat(testDiscount.getDiscountValue()).isEqualByComparingTo(UPDATED_DISCOUNT_VALUE);
        assertThat(testDiscount.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testDiscount.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testDiscount.getBeType()).isEqualTo(UPDATED_BE_TYPE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Discount> discountSearchList = IterableUtils.toList(discountSearchRepository.findAll());
                Discount testDiscountSearch = discountSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testDiscountSearch.getDiscountCode()).isEqualTo(UPDATED_DISCOUNT_CODE);
                assertThat(testDiscountSearch.getDiscountText()).isEqualTo(UPDATED_DISCOUNT_TEXT);
                assertThat(testDiscountSearch.getDiscountType()).isEqualTo(UPDATED_DISCOUNT_TYPE);
                assertThat(testDiscountSearch.getDiscountValue()).isEqualByComparingTo(UPDATED_DISCOUNT_VALUE);
                assertThat(testDiscountSearch.getStartDate()).isEqualTo(UPDATED_START_DATE);
                assertThat(testDiscountSearch.getEndDate()).isEqualTo(UPDATED_END_DATE);
                assertThat(testDiscountSearch.getBeType()).isEqualTo(UPDATED_BE_TYPE);
            });
    }

    @Test
    @Transactional
    void putNonExistingDiscount() throws Exception {
        int databaseSizeBeforeUpdate = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        discount.setId(longCount.incrementAndGet());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, discountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(discountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDiscount() throws Exception {
        int databaseSizeBeforeUpdate = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        discount.setId(longCount.incrementAndGet());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(discountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDiscount() throws Exception {
        int databaseSizeBeforeUpdate = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        discount.setId(longCount.incrementAndGet());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(discountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDiscountWithPatch() throws Exception {
        // Initialize the database
        discountRepository.saveAndFlush(discount);

        int databaseSizeBeforeUpdate = discountRepository.findAll().size();

        // Update the discount using partial update
        Discount partialUpdatedDiscount = new Discount();
        partialUpdatedDiscount.setId(discount.getId());

        partialUpdatedDiscount.discountText(UPDATED_DISCOUNT_TEXT).endDate(UPDATED_END_DATE);

        restDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiscount))
            )
            .andExpect(status().isOk());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeUpdate);
        Discount testDiscount = discountList.get(discountList.size() - 1);
        assertThat(testDiscount.getDiscountCode()).isEqualTo(DEFAULT_DISCOUNT_CODE);
        assertThat(testDiscount.getDiscountText()).isEqualTo(UPDATED_DISCOUNT_TEXT);
        assertThat(testDiscount.getDiscountType()).isEqualTo(DEFAULT_DISCOUNT_TYPE);
        assertThat(testDiscount.getDiscountValue()).isEqualByComparingTo(DEFAULT_DISCOUNT_VALUE);
        assertThat(testDiscount.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testDiscount.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testDiscount.getBeType()).isEqualTo(DEFAULT_BE_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateDiscountWithPatch() throws Exception {
        // Initialize the database
        discountRepository.saveAndFlush(discount);

        int databaseSizeBeforeUpdate = discountRepository.findAll().size();

        // Update the discount using partial update
        Discount partialUpdatedDiscount = new Discount();
        partialUpdatedDiscount.setId(discount.getId());

        partialUpdatedDiscount
            .discountCode(UPDATED_DISCOUNT_CODE)
            .discountText(UPDATED_DISCOUNT_TEXT)
            .discountType(UPDATED_DISCOUNT_TYPE)
            .discountValue(UPDATED_DISCOUNT_VALUE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .beType(UPDATED_BE_TYPE);

        restDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDiscount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDiscount))
            )
            .andExpect(status().isOk());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeUpdate);
        Discount testDiscount = discountList.get(discountList.size() - 1);
        assertThat(testDiscount.getDiscountCode()).isEqualTo(UPDATED_DISCOUNT_CODE);
        assertThat(testDiscount.getDiscountText()).isEqualTo(UPDATED_DISCOUNT_TEXT);
        assertThat(testDiscount.getDiscountType()).isEqualTo(UPDATED_DISCOUNT_TYPE);
        assertThat(testDiscount.getDiscountValue()).isEqualByComparingTo(UPDATED_DISCOUNT_VALUE);
        assertThat(testDiscount.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testDiscount.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testDiscount.getBeType()).isEqualTo(UPDATED_BE_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingDiscount() throws Exception {
        int databaseSizeBeforeUpdate = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        discount.setId(longCount.incrementAndGet());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, discountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(discountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDiscount() throws Exception {
        int databaseSizeBeforeUpdate = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        discount.setId(longCount.incrementAndGet());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(discountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDiscount() throws Exception {
        int databaseSizeBeforeUpdate = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        discount.setId(longCount.incrementAndGet());

        // Create the Discount
        DiscountDTO discountDTO = discountMapper.toDto(discount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDiscountMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(discountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Discount in the database
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDiscount() throws Exception {
        // Initialize the database
        discountRepository.saveAndFlush(discount);
        discountRepository.save(discount);
        discountSearchRepository.save(discount);

        int databaseSizeBeforeDelete = discountRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the discount
        restDiscountMockMvc
            .perform(delete(ENTITY_API_URL_ID, discount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Discount> discountList = discountRepository.findAll();
        assertThat(discountList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(discountSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDiscount() throws Exception {
        // Initialize the database
        discount = discountRepository.saveAndFlush(discount);
        discountSearchRepository.save(discount);

        // Search the discount
        restDiscountMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + discount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(discount.getId().intValue())))
            .andExpect(jsonPath("$.[*].discountCode").value(hasItem(DEFAULT_DISCOUNT_CODE)))
            .andExpect(jsonPath("$.[*].discountText").value(hasItem(DEFAULT_DISCOUNT_TEXT)))
            .andExpect(jsonPath("$.[*].discountType").value(hasItem(DEFAULT_DISCOUNT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].discountValue").value(hasItem(sameNumber(DEFAULT_DISCOUNT_VALUE))))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].beType").value(hasItem(DEFAULT_BE_TYPE.toString())));
    }
}
