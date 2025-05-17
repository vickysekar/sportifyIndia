package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.PaymentStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "payment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private PaymentStatusEnum paymentStatus;

    @NotNull
    @Column(name = "gateway_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String gatewayStatus;

    @Column(name = "gateway_code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String gatewayCode;

    @Column(name = "payment_desc")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String paymentDesc;

    @NotNull
    @Column(name = "transaction_id", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String transactionId;

    @NotNull
    @Column(name = "token", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String token;

    @NotNull
    @Column(name = "payment_gateway", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String paymentGateway;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "payments" }, allowSetters = true)
    private Charge charge;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Payment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Payment currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return this.paymentStatus;
    }

    public Payment paymentStatus(PaymentStatusEnum paymentStatus) {
        this.setPaymentStatus(paymentStatus);
        return this;
    }

    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getGatewayStatus() {
        return this.gatewayStatus;
    }

    public Payment gatewayStatus(String gatewayStatus) {
        this.setGatewayStatus(gatewayStatus);
        return this;
    }

    public void setGatewayStatus(String gatewayStatus) {
        this.gatewayStatus = gatewayStatus;
    }

    public String getGatewayCode() {
        return this.gatewayCode;
    }

    public Payment gatewayCode(String gatewayCode) {
        this.setGatewayCode(gatewayCode);
        return this;
    }

    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    public String getPaymentDesc() {
        return this.paymentDesc;
    }

    public Payment paymentDesc(String paymentDesc) {
        this.setPaymentDesc(paymentDesc);
        return this;
    }

    public void setPaymentDesc(String paymentDesc) {
        this.paymentDesc = paymentDesc;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public Payment transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getToken() {
        return this.token;
    }

    public Payment token(String token) {
        this.setToken(token);
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPaymentGateway() {
        return this.paymentGateway;
    }

    public Payment paymentGateway(String paymentGateway) {
        this.setPaymentGateway(paymentGateway);
        return this;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public Charge getCharge() {
        return this.charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    public Payment charge(Charge charge) {
        this.setCharge(charge);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Payment user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", currency='" + getCurrency() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", gatewayStatus='" + getGatewayStatus() + "'" +
            ", gatewayCode='" + getGatewayCode() + "'" +
            ", paymentDesc='" + getPaymentDesc() + "'" +
            ", transactionId='" + getTransactionId() + "'" +
            ", token='" + getToken() + "'" +
            ", paymentGateway='" + getPaymentGateway() + "'" +
            "}";
    }
}
