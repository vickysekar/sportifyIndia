package com.sportifyindia.app.web.rest;

import static com.sportifyindia.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.UtilityBookings;
import com.sportifyindia.app.domain.enumeration.BookingStatusEnum;
import com.sportifyindia.app.repository.UtilityBookingsRepository;
import com.sportifyindia.app.repository.search.UtilityBookingsSearchRepository;
import com.sportifyindia.app.service.dto.UtilityBookingsDTO;
import com.sportifyindia.app.service.mapper.UtilityBookingsMapper;
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
 * Integration tests for the {@link UtilityBookingsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UtilityBookingsResourceIT {

    private static final BigDecimal DEFAULT_AMOUNT_PAID = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT_PAID = new BigDecimal(2);

    private static final Integer DEFAULT_BOOKED_QUANTITY = 1;
    private static final Integer UPDATED_BOOKED_QUANTITY = 2;

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BookingStatusEnum DEFAULT_STATUS = BookingStatusEnum.CONFIRMED;
    private static final BookingStatusEnum UPDATED_STATUS = BookingStatusEnum.CANCELLED;

    private static final String ENTITY_API_URL = "/api/utility-bookings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/utility-bookings/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UtilityBookingsRepository utilityBookingsRepository;

    @Autowired
    private UtilityBookingsMapper utilityBookingsMapper;

    @Autowired
    private UtilityBookingsSearchRepository utilityBookingsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUtilityBookingsMockMvc;

    private UtilityBookings utilityBookings;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UtilityBookings createEntity(EntityManager em) {
        UtilityBookings utilityBookings = new UtilityBookings()
            .amountPaid(DEFAULT_AMOUNT_PAID)
            .bookedQuantity(DEFAULT_BOOKED_QUANTITY)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .status(DEFAULT_STATUS);
        return utilityBookings;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UtilityBookings createUpdatedEntity(EntityManager em) {
        UtilityBookings utilityBookings = new UtilityBookings()
            .amountPaid(UPDATED_AMOUNT_PAID)
            .bookedQuantity(UPDATED_BOOKED_QUANTITY)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .status(UPDATED_STATUS);
        return utilityBookings;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        utilityBookingsSearchRepository.deleteAll();
        assertThat(utilityBookingsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        utilityBookings = createEntity(em);
    }

    @Test
    @Transactional
    void createUtilityBookings() throws Exception {
        int databaseSizeBeforeCreate = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        // Create the UtilityBookings
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);
        restUtilityBookingsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        UtilityBookings testUtilityBookings = utilityBookingsList.get(utilityBookingsList.size() - 1);
        assertThat(testUtilityBookings.getAmountPaid()).isEqualByComparingTo(DEFAULT_AMOUNT_PAID);
        assertThat(testUtilityBookings.getBookedQuantity()).isEqualTo(DEFAULT_BOOKED_QUANTITY);
        assertThat(testUtilityBookings.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testUtilityBookings.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testUtilityBookings.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createUtilityBookingsWithExistingId() throws Exception {
        // Create the UtilityBookings with an existing ID
        utilityBookings.setId(1L);
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        int databaseSizeBeforeCreate = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUtilityBookingsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAmountPaidIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        // set the field null
        utilityBookings.setAmountPaid(null);

        // Create the UtilityBookings, which fails.
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        restUtilityBookingsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkBookedQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        // set the field null
        utilityBookings.setBookedQuantity(null);

        // Create the UtilityBookings, which fails.
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        restUtilityBookingsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        // set the field null
        utilityBookings.setStartTime(null);

        // Create the UtilityBookings, which fails.
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        restUtilityBookingsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        // set the field null
        utilityBookings.setEndTime(null);

        // Create the UtilityBookings, which fails.
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        restUtilityBookingsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        // set the field null
        utilityBookings.setStatus(null);

        // Create the UtilityBookings, which fails.
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        restUtilityBookingsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUtilityBookings() throws Exception {
        // Initialize the database
        utilityBookingsRepository.saveAndFlush(utilityBookings);

        // Get all the utilityBookingsList
        restUtilityBookingsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilityBookings.getId().intValue())))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].bookedQuantity").value(hasItem(DEFAULT_BOOKED_QUANTITY)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getUtilityBookings() throws Exception {
        // Initialize the database
        utilityBookingsRepository.saveAndFlush(utilityBookings);

        // Get the utilityBookings
        restUtilityBookingsMockMvc
            .perform(get(ENTITY_API_URL_ID, utilityBookings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(utilityBookings.getId().intValue()))
            .andExpect(jsonPath("$.amountPaid").value(sameNumber(DEFAULT_AMOUNT_PAID)))
            .andExpect(jsonPath("$.bookedQuantity").value(DEFAULT_BOOKED_QUANTITY))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUtilityBookings() throws Exception {
        // Get the utilityBookings
        restUtilityBookingsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUtilityBookings() throws Exception {
        // Initialize the database
        utilityBookingsRepository.saveAndFlush(utilityBookings);

        int databaseSizeBeforeUpdate = utilityBookingsRepository.findAll().size();
        utilityBookingsSearchRepository.save(utilityBookings);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());

        // Update the utilityBookings
        UtilityBookings updatedUtilityBookings = utilityBookingsRepository.findById(utilityBookings.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUtilityBookings are not directly saved in db
        em.detach(updatedUtilityBookings);
        updatedUtilityBookings
            .amountPaid(UPDATED_AMOUNT_PAID)
            .bookedQuantity(UPDATED_BOOKED_QUANTITY)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .status(UPDATED_STATUS);
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(updatedUtilityBookings);

        restUtilityBookingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilityBookingsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isOk());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeUpdate);
        UtilityBookings testUtilityBookings = utilityBookingsList.get(utilityBookingsList.size() - 1);
        assertThat(testUtilityBookings.getAmountPaid()).isEqualByComparingTo(UPDATED_AMOUNT_PAID);
        assertThat(testUtilityBookings.getBookedQuantity()).isEqualTo(UPDATED_BOOKED_QUANTITY);
        assertThat(testUtilityBookings.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testUtilityBookings.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testUtilityBookings.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UtilityBookings> utilityBookingsSearchList = IterableUtils.toList(utilityBookingsSearchRepository.findAll());
                UtilityBookings testUtilityBookingsSearch = utilityBookingsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testUtilityBookingsSearch.getAmountPaid()).isEqualByComparingTo(UPDATED_AMOUNT_PAID);
                assertThat(testUtilityBookingsSearch.getBookedQuantity()).isEqualTo(UPDATED_BOOKED_QUANTITY);
                assertThat(testUtilityBookingsSearch.getStartTime()).isEqualTo(UPDATED_START_TIME);
                assertThat(testUtilityBookingsSearch.getEndTime()).isEqualTo(UPDATED_END_TIME);
                assertThat(testUtilityBookingsSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingUtilityBookings() throws Exception {
        int databaseSizeBeforeUpdate = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        utilityBookings.setId(longCount.incrementAndGet());

        // Create the UtilityBookings
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilityBookingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilityBookingsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUtilityBookings() throws Exception {
        int databaseSizeBeforeUpdate = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        utilityBookings.setId(longCount.incrementAndGet());

        // Create the UtilityBookings
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityBookingsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUtilityBookings() throws Exception {
        int databaseSizeBeforeUpdate = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        utilityBookings.setId(longCount.incrementAndGet());

        // Create the UtilityBookings
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityBookingsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUtilityBookingsWithPatch() throws Exception {
        // Initialize the database
        utilityBookingsRepository.saveAndFlush(utilityBookings);

        int databaseSizeBeforeUpdate = utilityBookingsRepository.findAll().size();

        // Update the utilityBookings using partial update
        UtilityBookings partialUpdatedUtilityBookings = new UtilityBookings();
        partialUpdatedUtilityBookings.setId(utilityBookings.getId());

        partialUpdatedUtilityBookings.amountPaid(UPDATED_AMOUNT_PAID).bookedQuantity(UPDATED_BOOKED_QUANTITY).startTime(UPDATED_START_TIME);

        restUtilityBookingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtilityBookings.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilityBookings))
            )
            .andExpect(status().isOk());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeUpdate);
        UtilityBookings testUtilityBookings = utilityBookingsList.get(utilityBookingsList.size() - 1);
        assertThat(testUtilityBookings.getAmountPaid()).isEqualByComparingTo(UPDATED_AMOUNT_PAID);
        assertThat(testUtilityBookings.getBookedQuantity()).isEqualTo(UPDATED_BOOKED_QUANTITY);
        assertThat(testUtilityBookings.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testUtilityBookings.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testUtilityBookings.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateUtilityBookingsWithPatch() throws Exception {
        // Initialize the database
        utilityBookingsRepository.saveAndFlush(utilityBookings);

        int databaseSizeBeforeUpdate = utilityBookingsRepository.findAll().size();

        // Update the utilityBookings using partial update
        UtilityBookings partialUpdatedUtilityBookings = new UtilityBookings();
        partialUpdatedUtilityBookings.setId(utilityBookings.getId());

        partialUpdatedUtilityBookings
            .amountPaid(UPDATED_AMOUNT_PAID)
            .bookedQuantity(UPDATED_BOOKED_QUANTITY)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .status(UPDATED_STATUS);

        restUtilityBookingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtilityBookings.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilityBookings))
            )
            .andExpect(status().isOk());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeUpdate);
        UtilityBookings testUtilityBookings = utilityBookingsList.get(utilityBookingsList.size() - 1);
        assertThat(testUtilityBookings.getAmountPaid()).isEqualByComparingTo(UPDATED_AMOUNT_PAID);
        assertThat(testUtilityBookings.getBookedQuantity()).isEqualTo(UPDATED_BOOKED_QUANTITY);
        assertThat(testUtilityBookings.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testUtilityBookings.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testUtilityBookings.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingUtilityBookings() throws Exception {
        int databaseSizeBeforeUpdate = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        utilityBookings.setId(longCount.incrementAndGet());

        // Create the UtilityBookings
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilityBookingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, utilityBookingsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUtilityBookings() throws Exception {
        int databaseSizeBeforeUpdate = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        utilityBookings.setId(longCount.incrementAndGet());

        // Create the UtilityBookings
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityBookingsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUtilityBookings() throws Exception {
        int databaseSizeBeforeUpdate = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        utilityBookings.setId(longCount.incrementAndGet());

        // Create the UtilityBookings
        UtilityBookingsDTO utilityBookingsDTO = utilityBookingsMapper.toDto(utilityBookings);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityBookingsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityBookingsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UtilityBookings in the database
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUtilityBookings() throws Exception {
        // Initialize the database
        utilityBookingsRepository.saveAndFlush(utilityBookings);
        utilityBookingsRepository.save(utilityBookings);
        utilityBookingsSearchRepository.save(utilityBookings);

        int databaseSizeBeforeDelete = utilityBookingsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the utilityBookings
        restUtilityBookingsMockMvc
            .perform(delete(ENTITY_API_URL_ID, utilityBookings.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UtilityBookings> utilityBookingsList = utilityBookingsRepository.findAll();
        assertThat(utilityBookingsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilityBookingsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUtilityBookings() throws Exception {
        // Initialize the database
        utilityBookings = utilityBookingsRepository.saveAndFlush(utilityBookings);
        utilityBookingsSearchRepository.save(utilityBookings);

        // Search the utilityBookings
        restUtilityBookingsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + utilityBookings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilityBookings.getId().intValue())))
            .andExpect(jsonPath("$.[*].amountPaid").value(hasItem(sameNumber(DEFAULT_AMOUNT_PAID))))
            .andExpect(jsonPath("$.[*].bookedQuantity").value(hasItem(DEFAULT_BOOKED_QUANTITY)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
