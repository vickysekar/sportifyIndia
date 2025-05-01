package com.sportifyindia.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.SubscriptionAvailableDay} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubscriptionAvailableDayDTO implements Serializable {

    private Long id;

    @NotNull
    private String daysOfWeek;

    private TimeSlotsDTO timeSlots;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public TimeSlotsDTO getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(TimeSlotsDTO timeSlots) {
        this.timeSlots = timeSlots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionAvailableDayDTO)) {
            return false;
        }

        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO = (SubscriptionAvailableDayDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subscriptionAvailableDayDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubscriptionAvailableDayDTO{" +
            "id=" + getId() +
            ", daysOfWeek='" + getDaysOfWeek() + "'" +
            ", timeSlots=" + getTimeSlots() +
            "}";
    }
}
