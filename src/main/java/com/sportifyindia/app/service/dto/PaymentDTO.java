package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.PaymentStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.Payment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    @NotNull
    private PaymentStatusEnum paymentStatus;

    @NotNull
    private String gatewayStatus;

    private String gatewayCode;

    private String paymentDesc;

    @NotNull
    private String transactionId;

    @NotNull
    private String token;

    @NotNull
    private String paymentGateway;

    private ChargeDTO charge;

    private FacilityDTO facility;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getGatewayStatus() {
        return gatewayStatus;
    }

    public void setGatewayStatus(String gatewayStatus) {
        this.gatewayStatus = gatewayStatus;
    }

    public String getGatewayCode() {
        return gatewayCode;
    }

    public void setGatewayCode(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    public String getPaymentDesc() {
        return paymentDesc;
    }

    public void setPaymentDesc(String paymentDesc) {
        this.paymentDesc = paymentDesc;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public ChargeDTO getCharge() {
        return charge;
    }

    public void setCharge(ChargeDTO charge) {
        this.charge = charge;
    }

    public FacilityDTO getFacility() {
        return facility;
    }

    public void setFacility(FacilityDTO facility) {
        this.facility = facility;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentDTO)) {
            return false;
        }

        PaymentDTO paymentDTO = (PaymentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentDTO{" +
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
            ", charge=" + getCharge() +
            ", facility=" + getFacility() +
            ", user=" + getUser() +
            "}";
    }
}
