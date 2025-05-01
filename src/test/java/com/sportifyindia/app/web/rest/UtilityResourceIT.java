package com.sportifyindia.app.web.rest;

import static com.sportifyindia.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.Utility;
import com.sportifyindia.app.domain.enumeration.UtilityStatusEnum;
import com.sportifyindia.app.repository.UtilityRepository;
import com.sportifyindia.app.repository.search.UtilitySearchRepository;
import com.sportifyindia.app.service.UtilityService;
import com.sportifyindia.app.service.dto.UtilityDTO;
import com.sportifyindia.app.service.mapper.UtilityMapper;
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
 * Integration tests for the {@link UtilityResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class UtilityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_TERMS_AND_CONDITIONS = "AAAAAAAAAA";
    private static final String UPDATED_TERMS_AND_CONDITIONS = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE_PER_SLOT = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE_PER_SLOT = new BigDecimal(2);

    private static final Integer DEFAULT_MAX_CAPACITY = 1;
    private static final Integer UPDATED_MAX_CAPACITY = 2;

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final String DEFAULT_REQUIREMENTS = "AAAAAAAAAA";
    private static final String UPDATED_REQUIREMENTS = "BBBBBBBBBB";

    private static final UtilityStatusEnum DEFAULT_STATUS = UtilityStatusEnum.AVAILABLE;
    private static final UtilityStatusEnum UPDATED_STATUS = UtilityStatusEnum.UNAVAILABLE;

    private static final String ENTITY_API_URL = "/api/utilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/utilities/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UtilityRepository utilityRepository;

    @Mock
    private UtilityRepository utilityRepositoryMock;

    @Autowired
    private UtilityMapper utilityMapper;

    @Mock
    private UtilityService utilityServiceMock;

    @Autowired
    private UtilitySearchRepository utilitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUtilityMockMvc;

    private Utility utility;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Utility createEntity(EntityManager em) {
        Utility utility = new Utility()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .termsAndConditions(DEFAULT_TERMS_AND_CONDITIONS)
            .pricePerSlot(DEFAULT_PRICE_PER_SLOT)
            .maxCapacity(DEFAULT_MAX_CAPACITY)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .requirements(DEFAULT_REQUIREMENTS)
            .status(DEFAULT_STATUS);
        return utility;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Utility createUpdatedEntity(EntityManager em) {
        Utility utility = new Utility()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .termsAndConditions(UPDATED_TERMS_AND_CONDITIONS)
            .pricePerSlot(UPDATED_PRICE_PER_SLOT)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .requirements(UPDATED_REQUIREMENTS)
            .status(UPDATED_STATUS);
        return utility;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        utilitySearchRepository.deleteAll();
        assertThat(utilitySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        utility = createEntity(em);
    }

    @Test
    @Transactional
    void createUtility() throws Exception {
        int databaseSizeBeforeCreate = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        // Create the Utility
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);
        restUtilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityDTO)))
            .andExpect(status().isCreated());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Utility testUtility = utilityList.get(utilityList.size() - 1);
        assertThat(testUtility.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUtility.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testUtility.getTermsAndConditions()).isEqualTo(DEFAULT_TERMS_AND_CONDITIONS);
        assertThat(testUtility.getPricePerSlot()).isEqualByComparingTo(DEFAULT_PRICE_PER_SLOT);
        assertThat(testUtility.getMaxCapacity()).isEqualTo(DEFAULT_MAX_CAPACITY);
        assertThat(testUtility.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testUtility.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testUtility.getRequirements()).isEqualTo(DEFAULT_REQUIREMENTS);
        assertThat(testUtility.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createUtilityWithExistingId() throws Exception {
        // Create the Utility with an existing ID
        utility.setId(1L);
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        int databaseSizeBeforeCreate = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restUtilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        // set the field null
        utility.setName(null);

        // Create the Utility, which fails.
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        restUtilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityDTO)))
            .andExpect(status().isBadRequest());

        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPricePerSlotIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        // set the field null
        utility.setPricePerSlot(null);

        // Create the Utility, which fails.
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        restUtilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityDTO)))
            .andExpect(status().isBadRequest());

        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMaxCapacityIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        // set the field null
        utility.setMaxCapacity(null);

        // Create the Utility, which fails.
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        restUtilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityDTO)))
            .andExpect(status().isBadRequest());

        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        // set the field null
        utility.setStatus(null);

        // Create the Utility, which fails.
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        restUtilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityDTO)))
            .andExpect(status().isBadRequest());

        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllUtilities() throws Exception {
        // Initialize the database
        utilityRepository.saveAndFlush(utility);

        // Get all the utilityList
        restUtilityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utility.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].termsAndConditions").value(hasItem(DEFAULT_TERMS_AND_CONDITIONS)))
            .andExpect(jsonPath("$.[*].pricePerSlot").value(hasItem(sameNumber(DEFAULT_PRICE_PER_SLOT))))
            .andExpect(jsonPath("$.[*].maxCapacity").value(hasItem(DEFAULT_MAX_CAPACITY)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].requirements").value(hasItem(DEFAULT_REQUIREMENTS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUtilitiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(utilityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUtilityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(utilityServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUtilitiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(utilityServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUtilityMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(utilityRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getUtility() throws Exception {
        // Initialize the database
        utilityRepository.saveAndFlush(utility);

        // Get the utility
        restUtilityMockMvc
            .perform(get(ENTITY_API_URL_ID, utility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(utility.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.termsAndConditions").value(DEFAULT_TERMS_AND_CONDITIONS))
            .andExpect(jsonPath("$.pricePerSlot").value(sameNumber(DEFAULT_PRICE_PER_SLOT)))
            .andExpect(jsonPath("$.maxCapacity").value(DEFAULT_MAX_CAPACITY))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.requirements").value(DEFAULT_REQUIREMENTS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUtility() throws Exception {
        // Get the utility
        restUtilityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUtility() throws Exception {
        // Initialize the database
        utilityRepository.saveAndFlush(utility);

        int databaseSizeBeforeUpdate = utilityRepository.findAll().size();
        utilitySearchRepository.save(utility);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());

        // Update the utility
        Utility updatedUtility = utilityRepository.findById(utility.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUtility are not directly saved in db
        em.detach(updatedUtility);
        updatedUtility
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .termsAndConditions(UPDATED_TERMS_AND_CONDITIONS)
            .pricePerSlot(UPDATED_PRICE_PER_SLOT)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .requirements(UPDATED_REQUIREMENTS)
            .status(UPDATED_STATUS);
        UtilityDTO utilityDTO = utilityMapper.toDto(updatedUtility);

        restUtilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityDTO))
            )
            .andExpect(status().isOk());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeUpdate);
        Utility testUtility = utilityList.get(utilityList.size() - 1);
        assertThat(testUtility.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUtility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testUtility.getTermsAndConditions()).isEqualTo(UPDATED_TERMS_AND_CONDITIONS);
        assertThat(testUtility.getPricePerSlot()).isEqualByComparingTo(UPDATED_PRICE_PER_SLOT);
        assertThat(testUtility.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
        assertThat(testUtility.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testUtility.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testUtility.getRequirements()).isEqualTo(UPDATED_REQUIREMENTS);
        assertThat(testUtility.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Utility> utilitySearchList = IterableUtils.toList(utilitySearchRepository.findAll());
                Utility testUtilitySearch = utilitySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testUtilitySearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testUtilitySearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testUtilitySearch.getTermsAndConditions()).isEqualTo(UPDATED_TERMS_AND_CONDITIONS);
                assertThat(testUtilitySearch.getPricePerSlot()).isEqualByComparingTo(UPDATED_PRICE_PER_SLOT);
                assertThat(testUtilitySearch.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
                assertThat(testUtilitySearch.getLatitude()).isEqualTo(UPDATED_LATITUDE);
                assertThat(testUtilitySearch.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
                assertThat(testUtilitySearch.getRequirements()).isEqualTo(UPDATED_REQUIREMENTS);
                assertThat(testUtilitySearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingUtility() throws Exception {
        int databaseSizeBeforeUpdate = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        utility.setId(longCount.incrementAndGet());

        // Create the Utility
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, utilityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchUtility() throws Exception {
        int databaseSizeBeforeUpdate = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        utility.setId(longCount.incrementAndGet());

        // Create the Utility
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(utilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUtility() throws Exception {
        int databaseSizeBeforeUpdate = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        utility.setId(longCount.incrementAndGet());

        // Create the Utility
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(utilityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateUtilityWithPatch() throws Exception {
        // Initialize the database
        utilityRepository.saveAndFlush(utility);

        int databaseSizeBeforeUpdate = utilityRepository.findAll().size();

        // Update the utility using partial update
        Utility partialUpdatedUtility = new Utility();
        partialUpdatedUtility.setId(utility.getId());

        partialUpdatedUtility
            .description(UPDATED_DESCRIPTION)
            .termsAndConditions(UPDATED_TERMS_AND_CONDITIONS)
            .maxCapacity(UPDATED_MAX_CAPACITY);

        restUtilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtility))
            )
            .andExpect(status().isOk());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeUpdate);
        Utility testUtility = utilityList.get(utilityList.size() - 1);
        assertThat(testUtility.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUtility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testUtility.getTermsAndConditions()).isEqualTo(UPDATED_TERMS_AND_CONDITIONS);
        assertThat(testUtility.getPricePerSlot()).isEqualByComparingTo(DEFAULT_PRICE_PER_SLOT);
        assertThat(testUtility.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
        assertThat(testUtility.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testUtility.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testUtility.getRequirements()).isEqualTo(DEFAULT_REQUIREMENTS);
        assertThat(testUtility.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateUtilityWithPatch() throws Exception {
        // Initialize the database
        utilityRepository.saveAndFlush(utility);

        int databaseSizeBeforeUpdate = utilityRepository.findAll().size();

        // Update the utility using partial update
        Utility partialUpdatedUtility = new Utility();
        partialUpdatedUtility.setId(utility.getId());

        partialUpdatedUtility
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .termsAndConditions(UPDATED_TERMS_AND_CONDITIONS)
            .pricePerSlot(UPDATED_PRICE_PER_SLOT)
            .maxCapacity(UPDATED_MAX_CAPACITY)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .requirements(UPDATED_REQUIREMENTS)
            .status(UPDATED_STATUS);

        restUtilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUtility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUtility))
            )
            .andExpect(status().isOk());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeUpdate);
        Utility testUtility = utilityList.get(utilityList.size() - 1);
        assertThat(testUtility.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUtility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testUtility.getTermsAndConditions()).isEqualTo(UPDATED_TERMS_AND_CONDITIONS);
        assertThat(testUtility.getPricePerSlot()).isEqualByComparingTo(UPDATED_PRICE_PER_SLOT);
        assertThat(testUtility.getMaxCapacity()).isEqualTo(UPDATED_MAX_CAPACITY);
        assertThat(testUtility.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testUtility.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testUtility.getRequirements()).isEqualTo(UPDATED_REQUIREMENTS);
        assertThat(testUtility.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingUtility() throws Exception {
        int databaseSizeBeforeUpdate = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        utility.setId(longCount.incrementAndGet());

        // Create the Utility
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, utilityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUtility() throws Exception {
        int databaseSizeBeforeUpdate = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        utility.setId(longCount.incrementAndGet());

        // Create the Utility
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(utilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUtility() throws Exception {
        int databaseSizeBeforeUpdate = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        utility.setId(longCount.incrementAndGet());

        // Create the Utility
        UtilityDTO utilityDTO = utilityMapper.toDto(utility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUtilityMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(utilityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Utility in the database
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteUtility() throws Exception {
        // Initialize the database
        utilityRepository.saveAndFlush(utility);
        utilityRepository.save(utility);
        utilitySearchRepository.save(utility);

        int databaseSizeBeforeDelete = utilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the utility
        restUtilityMockMvc
            .perform(delete(ENTITY_API_URL_ID, utility.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Utility> utilityList = utilityRepository.findAll();
        assertThat(utilityList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(utilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchUtility() throws Exception {
        // Initialize the database
        utility = utilityRepository.saveAndFlush(utility);
        utilitySearchRepository.save(utility);

        // Search the utility
        restUtilityMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + utility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utility.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].termsAndConditions").value(hasItem(DEFAULT_TERMS_AND_CONDITIONS)))
            .andExpect(jsonPath("$.[*].pricePerSlot").value(hasItem(sameNumber(DEFAULT_PRICE_PER_SLOT))))
            .andExpect(jsonPath("$.[*].maxCapacity").value(hasItem(DEFAULT_MAX_CAPACITY)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].requirements").value(hasItem(DEFAULT_REQUIREMENTS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
