package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.CourseSubscriptionStatusEnum;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.CourseSubscription} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseSubscriptionDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    @NotNull
    private CourseSubscriptionStatusEnum status;

    private Integer remainingSessions;

    private CourseDTO course;

    private FacilityDTO facility;

    private UserDTO user;

    private SubscriptionPlanDTO subscriptionPlan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public CourseSubscriptionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CourseSubscriptionStatusEnum status) {
        this.status = status;
    }

    public Integer getRemainingSessions() {
        return remainingSessions;
    }

    public void setRemainingSessions(Integer remainingSessions) {
        this.remainingSessions = remainingSessions;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public FacilityDTO getFacility() {
        return facility;
    }

    public void setFacility(FacilityDTO facility) {
        this.facility = facility;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public SubscriptionPlanDTO getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlanDTO subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseSubscriptionDTO)) {
            return false;
        }

        CourseSubscriptionDTO courseSubscriptionDTO = (CourseSubscriptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseSubscriptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseSubscriptionDTO{" +
            "id=" + getId() +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", remainingSessions=" + getRemainingSessions() +
            ", course=" + getCourse() +
            ", facility=" + getFacility() +
            ", user=" + getUser() +
            ", subscriptionPlan=" + getSubscriptionPlan() +
            "}";
    }
}
