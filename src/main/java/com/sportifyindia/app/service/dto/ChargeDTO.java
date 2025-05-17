package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.BusinessEntityEnum;
import com.sportifyindia.app.domain.enumeration.ChargeSource;
import com.sportifyindia.app.domain.enumeration.OrderStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.Charge} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ChargeDTO implements Serializable {

    private Long id;

    @NotNull
    private BusinessEntityEnum beType;

    @NotNull
    private Long beId;

    @NotNull
    private ChargeSource chargeSource;

    @NotNull
    private OrderStatus orderStatus;

    @NotNull
    private BigDecimal computedCharge;

    private BigDecimal computedDiscount;

    @NotNull
    private BigDecimal total;

    @NotNull
    private String currency;

    private String discReason;

    private BigDecimal exchangeRate;

    @NotNull
    private BigDecimal finalCharge;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessEntityEnum getBeType() {
        return beType;
    }

    public void setBeType(BusinessEntityEnum beType) {
        this.beType = beType;
    }

    public Long getBeId() {
        return beId;
    }

    public void setBeId(Long beId) {
        this.beId = beId;
    }

    public ChargeSource getSource() {
        return chargeSource;
    }

    public void setSource(ChargeSource chargeSource) {
        this.chargeSource = chargeSource;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getComputedCharge() {
        return computedCharge;
    }

    public void setComputedCharge(BigDecimal computedCharge) {
        this.computedCharge = computedCharge;
    }

    public BigDecimal getComputedDiscount() {
        return computedDiscount;
    }

    public void setComputedDiscount(BigDecimal computedDiscount) {
        this.computedDiscount = computedDiscount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDiscReason() {
        return discReason;
    }

    public void setDiscReason(String discReason) {
        this.discReason = discReason;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getFinalCharge() {
        return finalCharge;
    }

    public void setFinalCharge(BigDecimal finalCharge) {
        this.finalCharge = finalCharge;
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
        if (!(o instanceof ChargeDTO)) {
            return false;
        }

        ChargeDTO chargeDTO = (ChargeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, chargeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChargeDTO{" +
            "id=" + getId() +
            ", beType='" + getBeType() + "'" +
            ", beId=" + getBeId() +
            ", chargeSource='" + getSource() + "'" +
            ", orderStatus='" + getOrderStatus() + "'" +
            ", computedCharge=" + getComputedCharge() +
            ", computedDiscount=" + getComputedDiscount() +
            ", total=" + getTotal() +
            ", currency='" + getCurrency() + "'" +
            ", discReason='" + getDiscReason() + "'" +
            ", exchangeRate=" + getExchangeRate() +
            ", finalCharge=" + getFinalCharge() +
            ", user=" + getUser() +
            "}";
    }
}
