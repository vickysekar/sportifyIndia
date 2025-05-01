package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.EmployeeStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.FacilityEmployee} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FacilityEmployeeDTO implements Serializable {

    private Long id;

    @NotNull
    private String position;

    @NotNull
    private EmployeeStatusEnum status;

    private FacilityDTO facility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public EmployeeStatusEnum getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatusEnum status) {
        this.status = status;
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
        if (!(o instanceof FacilityEmployeeDTO)) {
            return false;
        }

        FacilityEmployeeDTO facilityEmployeeDTO = (FacilityEmployeeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, facilityEmployeeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FacilityEmployeeDTO{" +
            "id=" + getId() +
            ", position='" + getPosition() + "'" +
            ", status='" + getStatus() + "'" +
            ", facility=" + getFacility() +
            "}";
    }
}
