package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.CourseSubscriptionStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CourseSubscription.
 */
@Entity
@Table(name = "course_subscription")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "coursesubscription")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseSubscription implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourseSubscriptionStatusEnum status;

    @Column(name = "remaining_sessions")
    private Integer remainingSessions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "facility", "subscriptionPlans" }, allowSetters = true)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "authorities", "facility", "facilityEmployees" }, allowSetters = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "course", "subscriptionAvailableDays" }, allowSetters = true)
    private SubscriptionPlan subscriptionPlan;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CourseSubscription id(Long id) {
        this.setId(id);
        return this;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public CourseSubscription startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public CourseSubscription endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public CourseSubscriptionStatusEnum getStatus() {
        return this.status;
    }

    public void setStatus(CourseSubscriptionStatusEnum status) {
        this.status = status;
    }

    public CourseSubscription status(CourseSubscriptionStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public Integer getRemainingSessions() {
        return this.remainingSessions;
    }

    public void setRemainingSessions(Integer remainingSessions) {
        this.remainingSessions = remainingSessions;
    }

    public CourseSubscription remainingSessions(Integer remainingSessions) {
        this.setRemainingSessions(remainingSessions);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseSubscription course(Course course) {
        this.setCourse(course);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CourseSubscription user(User user) {
        this.setUser(user);
        return this;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return this.subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public CourseSubscription subscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.setSubscriptionPlan(subscriptionPlan);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseSubscription)) {
            return false;
        }
        return getId() != null && getId().equals(((CourseSubscription) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseSubscription{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", remainingSessions=" + getRemainingSessions() +
            "}";
    }
}
