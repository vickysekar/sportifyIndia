package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TimeSlots.
 */
@Entity
@Table(name = "time_slots")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "timeslots")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TimeSlots implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "timeSlots")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "timeSlots", "subscriptionPlans" }, allowSetters = true)
    private Set<SubscriptionAvailableDay> subscriptionAvailableDays = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "timeSlots")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "timeSlots", "utilities" }, allowSetters = true)
    private Set<UtilityAvailableDays> utilityAvailableDays = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "timeSlots")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "utility", "timeSlots", "utilityBookings" }, allowSetters = true)
    private Set<UtilitySlots> utilitySlots = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TimeSlots id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public TimeSlots startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public TimeSlots endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Set<SubscriptionAvailableDay> getSubscriptionAvailableDays() {
        return this.subscriptionAvailableDays;
    }

    public void setSubscriptionAvailableDays(Set<SubscriptionAvailableDay> subscriptionAvailableDays) {
        if (this.subscriptionAvailableDays != null) {
            this.subscriptionAvailableDays.forEach(i -> i.setTimeSlots(null));
        }
        if (subscriptionAvailableDays != null) {
            subscriptionAvailableDays.forEach(i -> i.setTimeSlots(this));
        }
        this.subscriptionAvailableDays = subscriptionAvailableDays;
    }

    public TimeSlots subscriptionAvailableDays(Set<SubscriptionAvailableDay> subscriptionAvailableDays) {
        this.setSubscriptionAvailableDays(subscriptionAvailableDays);
        return this;
    }

    public TimeSlots addSubscriptionAvailableDay(SubscriptionAvailableDay subscriptionAvailableDay) {
        this.subscriptionAvailableDays.add(subscriptionAvailableDay);
        subscriptionAvailableDay.setTimeSlots(this);
        return this;
    }

    public TimeSlots removeSubscriptionAvailableDay(SubscriptionAvailableDay subscriptionAvailableDay) {
        this.subscriptionAvailableDays.remove(subscriptionAvailableDay);
        subscriptionAvailableDay.setTimeSlots(null);
        return this;
    }

    public Set<UtilityAvailableDays> getUtilityAvailableDays() {
        return this.utilityAvailableDays;
    }

    public void setUtilityAvailableDays(Set<UtilityAvailableDays> utilityAvailableDays) {
        if (this.utilityAvailableDays != null) {
            this.utilityAvailableDays.forEach(i -> i.setTimeSlots(null));
        }
        if (utilityAvailableDays != null) {
            utilityAvailableDays.forEach(i -> i.setTimeSlots(this));
        }
        this.utilityAvailableDays = utilityAvailableDays;
    }

    public TimeSlots utilityAvailableDays(Set<UtilityAvailableDays> utilityAvailableDays) {
        this.setUtilityAvailableDays(utilityAvailableDays);
        return this;
    }

    public TimeSlots addUtilityAvailableDays(UtilityAvailableDays utilityAvailableDays) {
        this.utilityAvailableDays.add(utilityAvailableDays);
        utilityAvailableDays.setTimeSlots(this);
        return this;
    }

    public TimeSlots removeUtilityAvailableDays(UtilityAvailableDays utilityAvailableDays) {
        this.utilityAvailableDays.remove(utilityAvailableDays);
        utilityAvailableDays.setTimeSlots(null);
        return this;
    }

    public Set<UtilitySlots> getUtilitySlots() {
        return this.utilitySlots;
    }

    public void setUtilitySlots(Set<UtilitySlots> utilitySlots) {
        if (this.utilitySlots != null) {
            this.utilitySlots.forEach(i -> i.setTimeSlots(null));
        }
        if (utilitySlots != null) {
            utilitySlots.forEach(i -> i.setTimeSlots(this));
        }
        this.utilitySlots = utilitySlots;
    }

    public TimeSlots utilitySlots(Set<UtilitySlots> utilitySlots) {
        this.setUtilitySlots(utilitySlots);
        return this;
    }

    public TimeSlots addUtilitySlots(UtilitySlots utilitySlots) {
        this.utilitySlots.add(utilitySlots);
        utilitySlots.setTimeSlots(this);
        return this;
    }

    public TimeSlots removeUtilitySlots(UtilitySlots utilitySlots) {
        this.utilitySlots.remove(utilitySlots);
        utilitySlots.setTimeSlots(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeSlots)) {
            return false;
        }
        return getId() != null && getId().equals(((TimeSlots) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TimeSlots{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
