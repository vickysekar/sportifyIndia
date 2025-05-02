package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.DaysOfWeekEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SubscriptionAvailableDay.
 */
@Entity
@Table(name = "subscription_available_day")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "subscriptionavailableday")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubscriptionAvailableDay extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "days_of_week", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    @Enumerated(EnumType.STRING)
    private DaysOfWeekEnum daysOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "subscriptionAvailableDays", "utilityAvailableDays", "utilitySlots" }, allowSetters = true)
    private TimeSlots timeSlots;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "subscriptionAvailableDays")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "course", "subscriptionAvailableDays" }, allowSetters = true)
    private Set<SubscriptionPlan> subscriptionPlans = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SubscriptionAvailableDay id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DaysOfWeekEnum getDaysOfWeek() {
        return this.daysOfWeek;
    }

    public SubscriptionAvailableDay daysOfWeek(DaysOfWeekEnum daysOfWeek) {
        this.setDaysOfWeek(daysOfWeek);
        return this;
    }

    public void setDaysOfWeek(DaysOfWeekEnum daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public TimeSlots getTimeSlots() {
        return this.timeSlots;
    }

    public void setTimeSlots(TimeSlots timeSlots) {
        this.timeSlots = timeSlots;
    }

    public SubscriptionAvailableDay timeSlots(TimeSlots timeSlots) {
        this.setTimeSlots(timeSlots);
        return this;
    }

    public Set<SubscriptionPlan> getSubscriptionPlans() {
        return this.subscriptionPlans;
    }

    public void setSubscriptionPlans(Set<SubscriptionPlan> subscriptionPlans) {
        if (this.subscriptionPlans != null) {
            this.subscriptionPlans.forEach(i -> i.removeSubscriptionAvailableDay(this));
        }
        if (subscriptionPlans != null) {
            subscriptionPlans.forEach(i -> i.addSubscriptionAvailableDay(this));
        }
        this.subscriptionPlans = subscriptionPlans;
    }

    public SubscriptionAvailableDay subscriptionPlans(Set<SubscriptionPlan> subscriptionPlans) {
        this.setSubscriptionPlans(subscriptionPlans);
        return this;
    }

    public SubscriptionAvailableDay addSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlans.add(subscriptionPlan);
        subscriptionPlan.getSubscriptionAvailableDays().add(this);
        return this;
    }

    public SubscriptionAvailableDay removeSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlans.remove(subscriptionPlan);
        subscriptionPlan.getSubscriptionAvailableDays().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionAvailableDay)) {
            return false;
        }
        return getId() != null && getId().equals(((SubscriptionAvailableDay) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubscriptionAvailableDay{" +
            "id=" + getId() +
            ", daysOfWeek='" + getDaysOfWeek() + "'" +
            "}";
    }
}
