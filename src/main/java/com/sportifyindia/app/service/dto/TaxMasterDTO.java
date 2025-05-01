package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.TaxSourceEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.TaxMaster} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaxMasterDTO implements Serializable {

    private Long id;

    @NotNull
    private String taxSlab;

    @NotNull
    private String taxName;

    @NotNull
    private Boolean isActive;

    @NotNull
    private String taxType;

    @NotNull
    private TaxSourceEnum taxSource;

    private FacilityDTO facility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaxSlab() {
        return taxSlab;
    }

    public void setTaxSlab(String taxSlab) {
        this.taxSlab = taxSlab;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        if (!(o instanceof TaxMasterDTO)) {
            return false;
        }

        TaxMasterDTO taxMasterDTO = (TaxMasterDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taxMasterDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaxMasterDTO{" +
            "id=" + getId() +
            ", taxSlab='" + getTaxSlab() + "'" +
            ", taxName='" + getTaxName() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", taxType='" + getTaxType() + "'" +
            ", taxSource='" + getTaxSource() + "'" +
            ", facility=" + getFacility() +
            "}";
    }
}
