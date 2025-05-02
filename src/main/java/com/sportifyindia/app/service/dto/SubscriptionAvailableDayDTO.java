package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.DaysOfWeekEnum;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.SubscriptionAvailableDay} entity.
 */
public class SubscriptionAvailableDayDTO implements Serializable {

    private Long id;
    private DaysOfWeekEnum daysOfWeek;
    private Instant startTime;
    private Instant endTime;

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

    @Override
    public String toString() {
        return (
            "SubscriptionAvailableDayDTO{" +
            "id=" +
            getId() +
            ", daysOfWeek='" +
            getDaysOfWeek() +
            "'" +
            ", startTime='" +
            getStartTime() +
            "'" +
            ", endTime='" +
            getEndTime() +
            "'" +
            "}"
        );
    }
}
