package com.sportifyindia.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.FacilityEmployee;
import com.sportifyindia.app.domain.enumeration.EmployeeStatusEnum;
import com.sportifyindia.app.repository.FacilityEmployeeRepository;
import com.sportifyindia.app.repository.search.FacilityEmployeeSearchRepository;
import com.sportifyindia.app.service.dto.FacilityEmployeeDTO;
import com.sportifyindia.app.service.mapper.FacilityEmployeeMapper;
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
 * Integration tests for the {@link FacilityEmployeeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FacilityEmployeeResourceIT {

    private static final String DEFAULT_POSITION = "AAAAAAAAAA";
    private static final String UPDATED_POSITION = "BBBBBBBBBB";

    private static final EmployeeStatusEnum DEFAULT_STATUS = EmployeeStatusEnum.ACTIVE;
    private static final EmployeeStatusEnum UPDATED_STATUS = EmployeeStatusEnum.INACTIVE;

    private static final String ENTITY_API_URL = "/api/facility-employees";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/facility-employees/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FacilityEmployeeRepository facilityEmployeeRepository;

    @Autowired
    private FacilityEmployeeMapper facilityEmployeeMapper;

    @Autowired
    private FacilityEmployeeSearchRepository facilityEmployeeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacilityEmployeeMockMvc;

    private FacilityEmployee facilityEmployee;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FacilityEmployee createEntity(EntityManager em) {
        FacilityEmployee facilityEmployee = new FacilityEmployee().position(DEFAULT_POSITION).status(DEFAULT_STATUS);
        return facilityEmployee;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FacilityEmployee createUpdatedEntity(EntityManager em) {
        FacilityEmployee facilityEmployee = new FacilityEmployee().position(UPDATED_POSITION).status(UPDATED_STATUS);
        return facilityEmployee;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        facilityEmployeeSearchRepository.deleteAll();
        assertThat(facilityEmployeeSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        facilityEmployee = createEntity(em);
    }

    @Test
    @Transactional
    void createFacilityEmployee() throws Exception {
        int databaseSizeBeforeCreate = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        // Create the FacilityEmployee
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);
        restFacilityEmployeeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        FacilityEmployee testFacilityEmployee = facilityEmployeeList.get(facilityEmployeeList.size() - 1);
        assertThat(testFacilityEmployee.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testFacilityEmployee.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createFacilityEmployeeWithExistingId() throws Exception {
        // Create the FacilityEmployee with an existing ID
        facilityEmployee.setId(1L);
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);

        int databaseSizeBeforeCreate = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacilityEmployeeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPositionIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        // set the field null
        facilityEmployee.setPosition(null);

        // Create the FacilityEmployee, which fails.
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);

        restFacilityEmployeeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isBadRequest());

        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        // set the field null
        facilityEmployee.setStatus(null);

        // Create the FacilityEmployee, which fails.
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);

        restFacilityEmployeeMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isBadRequest());

        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllFacilityEmployees() throws Exception {
        // Initialize the database
        facilityEmployeeRepository.saveAndFlush(facilityEmployee);

        // Get all the facilityEmployeeList
        restFacilityEmployeeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facilityEmployee.getId().intValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getFacilityEmployee() throws Exception {
        // Initialize the database
        facilityEmployeeRepository.saveAndFlush(facilityEmployee);

        // Get the facilityEmployee
        restFacilityEmployeeMockMvc
            .perform(get(ENTITY_API_URL_ID, facilityEmployee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(facilityEmployee.getId().intValue()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFacilityEmployee() throws Exception {
        // Get the facilityEmployee
        restFacilityEmployeeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFacilityEmployee() throws Exception {
        // Initialize the database
        facilityEmployeeRepository.saveAndFlush(facilityEmployee);

        int databaseSizeBeforeUpdate = facilityEmployeeRepository.findAll().size();
        facilityEmployeeSearchRepository.save(facilityEmployee);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());

        // Update the facilityEmployee
        FacilityEmployee updatedFacilityEmployee = facilityEmployeeRepository.findById(facilityEmployee.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFacilityEmployee are not directly saved in db
        em.detach(updatedFacilityEmployee);
        updatedFacilityEmployee.position(UPDATED_POSITION).status(UPDATED_STATUS);
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(updatedFacilityEmployee);

        restFacilityEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facilityEmployeeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isOk());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeUpdate);
        FacilityEmployee testFacilityEmployee = facilityEmployeeList.get(facilityEmployeeList.size() - 1);
        assertThat(testFacilityEmployee.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testFacilityEmployee.getStatus()).isEqualTo(UPDATED_STATUS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<FacilityEmployee> facilityEmployeeSearchList = IterableUtils.toList(facilityEmployeeSearchRepository.findAll());
                FacilityEmployee testFacilityEmployeeSearch = facilityEmployeeSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFacilityEmployeeSearch.getPosition()).isEqualTo(UPDATED_POSITION);
                assertThat(testFacilityEmployeeSearch.getStatus()).isEqualTo(UPDATED_STATUS);
            });
    }

    @Test
    @Transactional
    void putNonExistingFacilityEmployee() throws Exception {
        int databaseSizeBeforeUpdate = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        facilityEmployee.setId(longCount.incrementAndGet());

        // Create the FacilityEmployee
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacilityEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, facilityEmployeeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchFacilityEmployee() throws Exception {
        int databaseSizeBeforeUpdate = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        facilityEmployee.setId(longCount.incrementAndGet());

        // Create the FacilityEmployee
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFacilityEmployee() throws Exception {
        int databaseSizeBeforeUpdate = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        facilityEmployee.setId(longCount.incrementAndGet());

        // Create the FacilityEmployee
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityEmployeeMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateFacilityEmployeeWithPatch() throws Exception {
        // Initialize the database
        facilityEmployeeRepository.saveAndFlush(facilityEmployee);

        int databaseSizeBeforeUpdate = facilityEmployeeRepository.findAll().size();

        // Update the facilityEmployee using partial update
        FacilityEmployee partialUpdatedFacilityEmployee = new FacilityEmployee();
        partialUpdatedFacilityEmployee.setId(facilityEmployee.getId());

        partialUpdatedFacilityEmployee.position(UPDATED_POSITION).status(UPDATED_STATUS);

        restFacilityEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFacilityEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFacilityEmployee))
            )
            .andExpect(status().isOk());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeUpdate);
        FacilityEmployee testFacilityEmployee = facilityEmployeeList.get(facilityEmployeeList.size() - 1);
        assertThat(testFacilityEmployee.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testFacilityEmployee.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateFacilityEmployeeWithPatch() throws Exception {
        // Initialize the database
        facilityEmployeeRepository.saveAndFlush(facilityEmployee);

        int databaseSizeBeforeUpdate = facilityEmployeeRepository.findAll().size();

        // Update the facilityEmployee using partial update
        FacilityEmployee partialUpdatedFacilityEmployee = new FacilityEmployee();
        partialUpdatedFacilityEmployee.setId(facilityEmployee.getId());

        partialUpdatedFacilityEmployee.position(UPDATED_POSITION).status(UPDATED_STATUS);

        restFacilityEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFacilityEmployee.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFacilityEmployee))
            )
            .andExpect(status().isOk());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeUpdate);
        FacilityEmployee testFacilityEmployee = facilityEmployeeList.get(facilityEmployeeList.size() - 1);
        assertThat(testFacilityEmployee.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testFacilityEmployee.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingFacilityEmployee() throws Exception {
        int databaseSizeBeforeUpdate = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        facilityEmployee.setId(longCount.incrementAndGet());

        // Create the FacilityEmployee
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacilityEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, facilityEmployeeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFacilityEmployee() throws Exception {
        int databaseSizeBeforeUpdate = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        facilityEmployee.setId(longCount.incrementAndGet());

        // Create the FacilityEmployee
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFacilityEmployee() throws Exception {
        int databaseSizeBeforeUpdate = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        facilityEmployee.setId(longCount.incrementAndGet());

        // Create the FacilityEmployee
        FacilityEmployeeDTO facilityEmployeeDTO = facilityEmployeeMapper.toDto(facilityEmployee);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFacilityEmployeeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(facilityEmployeeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FacilityEmployee in the database
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteFacilityEmployee() throws Exception {
        // Initialize the database
        facilityEmployeeRepository.saveAndFlush(facilityEmployee);
        facilityEmployeeRepository.save(facilityEmployee);
        facilityEmployeeSearchRepository.save(facilityEmployee);

        int databaseSizeBeforeDelete = facilityEmployeeRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the facilityEmployee
        restFacilityEmployeeMockMvc
            .perform(delete(ENTITY_API_URL_ID, facilityEmployee.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FacilityEmployee> facilityEmployeeList = facilityEmployeeRepository.findAll();
        assertThat(facilityEmployeeList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(facilityEmployeeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchFacilityEmployee() throws Exception {
        // Initialize the database
        facilityEmployee = facilityEmployeeRepository.saveAndFlush(facilityEmployee);
        facilityEmployeeSearchRepository.save(facilityEmployee);

        // Search the facilityEmployee
        restFacilityEmployeeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + facilityEmployee.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facilityEmployee.getId().intValue())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
