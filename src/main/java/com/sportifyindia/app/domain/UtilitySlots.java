package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.UtilitySlotStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UtilitySlots.
 */
@Entity
@Table(name = "utility_slots")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "utilityslots")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtilitySlots extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @NotNull
    @Column(name = "max_capacity", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer maxCapacity;

    @NotNull
    @Column(name = "current_bookings", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer currentBookings;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private UtilitySlotStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "facility", "utilityAvailableDays", "utilityExceptionDays", "utilitySlots", "utilityBookings" },
        allowSetters = true
    )
    private Utility utility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "subscriptionAvailableDays", "utilityAvailableDays", "utilitySlots" }, allowSetters = true)
    private TimeSlots timeSlots;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "utilitySlots")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "utility", "utilitySlots" }, allowSetters = true)
    private Set<UtilityBookings> utilityBookings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UtilitySlots id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return this.date;
    }

    public UtilitySlots date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public UtilitySlots startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public UtilitySlots endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Integer getMaxCapacity() {
        return this.maxCapacity;
    }

    public UtilitySlots maxCapacity(Integer maxCapacity) {
        this.setMaxCapacity(maxCapacity);
        return this;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getCurrentBookings() {
        return this.currentBookings;
    }

    public UtilitySlots currentBookings(Integer currentBookings) {
        this.setCurrentBookings(currentBookings);
        return this;
    }

    public void setCurrentBookings(Integer currentBookings) {
        this.currentBookings = currentBookings;
    }

    public UtilitySlotStatusEnum getStatus() {
        return this.status;
    }

    public UtilitySlots status(UtilitySlotStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(UtilitySlotStatusEnum status) {
        this.status = status;
    }

    public Utility getUtility() {
        return this.utility;
    }

    public void setUtility(Utility utility) {
        this.utility = utility;
    }

    public UtilitySlots utility(Utility utility) {
        this.setUtility(utility);
        return this;
    }

    public TimeSlots getTimeSlots() {
        return this.timeSlots;
    }

    public void setTimeSlots(TimeSlots timeSlots) {
        this.timeSlots = timeSlots;
    }

    public UtilitySlots timeSlots(TimeSlots timeSlots) {
        this.setTimeSlots(timeSlots);
        return this;
    }

    public Set<UtilityBookings> getUtilityBookings() {
        return this.utilityBookings;
    }

    public void setUtilityBookings(Set<UtilityBookings> utilityBookings) {
        if (this.utilityBookings != null) {
            this.utilityBookings.forEach(i -> i.setUtilitySlots(null));
        }
        if (utilityBookings != null) {
            utilityBookings.forEach(i -> i.setUtilitySlots(this));
        }
        this.utilityBookings = utilityBookings;
    }

    public UtilitySlots utilityBookings(Set<UtilityBookings> utilityBookings) {
        this.setUtilityBookings(utilityBookings);
        return this;
    }

    public UtilitySlots addUtilityBookings(UtilityBookings utilityBookings) {
        this.utilityBookings.add(utilityBookings);
        utilityBookings.setUtilitySlots(this);
        return this;
    }

    public UtilitySlots removeUtilityBookings(UtilityBookings utilityBookings) {
        this.utilityBookings.remove(utilityBookings);
        utilityBookings.setUtilitySlots(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilitySlots)) {
            return false;
        }
        return getId() != null && getId().equals(((UtilitySlots) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilitySlots{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", maxCapacity=" + getMaxCapacity() +
            ", currentBookings=" + getCurrentBookings() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
