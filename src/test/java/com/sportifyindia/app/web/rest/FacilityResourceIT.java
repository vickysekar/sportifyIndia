package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.enumeration.FacilityStatusEnum;
import com.sportifyindia.app.repository.FacilityRepository;
import com.sportifyindia.app.repository.search.FacilitySearchRepository;
import com.sportifyindia.app.service.dto.FacilityDTO;
import com.sportifyindia.app.service.mapper.FacilityMapper;
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
 * Integration tests for the {@link FacilityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FacilityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CONTACT_NUM = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_NUM = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL_ID = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_ID = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final String DEFAULT_IMAGE_LINKS = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_LINKS = "BBBBBBBBBB";

    private static final FacilityStatusEnum DEFAULT_STATUS = FacilityStatusEnum.OPEN;
    private static final FacilityStatusEnum UPDATED_STATUS = FacilityStatusEnum.CLOSED;

    private static final String ENTITY_API_URL = "/api/facilities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/facilities/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityMapper facilityMapper;

    @Autowired
    private FacilitySearchRepository facilitySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacilityMockMvc;

    private Facility facility;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Facility createEntity(EntityManager em) {
        Facility facility = new Facility()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .contactNum(DEFAULT_CONTACT_NUM)
            .emailId(DEFAULT_EMAIL_ID)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .imageLinks(DEFAULT_IMAGE_LINKS)
            .status(DEFAULT_STATUS);
        return facility;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Facility createUpdatedEntity(EntityManager em) {
        Facility facility = new Facility()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .contactNum(UPDATED_CONTACT_NUM)
            .emailId(UPDATED_EMAIL_ID)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .imageLinks(UPDATED_IMAGE_LINKS)
            .status(UPDATED_STATUS);
        return facility;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        facilitySearchRepository.deleteAll();
        assertThat(facilitySearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        facility = createEntity(em);
    }

    @Test
    @Transactional
    void createFacility() throws Exception {
        int databaseSizeBeforeCreate = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);
        restFacilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityDTO)))
            .andExpect(status().isCreated());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFacility.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFacility.getContactNum()).isEqualTo(DEFAULT_CONTACT_NUM);
        assertThat(testFacility.getEmailId()).isEqualTo(DEFAULT_EMAIL_ID);
        assertThat(testFacility.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testFacility.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testFacility.getImageLinks()).isEqualTo(DEFAULT_IMAGE_LINKS);
        assertThat(testFacility.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createFacilityWithExistingId() throws Exception {
        // Create the Facility with an existing ID
        facility.setId(1L);
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        int databaseSizeBeforeCreate = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        // set the field null
        facility.setName(null);

        // Create the Facility, which fails.
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        restFacilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityDTO)))
            .andExpect(status().isBadRequest());

        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkContactNumIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        // set the field null
        facility.setContactNum(null);

        // Create the Facility, which fails.
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        restFacilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityDTO)))
            .andExpect(status().isBadRequest());

        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        // set the field null
        facility.setEmailId(null);

        // Create the Facility, which fails.
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        restFacilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityDTO)))
            .andExpect(status().isBadRequest());

        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        // set the field null
        facility.setStatus(null);

        // Create the Facility, which fails.
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        restFacilityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityDTO)))
            .andExpect(status().isBadRequest());

        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllFacilities() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        // Get all the facilityList
        restFacilityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facility.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].contactNum").value(hasItem(DEFAULT_CONTACT_NUM)))
            .andExpect(jsonPath("$.[*].emailId").value(hasItem(DEFAULT_EMAIL_ID)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].imageLinks").value(hasItem(DEFAULT_IMAGE_LINKS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getFacility() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        // Get the facility
        restFacilityMockMvc
            .perform(get(ENTITY_API_URL_ID, facility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(facility.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.contactNum").value(DEFAULT_CONTACT_NUM))
            .andExpect(jsonPath("$.emailId").value(DEFAULT_EMAIL_ID))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.imageLinks").value(DEFAULT_IMAGE_LINKS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFacility() throws Exception {
        // Get the facility
        restFacilityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFacility() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        facilitySearchRepository.save(facility);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());

        // Update the facility
        Facility updatedFacility = facilityRepository.findById(facility.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFacility are not directly saved in db
        em.detach(updatedFacility);
        updatedFacility
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .contactNum(UPDATED_CONTACT_NUM)
            .emailId(UPDATED_EMAIL_ID)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .imageLinks(UPDATED_IMAGE_LINKS)
            .status(UPDATED_STATUS);
        FacilityDTO facilityDTO = facilityMapper.toDto(updatedFacility);

        restFacilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facilityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facilityDTO))
            )
            .andExpect(status().isOk());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFacility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFacility.getContactNum()).isEqualTo(UPDATED_CONTACT_NUM);
        assertThat(testFacility.getEmailId()).isEqualTo(UPDATED_EMAIL_ID);
        assertThat(testFacility.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testFacility.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testFacility.getImageLinks()).isEqualTo(UPDATED_IMAGE_LINKS);
        assertThat(testFacility.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Facility> facilitySearchList = IterableUtils.toList(facilitySearchRepository.findAll());
                Facility testFacilitySearch = facilitySearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFacilitySearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testFacilitySearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testFacilitySearch.getContactNum()).isEqualTo(UPDATED_CONTACT_NUM);
                assertThat(testFacilitySearch.getEmailId()).isEqualTo(UPDATED_EMAIL_ID);
                assertThat(testFacilitySearch.getLatitude()).isEqualTo(UPDATED_LATITUDE);
                assertThat(testFacilitySearch.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
                assertThat(testFacilitySearch.getImageLinks()).isEqualTo(UPDATED_IMAGE_LINKS);
                assertThat(testFacilitySearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        facility.setId(longCount.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facilityDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        facility.setId(longCount.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        facility.setId(longCount.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateFacilityWithPatch() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();

        // Update the facility using partial update
        Facility partialUpdatedFacility = new Facility();
        partialUpdatedFacility.setId(facility.getId());

        partialUpdatedFacility
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .emailId(UPDATED_EMAIL_ID)
            .latitude(UPDATED_LATITUDE)
            .imageLinks(UPDATED_IMAGE_LINKS);

        restFacilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFacility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFacility))
            )
            .andExpect(status().isOk());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFacility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFacility.getContactNum()).isEqualTo(DEFAULT_CONTACT_NUM);
        assertThat(testFacility.getEmailId()).isEqualTo(UPDATED_EMAIL_ID);
        assertThat(testFacility.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testFacility.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testFacility.getImageLinks()).isEqualTo(UPDATED_IMAGE_LINKS);
        assertThat(testFacility.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateFacilityWithPatch() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);

        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();

        // Update the facility using partial update
        Facility partialUpdatedFacility = new Facility();
        partialUpdatedFacility.setId(facility.getId());

        partialUpdatedFacility
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .contactNum(UPDATED_CONTACT_NUM)
            .emailId(UPDATED_EMAIL_ID)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .imageLinks(UPDATED_IMAGE_LINKS)
            .status(UPDATED_STATUS);

        restFacilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFacility.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFacility))
            )
            .andExpect(status().isOk());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        Facility testFacility = facilityList.get(facilityList.size() - 1);
        assertThat(testFacility.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFacility.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFacility.getContactNum()).isEqualTo(UPDATED_CONTACT_NUM);
        assertThat(testFacility.getEmailId()).isEqualTo(UPDATED_EMAIL_ID);
        assertThat(testFacility.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testFacility.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testFacility.getImageLinks()).isEqualTo(UPDATED_IMAGE_LINKS);
        assertThat(testFacility.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        facility.setId(longCount.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, facilityDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(facilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        facility.setId(longCount.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(facilityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFacility() throws Exception {
        int databaseSizeBeforeUpdate = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        facility.setId(longCount.incrementAndGet());

        // Create the Facility
        FacilityDTO facilityDTO = facilityMapper.toDto(facility);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(facilityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Facility in the database
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteFacility() throws Exception {
        // Initialize the database
        facilityRepository.saveAndFlush(facility);
        facilityRepository.save(facility);
        facilitySearchRepository.save(facility);

        int databaseSizeBeforeDelete = facilityRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the facility
        restFacilityMockMvc
            .perform(delete(ENTITY_API_URL_ID, facility.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Facility> facilityList = facilityRepository.findAll();
        assertThat(facilityList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilitySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchFacility() throws Exception {
        // Initialize the database
        facility = facilityRepository.saveAndFlush(facility);
        facilitySearchRepository.save(facility);

        // Search the facility
        restFacilityMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + facility.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facility.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].contactNum").value(hasItem(DEFAULT_CONTACT_NUM)))
            .andExpect(jsonPath("$.[*].emailId").value(hasItem(DEFAULT_EMAIL_ID)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].imageLinks").value(hasItem(DEFAULT_IMAGE_LINKS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
