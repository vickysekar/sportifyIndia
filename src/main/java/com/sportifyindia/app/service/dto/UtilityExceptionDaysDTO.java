package com.sportifyindia.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.UtilityExceptionDays} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtilityExceptionDaysDTO implements Serializable {

    private Long id;

    @NotNull
    private String reason;

    @NotNull
    private Instant date;

    private String nestedTimeslots;

    private UtilityDTO utility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getNestedTimeslots() {
        return nestedTimeslots;
    }

    public void setNestedTimeslots(String nestedTimeslots) {
        this.nestedTimeslots = nestedTimeslots;
    }

    public UtilityDTO getUtility() {
        return utility;
    }

    public void setUtility(UtilityDTO utility) {
        this.utility = utility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilityExceptionDaysDTO)) {
            return false;
        }

        UtilityExceptionDaysDTO utilityExceptionDaysDTO = (UtilityExceptionDaysDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, utilityExceptionDaysDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilityExceptionDaysDTO{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", date='" + getDate() + "'" +
            ", nestedTimeslots='" + getNestedTimeslots() + "'" +
            ", utility=" + getUtility() +
            "}";
    }
}
