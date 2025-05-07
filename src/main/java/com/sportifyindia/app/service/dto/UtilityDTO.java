package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.UtilityStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.Utility} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtilityDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    private String termsAndConditions;

    @NotNull
    private BigDecimal pricePerSlot;

    @NotNull
    private Integer maxCapacity;

    private Double latitude;

    private Double longitude;

    private String requirements;

    @NotNull
    private UtilityStatusEnum status;

    private Long facilityId;

    private Set<UtilityAvailableDaysDTO> utilityAvailableDays = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public BigDecimal getPricePerSlot() {
        return pricePerSlot;
    }

    public void setPricePerSlot(BigDecimal pricePerSlot) {
        this.pricePerSlot = pricePerSlot;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public UtilityStatusEnum getStatus() {
        return status;
    }

    public void setStatus(UtilityStatusEnum status) {
        this.status = status;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Set<UtilityAvailableDaysDTO> getUtilityAvailableDays() {
        return utilityAvailableDays;
    }

    public void setUtilityAvailableDays(Set<UtilityAvailableDaysDTO> utilityAvailableDays) {
        this.utilityAvailableDays = utilityAvailableDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilityDTO)) {
            return false;
        }

        UtilityDTO utilityDTO = (UtilityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, utilityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilityDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", termsAndConditions='" + getTermsAndConditions() + "'" +
            ", pricePerSlot=" + getPricePerSlot() +
            ", maxCapacity=" + getMaxCapacity() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", requirements='" + getRequirements() + "'" +
            ", status='" + getStatus() + "'" +
            ", facilityId=" + getFacilityId() +
            ", utilityAvailableDays=" + getUtilityAvailableDays() +
            "}";
    }
}
