package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.DaysOfWeekEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.UtilityAvailableDays} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtilityAvailableDaysDTO implements Serializable {

    private Long id;

    @NotNull
    private DaysOfWeekEnum daysOfWeek;

    @NotNull
    private List<TimeSlotDTO> timeSlots;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DaysOfWeekEnum getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(DaysOfWeekEnum daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<TimeSlotDTO> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlotDTO> timeSlots) {
        this.timeSlots = timeSlots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilityAvailableDaysDTO)) {
            return false;
        }

        UtilityAvailableDaysDTO utilityAvailableDaysDTO = (UtilityAvailableDaysDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, utilityAvailableDaysDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilityAvailableDaysDTO{" +
            "id=" + getId() +
            ", daysOfWeek='" + getDaysOfWeek() + "'" +
            ", timeSlots=" + getTimeSlots() +
            "}";
    }
}
