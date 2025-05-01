package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.BusinessEntityEnum;
import com.sportifyindia.app.domain.enumeration.DiscountTypeEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.Discount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DiscountDTO implements Serializable {

    private Long id;

    @NotNull
    private String discountCode;

    private String discountText;

    @NotNull
    private DiscountTypeEnum discountType;

    @NotNull
    private BigDecimal discountValue;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    @NotNull
    private BusinessEntityEnum beType;

    private FacilityDTO facility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public String getDiscountText() {
        return discountText;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
    }

    public DiscountTypeEnum getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public BusinessEntityEnum getBeType() {
        return beType;
    }

    public void setBeType(BusinessEntityEnum beType) {
        this.beType = beType;
    }

    public FacilityDTO getFacility() {
        return facility;
    }

    public void setFacility(FacilityDTO facility) {
        this.facility = facility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscountDTO)) {
            return false;
        }

        DiscountDTO discountDTO = (DiscountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, discountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DiscountDTO{" +
            "id=" + getId() +
            ", discountCode='" + getDiscountCode() + "'" +
            ", discountText='" + getDiscountText() + "'" +
            ", discountType='" + getDiscountType() + "'" +
            ", discountValue=" + getDiscountValue() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", beType='" + getBeType() + "'" +
            ", facility=" + getFacility() +
            "}";
    }
}
