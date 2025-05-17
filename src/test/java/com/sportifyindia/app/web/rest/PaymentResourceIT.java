package com.sportifyindia.app.web.rest;

import static com.sportifyindia.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sportifyindia.app.IntegrationTest;
import com.sportifyindia.app.domain.Payment;
import com.sportifyindia.app.domain.enumeration.PaymentStatusEnum;
import com.sportifyindia.app.repository.PaymentRepository;
import com.sportifyindia.app.repository.search.PaymentSearchRepository;
import com.sportifyindia.app.service.dto.PaymentDTO;
import com.sportifyindia.app.service.mapper.PaymentMapper;
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
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentResourceIT {

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final PaymentStatusEnum DEFAULT_PAYMENT_STATUS = PaymentStatusEnum.INITIATED;
    private static final PaymentStatusEnum UPDATED_PAYMENT_STATUS = PaymentStatusEnum.SUCCESS;

    private static final String DEFAULT_GATEWAY_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_GATEWAY_STATUS = "BBBBBBBBBB";

    private static final String DEFAULT_GATEWAY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_GATEWAY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_GATEWAY = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_GATEWAY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/payments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentSearchRepository paymentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMockMvc;

    private Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment()
            .amount(DEFAULT_AMOUNT)
            .currency(DEFAULT_CURRENCY)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .gatewayStatus(DEFAULT_GATEWAY_STATUS)
            .gatewayCode(DEFAULT_GATEWAY_CODE)
            .paymentDesc(DEFAULT_PAYMENT_DESC)
            .transactionId(DEFAULT_TRANSACTION_ID)
            .token(DEFAULT_TOKEN)
            .paymentGateway(DEFAULT_PAYMENT_GATEWAY);
        return payment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity(EntityManager em) {
        Payment payment = new Payment()
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .gatewayStatus(UPDATED_GATEWAY_STATUS)
            .gatewayCode(UPDATED_GATEWAY_CODE)
            .paymentDesc(UPDATED_PAYMENT_DESC)
            .transactionId(UPDATED_TRANSACTION_ID)
            .token(UPDATED_TOKEN)
            .paymentGateway(UPDATED_PAYMENT_GATEWAY);
        return payment;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        paymentSearchRepository.deleteAll();
        assertThat(paymentSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        payment = createEntity(em);
    }

    @Test
    @Transactional
    void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);
        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isCreated());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testPayment.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testPayment.getGatewayStatus()).isEqualTo(DEFAULT_GATEWAY_STATUS);
        assertThat(testPayment.getGatewayCode()).isEqualTo(DEFAULT_GATEWAY_CODE);
        assertThat(testPayment.getPaymentDesc()).isEqualTo(DEFAULT_PAYMENT_DESC);
        assertThat(testPayment.getTransactionId()).isEqualTo(DEFAULT_TRANSACTION_ID);
        assertThat(testPayment.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testPayment.getPaymentGateway()).isEqualTo(DEFAULT_PAYMENT_GATEWAY);
    }

    @Test
    @Transactional
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        payment.setId(1L);
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        int databaseSizeBeforeCreate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setAmount(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setCurrency(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPaymentStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setPaymentStatus(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkGatewayStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setGatewayStatus(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTransactionIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setTransactionId(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setToken(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPaymentGatewayIsRequired() throws Exception {
        int databaseSizeBeforeTest = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // set the field null
        payment.setPaymentGateway(null);

        // Create the Payment, which fails.
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        restPaymentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isBadRequest());

        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPayments() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].gatewayStatus").value(hasItem(DEFAULT_GATEWAY_STATUS)))
            .andExpect(jsonPath("$.[*].gatewayCode").value(hasItem(DEFAULT_GATEWAY_CODE)))
            .andExpect(jsonPath("$.[*].paymentDesc").value(hasItem(DEFAULT_PAYMENT_DESC)))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].paymentGateway").value(hasItem(DEFAULT_PAYMENT_GATEWAY)));
    }

    @Test
    @Transactional
    void getPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get the payment
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(payment.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.paymentStatus").value(DEFAULT_PAYMENT_STATUS.toString()))
            .andExpect(jsonPath("$.gatewayStatus").value(DEFAULT_GATEWAY_STATUS))
            .andExpect(jsonPath("$.gatewayCode").value(DEFAULT_GATEWAY_CODE))
            .andExpect(jsonPath("$.paymentDesc").value(DEFAULT_PAYMENT_DESC))
            .andExpect(jsonPath("$.transactionId").value(DEFAULT_TRANSACTION_ID))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN))
            .andExpect(jsonPath("$.paymentGateway").value(DEFAULT_PAYMENT_GATEWAY));
    }

    @Test
    @Transactional
    void getNonExistingPayment() throws Exception {
        // Get the payment
        restPaymentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        paymentSearchRepository.save(payment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPayment are not directly saved in db
        em.detach(updatedPayment);
        updatedPayment
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .gatewayStatus(UPDATED_GATEWAY_STATUS)
            .gatewayCode(UPDATED_GATEWAY_CODE)
            .paymentDesc(UPDATED_PAYMENT_DESC)
            .transactionId(UPDATED_TRANSACTION_ID)
            .token(UPDATED_TOKEN)
            .paymentGateway(UPDATED_PAYMENT_GATEWAY);
        PaymentDTO paymentDTO = paymentMapper.toDto(updatedPayment);

        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testPayment.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testPayment.getGatewayStatus()).isEqualTo(UPDATED_GATEWAY_STATUS);
        assertThat(testPayment.getGatewayCode()).isEqualTo(UPDATED_GATEWAY_CODE);
        assertThat(testPayment.getPaymentDesc()).isEqualTo(UPDATED_PAYMENT_DESC);
        assertThat(testPayment.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testPayment.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testPayment.getPaymentGateway()).isEqualTo(UPDATED_PAYMENT_GATEWAY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Payment> paymentSearchList = IterableUtils.toList(paymentSearchRepository.findAll());
                Payment testPaymentSearch = paymentSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPaymentSearch.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
                assertThat(testPaymentSearch.getCurrency()).isEqualTo(UPDATED_CURRENCY);
                assertThat(testPaymentSearch.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
                assertThat(testPaymentSearch.getGatewayStatus()).isEqualTo(UPDATED_GATEWAY_STATUS);
                assertThat(testPaymentSearch.getGatewayCode()).isEqualTo(UPDATED_GATEWAY_CODE);
                assertThat(testPaymentSearch.getPaymentDesc()).isEqualTo(UPDATED_PAYMENT_DESC);
                assertThat(testPaymentSearch.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
                assertThat(testPaymentSearch.getToken()).isEqualTo(UPDATED_TOKEN);
                assertThat(testPaymentSearch.getPaymentGateway()).isEqualTo(UPDATED_PAYMENT_GATEWAY);
            });
    }

    @Test
    @Transactional
    void putNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paymentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment.gatewayStatus(UPDATED_GATEWAY_STATUS).transactionId(UPDATED_TRANSACTION_ID).token(UPDATED_TOKEN);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testPayment.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testPayment.getGatewayStatus()).isEqualTo(UPDATED_GATEWAY_STATUS);
        assertThat(testPayment.getGatewayCode()).isEqualTo(DEFAULT_GATEWAY_CODE);
        assertThat(testPayment.getPaymentDesc()).isEqualTo(DEFAULT_PAYMENT_DESC);
        assertThat(testPayment.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testPayment.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testPayment.getPaymentGateway()).isEqualTo(DEFAULT_PAYMENT_GATEWAY);
    }

    @Test
    @Transactional
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .amount(UPDATED_AMOUNT)
            .currency(UPDATED_CURRENCY)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .gatewayStatus(UPDATED_GATEWAY_STATUS)
            .gatewayCode(UPDATED_GATEWAY_CODE)
            .paymentDesc(UPDATED_PAYMENT_DESC)
            .transactionId(UPDATED_TRANSACTION_ID)
            .token(UPDATED_TOKEN)
            .paymentGateway(UPDATED_PAYMENT_GATEWAY);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testPayment.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testPayment.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testPayment.getGatewayStatus()).isEqualTo(UPDATED_GATEWAY_STATUS);
        assertThat(testPayment.getGatewayCode()).isEqualTo(UPDATED_GATEWAY_CODE);
        assertThat(testPayment.getPaymentDesc()).isEqualTo(UPDATED_PAYMENT_DESC);
        assertThat(testPayment.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testPayment.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testPayment.getPaymentGateway()).isEqualTo(UPDATED_PAYMENT_GATEWAY);
    }

    @Test
    @Transactional
    void patchNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(longCount.incrementAndGet());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);
        paymentRepository.save(payment);
        paymentSearchRepository.save(payment);

        int databaseSizeBeforeDelete = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the payment
        restPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, payment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPayment() throws Exception {
        // Initialize the database
        payment = paymentRepository.saveAndFlush(payment);
        paymentSearchRepository.save(payment);

        // Search the payment
        restPaymentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].gatewayStatus").value(hasItem(DEFAULT_GATEWAY_STATUS)))
            .andExpect(jsonPath("$.[*].gatewayCode").value(hasItem(DEFAULT_GATEWAY_CODE)))
            .andExpect(jsonPath("$.[*].paymentDesc").value(hasItem(DEFAULT_PAYMENT_DESC)))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].paymentGateway").value(hasItem(DEFAULT_PAYMENT_GATEWAY)));
    }
}
