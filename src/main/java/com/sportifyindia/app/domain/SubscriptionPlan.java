package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.SubscriptionPlanStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SubscriptionPlan.
 */
@Entity
@Table(name = "subscription_plan")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "subscriptionplan")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubscriptionPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Column(name = "description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "validity_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String validityType;

    @NotNull
    @Column(name = "validity_period", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer validityPeriod;

    @NotNull
    @Column(name = "no_of_pause_days", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer noOfPauseDays;

    @NotNull
    @Column(name = "session_limit", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer sessionLimit;

    @NotNull
    @Column(name = "is_unlimited_sessions", nullable = false)
    private Boolean isUnlimitedSessions = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private SubscriptionPlanStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "facility", "subscriptionPlans" }, allowSetters = true)
    private Course course;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_subscription_plan__subscription_available_day",
        joinColumns = @JoinColumn(name = "subscription_plan_id"),
        inverseJoinColumns = @JoinColumn(name = "subscription_available_day_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "timeSlots", "subscriptionPlans" }, allowSetters = true)
    private Set<SubscriptionAvailableDay> subscriptionAvailableDays = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SubscriptionPlan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public SubscriptionPlan name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public SubscriptionPlan description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public SubscriptionPlan amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getValidityType() {
        return this.validityType;
    }

    public SubscriptionPlan validityType(String validityType) {
        this.setValidityType(validityType);
        return this;
    }

    public void setValidityType(String validityType) {
        this.validityType = validityType;
    }

    public Integer getValidityPeriod() {
        return this.validityPeriod;
    }

    public SubscriptionPlan validityPeriod(Integer validityPeriod) {
        this.setValidityPeriod(validityPeriod);
        return this;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public Integer getNoOfPauseDays() {
        return this.noOfPauseDays;
    }

    public SubscriptionPlan noOfPauseDays(Integer noOfPauseDays) {
        this.setNoOfPauseDays(noOfPauseDays);
        return this;
    }

    public void setNoOfPauseDays(Integer noOfPauseDays) {
        this.noOfPauseDays = noOfPauseDays;
    }

    public Integer getSessionLimit() {
        return this.sessionLimit;
    }

    public SubscriptionPlan sessionLimit(Integer sessionLimit) {
        this.setSessionLimit(sessionLimit);
        return this;
    }

    public void setSessionLimit(Integer sessionLimit) {
        this.sessionLimit = sessionLimit;
    }

    public Boolean getIsUnlimitedSessions() {
        return this.isUnlimitedSessions;
    }

    public void setIsUnlimitedSessions(Boolean isUnlimitedSessions) {
        this.isUnlimitedSessions = isUnlimitedSessions;
    }

    public SubscriptionPlan isUnlimitedSessions(Boolean isUnlimitedSessions) {
        this.setIsUnlimitedSessions(isUnlimitedSessions);
        return this;
    }

    public SubscriptionPlanStatusEnum getStatus() {
        return this.status;
    }

    public SubscriptionPlan status(SubscriptionPlanStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SubscriptionPlanStatusEnum status) {
        this.status = status;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public SubscriptionPlan course(Course course) {
        this.setCourse(course);
        return this;
    }

    public Set<SubscriptionAvailableDay> getSubscriptionAvailableDays() {
        return this.subscriptionAvailableDays;
    }

    public void setSubscriptionAvailableDays(Set<SubscriptionAvailableDay> subscriptionAvailableDays) {
        this.subscriptionAvailableDays = subscriptionAvailableDays;
    }

    public SubscriptionPlan subscriptionAvailableDays(Set<SubscriptionAvailableDay> subscriptionAvailableDays) {
        this.setSubscriptionAvailableDays(subscriptionAvailableDays);
        return this;
    }

    public SubscriptionPlan addSubscriptionAvailableDay(SubscriptionAvailableDay subscriptionAvailableDay) {
        this.subscriptionAvailableDays.add(subscriptionAvailableDay);
        return this;
    }

    public SubscriptionPlan removeSubscriptionAvailableDay(SubscriptionAvailableDay subscriptionAvailableDay) {
        this.subscriptionAvailableDays.remove(subscriptionAvailableDay);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionPlan)) {
            return false;
        }
        return getId() != null && getId().equals(((SubscriptionPlan) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubscriptionPlan{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", amount=" + getAmount() +
            ", validityType='" + getValidityType() + "'" +
            ", validityPeriod=" + getValidityPeriod() +
            ", noOfPauseDays=" + getNoOfPauseDays() +
            ", sessionLimit=" + getSessionLimit() +
            ", isUnlimitedSessions=" + getIsUnlimitedSessions() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
