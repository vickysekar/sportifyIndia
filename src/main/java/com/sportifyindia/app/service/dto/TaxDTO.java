package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.TaxSourceEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.Tax} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaxDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal netAmount;

    @NotNull
    private String computedSlab;

    @NotNull
    private BigDecimal computedAmount;

    @NotNull
    private String taxType;

    @NotNull
    private TaxSourceEnum taxSource;

    private ChargeDTO charge;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getComputedSlab() {
        return computedSlab;
    }

    public void setComputedSlab(String computedSlab) {
        this.computedSlab = computedSlab;
    }

    public BigDecimal getComputedAmount() {
        return computedAmount;
    }

    public void setComputedAmount(BigDecimal computedAmount) {
        this.computedAmount = computedAmount;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public TaxSourceEnum getTaxSource() {
        return taxSource;
    }

    public void setTaxSource(TaxSourceEnum taxSource) {
        this.taxSource = taxSource;
    }

    public ChargeDTO getCharge() {
        return charge;
    }

    public void setCharge(ChargeDTO charge) {
        this.charge = charge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxDTO)) {
            return false;
        }

        TaxDTO taxDTO = (TaxDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taxDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaxDTO{" +
            "id=" + getId() +
            ", netAmount=" + getNetAmount() +
            ", computedSlab='" + getComputedSlab() + "'" +
            ", computedAmount=" + getComputedAmount() +
            ", taxType='" + getTaxType() + "'" +
            ", taxSource='" + getTaxSource() + "'" +
            ", charge=" + getCharge() +
            "}";
    }
}
