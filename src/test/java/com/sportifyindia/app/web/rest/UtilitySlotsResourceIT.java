package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.UtilitySlots;
import com.sportifyindia.app.domain.enumeration.UtilitySlotStatusEnum;
import com.sportifyindia.app.repository.UtilitySlotsRepository;
import com.sportifyindia.app.repository.search.UtilitySlotsSearchRepository;
import com.sportifyindia.app.service.dto.UtilitySlotsDTO;
import com.sportifyindia.app.service.mapper.UtilitySlotsMapper;
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
 * Integration tests for the {@link UtilitySlotsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UtilitySlotsResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_MAX_CAPACITY = 1;
    private static final Integer UPDATED_MAX_CAPACITY = 2;

    private static final Integer DEFAULT_CURRENT_BOOKINGS = 1;
    private static final Integer UPDATED_CURRENT_BOOKINGS = 2;

    private static final UtilitySlotStatusEnum DEFAULT_STATUS = UtilitySlotStatusEnum.OPEN;
    private static final UtilitySlotStatusEnum UPDATED_STATUS = UtilitySlotStatusEnum.BOOKED;

    private static final String ENTITY_API_URL = "/api/utility-slots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/utility-slots/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UtilitySlotsRepository utilitySlotsRepository;

    @Autowired
    private UtilitySlotsMapper utilitySlotsMapper;

    @Autowired
    private UtilitySlotsSearchRepository utilitySlotsSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUtilitySlotsMockMvc;

    private UtilitySlots utilitySlots;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UtilitySlots createEntity(EntityManager em) {
        UtilitySlots utilitySlots = new UtilitySlots()
            .date(DEFAULT_DATE)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .maxCapacity(DEFAULT_MAX_CAPACITY)
            .currentBookings(DEFAULT_CURRENT_BOOKINGS)
            .status(DEFAULT_STATUS);
        return utilitySlots;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UtilitySlots createUpdatedEntity(EntityManager em) {
        UtilitySlots utilitySlots = new UtilitySlots()
            .date(UPDATED_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .currentBookings(UPDATED_CURRENT_BOOKINGS)
            .status(UPDATED_STATUS);
        return utilitySlots;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        utilitySlotsSearchRepository.deleteAll();
        assertThat(utilitySlotsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        utilitySlots = createEntity(em);
    }

    @Test
    @Transactional
    void createUtilitySlots() throws Exception {
        int databaseSizeBeforeCreate = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        // Create the UtilitySlots
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);
        restUtilitySlotsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        UtilitySlots testUtilitySlots = utilitySlotsList.get(utilitySlotsList.size() - 1);
        assertThat(testUtilitySlots.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testUtilitySlots.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testUtilitySlots.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testUtilitySlots.getMaxCapacity()).isEqualTo(DEFAULT_MAX_CAPACITY);
        assertThat(testUtilitySlots.getCurrentBookings()).isEqualTo(DEFAULT_CURRENT_BOOKINGS);
        assertThat(testUtilitySlots.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createUtilitySlotsWithExistingId() throws Exception {
        // Create the UtilitySlots with an existing ID
        utilitySlots.setId(1L);
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        int databaseSizeBeforeCreate = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUtilitySlotsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        // set the field null
        utilitySlots.setDate(null);

        // Create the UtilitySlots, which fails.
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        restUtilitySlotsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        // set the field null
        utilitySlots.setStartTime(null);

        // Create the UtilitySlots, which fails.
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        restUtilitySlotsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        // set the field null
        utilitySlots.setEndTime(null);

        // Create the UtilitySlots, which fails.
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        restUtilitySlotsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMaxCapacityIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        // set the field null
        utilitySlots.setMaxCapacity(null);

        // Create the UtilitySlots, which fails.
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        restUtilitySlotsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCurrentBookingsIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        // set the field null
        utilitySlots.setCurrentBookings(null);

        // Create the UtilitySlots, which fails.
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        restUtilitySlotsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        // set the field null
        utilitySlots.setStatus(null);

        // Create the UtilitySlots, which fails.
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        restUtilitySlotsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUtilitySlots() throws Exception {
        // Initialize the database
        utilitySlotsRepository.saveAndFlush(utilitySlots);

        // Get all the utilitySlotsList
        restUtilitySlotsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilitySlots.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].maxCapacity").value(hasItem(DEFAULT_MAX_CAPACITY)))
            .andExpect(jsonPath("$.[*].currentBookings").value(hasItem(DEFAULT_CURRENT_BOOKINGS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getUtilitySlots() throws Exception {
        // Initialize the database
        utilitySlotsRepository.saveAndFlush(utilitySlots);

        // Get the utilitySlots
        restUtilitySlotsMockMvc
            .perform(get(ENTITY_API_URL_ID, utilitySlots.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(utilitySlots.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.maxCapacity").value(DEFAULT_MAX_CAPACITY))
            .andExpect(jsonPath("$.currentBookings").value(DEFAULT_CURRENT_BOOKINGS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUtilitySlots() throws Exception {
        // Get the utilitySlots
        restUtilitySlotsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUtilitySlots() throws Exception {
        // Initialize the database
        utilitySlotsRepository.saveAndFlush(utilitySlots);

        int databaseSizeBeforeUpdate = utilitySlotsRepository.findAll().size();
        utilitySlotsSearchRepository.save(utilitySlots);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());

        // Update the utilitySlots
        UtilitySlots updatedUtilitySlots = utilitySlotsRepository.findById(utilitySlots.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUtilitySlots are not directly saved in db
        em.detach(updatedUtilitySlots);
        updatedUtilitySlots
            .date(UPDATED_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .currentBookings(UPDATED_CURRENT_BOOKINGS)
            .status(UPDATED_STATUS);
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(updatedUtilitySlots);

        restUtilitySlotsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilitySlotsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isOk());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeUpdate);
        UtilitySlots testUtilitySlots = utilitySlotsList.get(utilitySlotsList.size() - 1);
        assertThat(testUtilitySlots.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testUtilitySlots.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testUtilitySlots.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testUtilitySlots.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
        assertThat(testUtilitySlots.getCurrentBookings()).isEqualTo(UPDATED_CURRENT_BOOKINGS);
        assertThat(testUtilitySlots.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UtilitySlots> utilitySlotsSearchList = IterableUtils.toList(utilitySlotsSearchRepository.findAll());
                UtilitySlots testUtilitySlotsSearch = utilitySlotsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testUtilitySlotsSearch.getDate()).isEqualTo(UPDATED_DATE);
                assertThat(testUtilitySlotsSearch.getStartTime()).isEqualTo(UPDATED_START_TIME);
                assertThat(testUtilitySlotsSearch.getEndTime()).isEqualTo(UPDATED_END_TIME);
                assertThat(testUtilitySlotsSearch.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
                assertThat(testUtilitySlotsSearch.getCurrentBookings()).isEqualTo(UPDATED_CURRENT_BOOKINGS);
                assertThat(testUtilitySlotsSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingUtilitySlots() throws Exception {
        int databaseSizeBeforeUpdate = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        utilitySlots.setId(longCount.incrementAndGet());

        // Create the UtilitySlots
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilitySlotsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilitySlotsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUtilitySlots() throws Exception {
        int databaseSizeBeforeUpdate = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        utilitySlots.setId(longCount.incrementAndGet());

        // Create the UtilitySlots
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilitySlotsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUtilitySlots() throws Exception {
        int databaseSizeBeforeUpdate = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        utilitySlots.setId(longCount.incrementAndGet());

        // Create the UtilitySlots
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilitySlotsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUtilitySlotsWithPatch() throws Exception {
        // Initialize the database
        utilitySlotsRepository.saveAndFlush(utilitySlots);

        int databaseSizeBeforeUpdate = utilitySlotsRepository.findAll().size();

        // Update the utilitySlots using partial update
        UtilitySlots partialUpdatedUtilitySlots = new UtilitySlots();
        partialUpdatedUtilitySlots.setId(utilitySlots.getId());

        partialUpdatedUtilitySlots.date(UPDATED_DATE).currentBookings(UPDATED_CURRENT_BOOKINGS).status(UPDATED_STATUS);

        restUtilitySlotsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtilitySlots.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilitySlots))
            )
            .andExpect(status().isOk());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeUpdate);
        UtilitySlots testUtilitySlots = utilitySlotsList.get(utilitySlotsList.size() - 1);
        assertThat(testUtilitySlots.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testUtilitySlots.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testUtilitySlots.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testUtilitySlots.getMaxCapacity()).isEqualTo(DEFAULT_MAX_CAPACITY);
        assertThat(testUtilitySlots.getCurrentBookings()).isEqualTo(UPDATED_CURRENT_BOOKINGS);
        assertThat(testUtilitySlots.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateUtilitySlotsWithPatch() throws Exception {
        // Initialize the database
        utilitySlotsRepository.saveAndFlush(utilitySlots);

        int databaseSizeBeforeUpdate = utilitySlotsRepository.findAll().size();

        // Update the utilitySlots using partial update
        UtilitySlots partialUpdatedUtilitySlots = new UtilitySlots();
        partialUpdatedUtilitySlots.setId(utilitySlots.getId());

        partialUpdatedUtilitySlots
            .date(UPDATED_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .currentBookings(UPDATED_CURRENT_BOOKINGS)
            .status(UPDATED_STATUS);

        restUtilitySlotsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtilitySlots.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtilitySlots))
            )
            .andExpect(status().isOk());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeUpdate);
        UtilitySlots testUtilitySlots = utilitySlotsList.get(utilitySlotsList.size() - 1);
        assertThat(testUtilitySlots.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testUtilitySlots.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testUtilitySlots.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testUtilitySlots.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
        assertThat(testUtilitySlots.getCurrentBookings()).isEqualTo(UPDATED_CURRENT_BOOKINGS);
        assertThat(testUtilitySlots.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingUtilitySlots() throws Exception {
        int databaseSizeBeforeUpdate = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        utilitySlots.setId(longCount.incrementAndGet());

        // Create the UtilitySlots
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilitySlotsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, utilitySlotsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUtilitySlots() throws Exception {
        int databaseSizeBeforeUpdate = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        utilitySlots.setId(longCount.incrementAndGet());

        // Create the UtilitySlots
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilitySlotsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUtilitySlots() throws Exception {
        int databaseSizeBeforeUpdate = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        utilitySlots.setId(longCount.incrementAndGet());

        // Create the UtilitySlots
        UtilitySlotsDTO utilitySlotsDTO = utilitySlotsMapper.toDto(utilitySlots);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilitySlotsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilitySlotsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the UtilitySlots in the database
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUtilitySlots() throws Exception {
        // Initialize the database
        utilitySlotsRepository.saveAndFlush(utilitySlots);
        utilitySlotsRepository.save(utilitySlots);
        utilitySlotsSearchRepository.save(utilitySlots);

        int databaseSizeBeforeDelete = utilitySlotsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the utilitySlots
        restUtilitySlotsMockMvc
            .perform(delete(ENTITY_API_URL_ID, utilitySlots.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UtilitySlots> utilitySlotsList = utilitySlotsRepository.findAll();
        assertThat(utilitySlotsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySlotsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUtilitySlots() throws Exception {
        // Initialize the database
        utilitySlots = utilitySlotsRepository.saveAndFlush(utilitySlots);
        utilitySlotsSearchRepository.save(utilitySlots);

        // Search the utilitySlots
        restUtilitySlotsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + utilitySlots.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilitySlots.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].maxCapacity").value(hasItem(DEFAULT_MAX_CAPACITY)))
            .andExpect(jsonPath("$.[*].currentBookings").value(hasItem(DEFAULT_CURRENT_BOOKINGS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
