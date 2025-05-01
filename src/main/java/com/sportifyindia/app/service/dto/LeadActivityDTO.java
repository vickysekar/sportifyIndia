package com.sportifyindia.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.LeadActivity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LeadActivityDTO implements Serializable {

    private Long id;

    @NotNull
    private String activityType;

    private String description;

    private SaleLeadDTO saleLead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SaleLeadDTO getSaleLead() {
        return saleLead;
    }

    public void setSaleLead(SaleLeadDTO saleLead) {
        this.saleLead = saleLead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LeadActivityDTO)) {
            return false;
        }

        LeadActivityDTO leadActivityDTO = (LeadActivityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, leadActivityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LeadActivityDTO{" +
            "id=" + getId() +
            ", activityType='" + getActivityType() + "'" +
            ", description='" + getDescription() + "'" +
            ", saleLead=" + getSaleLead() +
            "}";
    }
}
