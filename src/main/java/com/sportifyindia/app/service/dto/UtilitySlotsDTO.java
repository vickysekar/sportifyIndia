package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.UtilitySlotStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.UtilitySlots} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtilitySlotsDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant date;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    @NotNull
    private Integer maxCapacity;

    @NotNull
    private Integer currentBookings;

    @NotNull
    private UtilitySlotStatusEnum status;

    private UtilityDTO utility;

    private TimeSlotsDTO timeSlots;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getCurrentBookings() {
        return currentBookings;
    }

    public void setCurrentBookings(Integer currentBookings) {
        this.currentBookings = currentBookings;
    }

    public UtilitySlotStatusEnum getStatus() {
        return status;
    }

    public void setStatus(UtilitySlotStatusEnum status) {
        this.status = status;
    }

    public UtilityDTO getUtility() {
        return utility;
    }

    public void setUtility(UtilityDTO utility) {
        this.utility = utility;
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
        if (!(o instanceof UtilitySlotsDTO)) {
            return false;
        }

        UtilitySlotsDTO utilitySlotsDTO = (UtilitySlotsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, utilitySlotsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilitySlotsDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", maxCapacity=" + getMaxCapacity() +
            ", currentBookings=" + getCurrentBookings() +
            ", status='" + getStatus() + "'" +
            ", utility=" + getUtility() +
            ", timeSlots=" + getTimeSlots() +
            "}";
    }
}
