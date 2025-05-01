package com.sportifyindia.app.web.rest;

import static com.sportifyindia.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.OneTimeEvent;
import com.sportifyindia.app.domain.enumeration.EventStatusEnum;
import com.sportifyindia.app.repository.OneTimeEventRepository;
import com.sportifyindia.app.repository.search.OneTimeEventSearchRepository;
import com.sportifyindia.app.service.dto.OneTimeEventDTO;
import com.sportifyindia.app.service.mapper.OneTimeEventMapper;
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
 * Integration tests for the {@link OneTimeEventResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OneTimeEventResourceIT {

    private static final String DEFAULT_EVENT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EVENT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_EVENT_DESC = "BBBBBBBBBB";

    private static final Double DEFAULT_EVENT_LATITUDE = 1D;
    private static final Double UPDATED_EVENT_LATITUDE = 2D;

    private static final Double DEFAULT_EVENT_LONGITUDE = 1D;
    private static final Double UPDATED_EVENT_LONGITUDE = 2D;

    private static final BigDecimal DEFAULT_ENTRY_FEE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ENTRY_FEE = new BigDecimal(2);

    private static final Integer DEFAULT_MAX_CAPACITY = 1;
    private static final Integer UPDATED_MAX_CAPACITY = 2;

    private static final Instant DEFAULT_EVENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EVENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_IMAGE_LINKS = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_LINKS = "BBBBBBBBBB";

    private static final EventStatusEnum DEFAULT_STATUS = EventStatusEnum.SCHEDULED;
    private static final EventStatusEnum UPDATED_STATUS = EventStatusEnum.ONGOING;

    private static final Instant DEFAULT_REGISTRATION_DEADLINE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REGISTRATION_DEADLINE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final String DEFAULT_TERMS_AND_CONDITIONS = "AAAAAAAAAA";
    private static final String UPDATED_TERMS_AND_CONDITIONS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/one-time-events";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/one-time-events/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OneTimeEventRepository oneTimeEventRepository;

    @Autowired
    private OneTimeEventMapper oneTimeEventMapper;

    @Autowired
    private OneTimeEventSearchRepository oneTimeEventSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOneTimeEventMockMvc;

    private OneTimeEvent oneTimeEvent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OneTimeEvent createEntity(EntityManager em) {
        OneTimeEvent oneTimeEvent = new OneTimeEvent()
            .eventName(DEFAULT_EVENT_NAME)
            .eventDesc(DEFAULT_EVENT_DESC)
            .eventLatitude(DEFAULT_EVENT_LATITUDE)
            .eventLongitude(DEFAULT_EVENT_LONGITUDE)
            .entryFee(DEFAULT_ENTRY_FEE)
            .maxCapacity(DEFAULT_MAX_CAPACITY)
            .eventDate(DEFAULT_EVENT_DATE)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .imageLinks(DEFAULT_IMAGE_LINKS)
            .status(DEFAULT_STATUS)
            .registrationDeadline(DEFAULT_REGISTRATION_DEADLINE)
            .category(DEFAULT_CATEGORY)
            .tags(DEFAULT_TAGS)
            .termsAndConditions(DEFAULT_TERMS_AND_CONDITIONS);
        return oneTimeEvent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OneTimeEvent createUpdatedEntity(EntityManager em) {
        OneTimeEvent oneTimeEvent = new OneTimeEvent()
            .eventName(UPDATED_EVENT_NAME)
            .eventDesc(UPDATED_EVENT_DESC)
            .eventLatitude(UPDATED_EVENT_LATITUDE)
            .eventLongitude(UPDATED_EVENT_LONGITUDE)
            .entryFee(UPDATED_ENTRY_FEE)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .eventDate(UPDATED_EVENT_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .imageLinks(UPDATED_IMAGE_LINKS)
            .status(UPDATED_STATUS)
            .registrationDeadline(UPDATED_REGISTRATION_DEADLINE)
            .category(UPDATED_CATEGORY)
            .tags(UPDATED_TAGS)
            .termsAndConditions(UPDATED_TERMS_AND_CONDITIONS);
        return oneTimeEvent;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        oneTimeEventSearchRepository.deleteAll();
        assertThat(oneTimeEventSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        oneTimeEvent = createEntity(em);
    }

    @Test
    @Transactional
    void createOneTimeEvent() throws Exception {
        int databaseSizeBeforeCreate = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // Create the OneTimeEvent
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);
        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isCreated());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        OneTimeEvent testOneTimeEvent = oneTimeEventList.get(oneTimeEventList.size() - 1);
        assertThat(testOneTimeEvent.getEventName()).isEqualTo(DEFAULT_EVENT_NAME);
        assertThat(testOneTimeEvent.getEventDesc()).isEqualTo(DEFAULT_EVENT_DESC);
        assertThat(testOneTimeEvent.getEventLatitude()).isEqualTo(DEFAULT_EVENT_LATITUDE);
        assertThat(testOneTimeEvent.getEventLongitude()).isEqualTo(DEFAULT_EVENT_LONGITUDE);
        assertThat(testOneTimeEvent.getEntryFee()).isEqualByComparingTo(DEFAULT_ENTRY_FEE);
        assertThat(testOneTimeEvent.getMaxCapacity()).isEqualTo(DEFAULT_MAX_CAPACITY);
        assertThat(testOneTimeEvent.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
        assertThat(testOneTimeEvent.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testOneTimeEvent.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testOneTimeEvent.getImageLinks()).isEqualTo(DEFAULT_IMAGE_LINKS);
        assertThat(testOneTimeEvent.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOneTimeEvent.getRegistrationDeadline()).isEqualTo(DEFAULT_REGISTRATION_DEADLINE);
        assertThat(testOneTimeEvent.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testOneTimeEvent.getTags()).isEqualTo(DEFAULT_TAGS);
        assertThat(testOneTimeEvent.getTermsAndConditions()).isEqualTo(DEFAULT_TERMS_AND_CONDITIONS);
    }

    @Test
    @Transactional
    void createOneTimeEventWithExistingId() throws Exception {
        // Create the OneTimeEvent with an existing ID
        oneTimeEvent.setId(1L);
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        int databaseSizeBeforeCreate = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEventNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // set the field null
        oneTimeEvent.setEventName(null);

        // Create the OneTimeEvent, which fails.
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEntryFeeIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // set the field null
        oneTimeEvent.setEntryFee(null);

        // Create the OneTimeEvent, which fails.
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMaxCapacityIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // set the field null
        oneTimeEvent.setMaxCapacity(null);

        // Create the OneTimeEvent, which fails.
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEventDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // set the field null
        oneTimeEvent.setEventDate(null);

        // Create the OneTimeEvent, which fails.
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // set the field null
        oneTimeEvent.setStartTime(null);

        // Create the OneTimeEvent, which fails.
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // set the field null
        oneTimeEvent.setEndTime(null);

        // Create the OneTimeEvent, which fails.
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // set the field null
        oneTimeEvent.setStatus(null);

        // Create the OneTimeEvent, which fails.
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRegistrationDeadlineIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // set the field null
        oneTimeEvent.setRegistrationDeadline(null);

        // Create the OneTimeEvent, which fails.
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        // set the field null
        oneTimeEvent.setCategory(null);

        // Create the OneTimeEvent, which fails.
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOneTimeEvents() throws Exception {
        // Initialize the database
        oneTimeEventRepository.saveAndFlush(oneTimeEvent);

        // Get all the oneTimeEventList
        restOneTimeEventMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oneTimeEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventName").value(hasItem(DEFAULT_EVENT_NAME)))
            .andExpect(jsonPath("$.[*].eventDesc").value(hasItem(DEFAULT_EVENT_DESC)))
            .andExpect(jsonPath("$.[*].eventLatitude").value(hasItem(DEFAULT_EVENT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].eventLongitude").value(hasItem(DEFAULT_EVENT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].entryFee").value(hasItem(sameNumber(DEFAULT_ENTRY_FEE))))
            .andExpect(jsonPath("$.[*].maxCapacity").value(hasItem(DEFAULT_MAX_CAPACITY)))
            .andExpect(jsonPath("$.[*].eventDate").value(hasItem(DEFAULT_EVENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].imageLinks").value(hasItem(DEFAULT_IMAGE_LINKS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].registrationDeadline").value(hasItem(DEFAULT_REGISTRATION_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].termsAndConditions").value(hasItem(DEFAULT_TERMS_AND_CONDITIONS)));
    }

    @Test
    @Transactional
    void getOneTimeEvent() throws Exception {
        // Initialize the database
        oneTimeEventRepository.saveAndFlush(oneTimeEvent);

        // Get the oneTimeEvent
        restOneTimeEventMockMvc
            .perform(get(ENTITY_API_URL_ID, oneTimeEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(oneTimeEvent.getId().intValue()))
            .andExpect(jsonPath("$.eventName").value(DEFAULT_EVENT_NAME))
            .andExpect(jsonPath("$.eventDesc").value(DEFAULT_EVENT_DESC))
            .andExpect(jsonPath("$.eventLatitude").value(DEFAULT_EVENT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.eventLongitude").value(DEFAULT_EVENT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.entryFee").value(sameNumber(DEFAULT_ENTRY_FEE)))
            .andExpect(jsonPath("$.maxCapacity").value(DEFAULT_MAX_CAPACITY))
            .andExpect(jsonPath("$.eventDate").value(DEFAULT_EVENT_DATE.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.imageLinks").value(DEFAULT_IMAGE_LINKS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.registrationDeadline").value(DEFAULT_REGISTRATION_DEADLINE.toString()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS))
            .andExpect(jsonPath("$.termsAndConditions").value(DEFAULT_TERMS_AND_CONDITIONS));
    }

    @Test
    @Transactional
    void getNonExistingOneTimeEvent() throws Exception {
        // Get the oneTimeEvent
        restOneTimeEventMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOneTimeEvent() throws Exception {
        // Initialize the database
        oneTimeEventRepository.saveAndFlush(oneTimeEvent);

        int databaseSizeBeforeUpdate = oneTimeEventRepository.findAll().size();
        oneTimeEventSearchRepository.save(oneTimeEvent);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());

        // Update the oneTimeEvent
        OneTimeEvent updatedOneTimeEvent = oneTimeEventRepository.findById(oneTimeEvent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOneTimeEvent are not directly saved in db
        em.detach(updatedOneTimeEvent);
        updatedOneTimeEvent
            .eventName(UPDATED_EVENT_NAME)
            .eventDesc(UPDATED_EVENT_DESC)
            .eventLatitude(UPDATED_EVENT_LATITUDE)
            .eventLongitude(UPDATED_EVENT_LONGITUDE)
            .entryFee(UPDATED_ENTRY_FEE)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .eventDate(UPDATED_EVENT_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .imageLinks(UPDATED_IMAGE_LINKS)
            .status(UPDATED_STATUS)
            .registrationDeadline(UPDATED_REGISTRATION_DEADLINE)
            .category(UPDATED_CATEGORY)
            .tags(UPDATED_TAGS)
            .termsAndConditions(UPDATED_TERMS_AND_CONDITIONS);
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(updatedOneTimeEvent);

        restOneTimeEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, oneTimeEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isOk());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeUpdate);
        OneTimeEvent testOneTimeEvent = oneTimeEventList.get(oneTimeEventList.size() - 1);
        assertThat(testOneTimeEvent.getEventName()).isEqualTo(UPDATED_EVENT_NAME);
        assertThat(testOneTimeEvent.getEventDesc()).isEqualTo(UPDATED_EVENT_DESC);
        assertThat(testOneTimeEvent.getEventLatitude()).isEqualTo(UPDATED_EVENT_LATITUDE);
        assertThat(testOneTimeEvent.getEventLongitude()).isEqualTo(UPDATED_EVENT_LONGITUDE);
        assertThat(testOneTimeEvent.getEntryFee()).isEqualByComparingTo(UPDATED_ENTRY_FEE);
        assertThat(testOneTimeEvent.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
        assertThat(testOneTimeEvent.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testOneTimeEvent.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testOneTimeEvent.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testOneTimeEvent.getImageLinks()).isEqualTo(UPDATED_IMAGE_LINKS);
        assertThat(testOneTimeEvent.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOneTimeEvent.getRegistrationDeadline()).isEqualTo(UPDATED_REGISTRATION_DEADLINE);
        assertThat(testOneTimeEvent.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testOneTimeEvent.getTags()).isEqualTo(UPDATED_TAGS);
        assertThat(testOneTimeEvent.getTermsAndConditions()).isEqualTo(UPDATED_TERMS_AND_CONDITIONS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<OneTimeEvent> oneTimeEventSearchList = IterableUtils.toList(oneTimeEventSearchRepository.findAll());
                OneTimeEvent testOneTimeEventSearch = oneTimeEventSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOneTimeEventSearch.getEventName()).isEqualTo(UPDATED_EVENT_NAME);
                assertThat(testOneTimeEventSearch.getEventDesc()).isEqualTo(UPDATED_EVENT_DESC);
                assertThat(testOneTimeEventSearch.getEventLatitude()).isEqualTo(UPDATED_EVENT_LATITUDE);
                assertThat(testOneTimeEventSearch.getEventLongitude()).isEqualTo(UPDATED_EVENT_LONGITUDE);
                assertThat(testOneTimeEventSearch.getEntryFee()).isEqualByComparingTo(UPDATED_ENTRY_FEE);
                assertThat(testOneTimeEventSearch.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
                assertThat(testOneTimeEventSearch.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
                assertThat(testOneTimeEventSearch.getStartTime()).isEqualTo(UPDATED_START_TIME);
                assertThat(testOneTimeEventSearch.getEndTime()).isEqualTo(UPDATED_END_TIME);
                assertThat(testOneTimeEventSearch.getImageLinks()).isEqualTo(UPDATED_IMAGE_LINKS);
                assertThat(testOneTimeEventSearch.getStatus()).isEqualTo(UPDATED_STATUS);
                assertThat(testOneTimeEventSearch.getRegistrationDeadline()).isEqualTo(UPDATED_REGISTRATION_DEADLINE);
                assertThat(testOneTimeEventSearch.getCategory()).isEqualTo(UPDATED_CATEGORY);
                assertThat(testOneTimeEventSearch.getTags()).isEqualTo(UPDATED_TAGS);
                assertThat(testOneTimeEventSearch.getTermsAndConditions()).isEqualTo(UPDATED_TERMS_AND_CONDITIONS);
            });
    }

    @Test
    @Transactional
    void putNonExistingOneTimeEvent() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        oneTimeEvent.setId(longCount.incrementAndGet());

        // Create the OneTimeEvent
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOneTimeEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, oneTimeEventDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOneTimeEvent() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        oneTimeEvent.setId(longCount.incrementAndGet());

        // Create the OneTimeEvent
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOneTimeEventMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOneTimeEvent() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        oneTimeEvent.setId(longCount.incrementAndGet());

        // Create the OneTimeEvent
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOneTimeEventMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOneTimeEventWithPatch() throws Exception {
        // Initialize the database
        oneTimeEventRepository.saveAndFlush(oneTimeEvent);

        int databaseSizeBeforeUpdate = oneTimeEventRepository.findAll().size();

        // Update the oneTimeEvent using partial update
        OneTimeEvent partialUpdatedOneTimeEvent = new OneTimeEvent();
        partialUpdatedOneTimeEvent.setId(oneTimeEvent.getId());

        partialUpdatedOneTimeEvent
            .eventName(UPDATED_EVENT_NAME)
            .eventLatitude(UPDATED_EVENT_LATITUDE)
            .eventLongitude(UPDATED_EVENT_LONGITUDE)
            .imageLinks(UPDATED_IMAGE_LINKS)
            .tags(UPDATED_TAGS);

        restOneTimeEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOneTimeEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOneTimeEvent))
            )
            .andExpect(status().isOk());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeUpdate);
        OneTimeEvent testOneTimeEvent = oneTimeEventList.get(oneTimeEventList.size() - 1);
        assertThat(testOneTimeEvent.getEventName()).isEqualTo(UPDATED_EVENT_NAME);
        assertThat(testOneTimeEvent.getEventDesc()).isEqualTo(DEFAULT_EVENT_DESC);
        assertThat(testOneTimeEvent.getEventLatitude()).isEqualTo(UPDATED_EVENT_LATITUDE);
        assertThat(testOneTimeEvent.getEventLongitude()).isEqualTo(UPDATED_EVENT_LONGITUDE);
        assertThat(testOneTimeEvent.getEntryFee()).isEqualByComparingTo(DEFAULT_ENTRY_FEE);
        assertThat(testOneTimeEvent.getMaxCapacity()).isEqualTo(DEFAULT_MAX_CAPACITY);
        assertThat(testOneTimeEvent.getEventDate()).isEqualTo(DEFAULT_EVENT_DATE);
        assertThat(testOneTimeEvent.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testOneTimeEvent.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testOneTimeEvent.getImageLinks()).isEqualTo(UPDATED_IMAGE_LINKS);
        assertThat(testOneTimeEvent.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOneTimeEvent.getRegistrationDeadline()).isEqualTo(DEFAULT_REGISTRATION_DEADLINE);
        assertThat(testOneTimeEvent.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testOneTimeEvent.getTags()).isEqualTo(UPDATED_TAGS);
        assertThat(testOneTimeEvent.getTermsAndConditions()).isEqualTo(DEFAULT_TERMS_AND_CONDITIONS);
    }

    @Test
    @Transactional
    void fullUpdateOneTimeEventWithPatch() throws Exception {
        // Initialize the database
        oneTimeEventRepository.saveAndFlush(oneTimeEvent);

        int databaseSizeBeforeUpdate = oneTimeEventRepository.findAll().size();

        // Update the oneTimeEvent using partial update
        OneTimeEvent partialUpdatedOneTimeEvent = new OneTimeEvent();
        partialUpdatedOneTimeEvent.setId(oneTimeEvent.getId());

        partialUpdatedOneTimeEvent
            .eventName(UPDATED_EVENT_NAME)
            .eventDesc(UPDATED_EVENT_DESC)
            .eventLatitude(UPDATED_EVENT_LATITUDE)
            .eventLongitude(UPDATED_EVENT_LONGITUDE)
            .entryFee(UPDATED_ENTRY_FEE)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .eventDate(UPDATED_EVENT_DATE)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .imageLinks(UPDATED_IMAGE_LINKS)
            .status(UPDATED_STATUS)
            .registrationDeadline(UPDATED_REGISTRATION_DEADLINE)
            .category(UPDATED_CATEGORY)
            .tags(UPDATED_TAGS)
            .termsAndConditions(UPDATED_TERMS_AND_CONDITIONS);

        restOneTimeEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOneTimeEvent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOneTimeEvent))
            )
            .andExpect(status().isOk());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeUpdate);
        OneTimeEvent testOneTimeEvent = oneTimeEventList.get(oneTimeEventList.size() - 1);
        assertThat(testOneTimeEvent.getEventName()).isEqualTo(UPDATED_EVENT_NAME);
        assertThat(testOneTimeEvent.getEventDesc()).isEqualTo(UPDATED_EVENT_DESC);
        assertThat(testOneTimeEvent.getEventLatitude()).isEqualTo(UPDATED_EVENT_LATITUDE);
        assertThat(testOneTimeEvent.getEventLongitude()).isEqualTo(UPDATED_EVENT_LONGITUDE);
        assertThat(testOneTimeEvent.getEntryFee()).isEqualByComparingTo(UPDATED_ENTRY_FEE);
        assertThat(testOneTimeEvent.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
        assertThat(testOneTimeEvent.getEventDate()).isEqualTo(UPDATED_EVENT_DATE);
        assertThat(testOneTimeEvent.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testOneTimeEvent.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testOneTimeEvent.getImageLinks()).isEqualTo(UPDATED_IMAGE_LINKS);
        assertThat(testOneTimeEvent.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOneTimeEvent.getRegistrationDeadline()).isEqualTo(UPDATED_REGISTRATION_DEADLINE);
        assertThat(testOneTimeEvent.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testOneTimeEvent.getTags()).isEqualTo(UPDATED_TAGS);
        assertThat(testOneTimeEvent.getTermsAndConditions()).isEqualTo(UPDATED_TERMS_AND_CONDITIONS);
    }

    @Test
    @Transactional
    void patchNonExistingOneTimeEvent() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        oneTimeEvent.setId(longCount.incrementAndGet());

        // Create the OneTimeEvent
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOneTimeEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, oneTimeEventDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOneTimeEvent() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        oneTimeEvent.setId(longCount.incrementAndGet());

        // Create the OneTimeEvent
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOneTimeEventMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOneTimeEvent() throws Exception {
        int databaseSizeBeforeUpdate = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        oneTimeEvent.setId(longCount.incrementAndGet());

        // Create the OneTimeEvent
        OneTimeEventDTO oneTimeEventDTO = oneTimeEventMapper.toDto(oneTimeEvent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOneTimeEventMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(oneTimeEventDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OneTimeEvent in the database
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOneTimeEvent() throws Exception {
        // Initialize the database
        oneTimeEventRepository.saveAndFlush(oneTimeEvent);
        oneTimeEventRepository.save(oneTimeEvent);
        oneTimeEventSearchRepository.save(oneTimeEvent);

        int databaseSizeBeforeDelete = oneTimeEventRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the oneTimeEvent
        restOneTimeEventMockMvc
            .perform(delete(ENTITY_API_URL_ID, oneTimeEvent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OneTimeEvent> oneTimeEventList = oneTimeEventRepository.findAll();
        assertThat(oneTimeEventList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(oneTimeEventSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOneTimeEvent() throws Exception {
        // Initialize the database
        oneTimeEvent = oneTimeEventRepository.saveAndFlush(oneTimeEvent);
        oneTimeEventSearchRepository.save(oneTimeEvent);

        // Search the oneTimeEvent
        restOneTimeEventMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + oneTimeEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oneTimeEvent.getId().intValue())))
            .andExpect(jsonPath("$.[*].eventName").value(hasItem(DEFAULT_EVENT_NAME)))
            .andExpect(jsonPath("$.[*].eventDesc").value(hasItem(DEFAULT_EVENT_DESC)))
            .andExpect(jsonPath("$.[*].eventLatitude").value(hasItem(DEFAULT_EVENT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].eventLongitude").value(hasItem(DEFAULT_EVENT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].entryFee").value(hasItem(sameNumber(DEFAULT_ENTRY_FEE))))
            .andExpect(jsonPath("$.[*].maxCapacity").value(hasItem(DEFAULT_MAX_CAPACITY)))
            .andExpect(jsonPath("$.[*].eventDate").value(hasItem(DEFAULT_EVENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].imageLinks").value(hasItem(DEFAULT_IMAGE_LINKS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].registrationDeadline").value(hasItem(DEFAULT_REGISTRATION_DEADLINE.toString())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].termsAndConditions").value(hasItem(DEFAULT_TERMS_AND_CONDITIONS)));
    }
}
