package com.sportifyindia.app.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.Course} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CourseDTO implements Serializable {

    private Long id;

    private String name;

    private String sport;

    private String level;

    private String description;

    private Instant startTime;

    private Instant endTime;

    private Integer duration;

    private String imageLinks;

    private String status;

    private String termsAndConditions;

    private Long facilityId;

    private Set<SubscriptionPlanDTO> subscriptionPlans = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Set<SubscriptionPlanDTO> getSubscriptionPlans() {
        return subscriptionPlans;
    }

    public void setSubscriptionPlans(Set<SubscriptionPlanDTO> subscriptionPlans) {
        this.subscriptionPlans = subscriptionPlans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseDTO)) {
            return false;
        }

        CourseDTO courseDTO = (CourseDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, courseDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "CourseDTO{" +
            "id=" +
            getId() +
            ", name='" +
            getName() +
            "'" +
            ", sport='" +
            getSport() +
            "'" +
            ", level='" +
            getLevel() +
            "'" +
            ", description='" +
            getDescription() +
            "'" +
            ", startTime='" +
            getStartTime() +
            "'" +
            ", endTime='" +
            getEndTime() +
            "'" +
            ", duration=" +
            getDuration() +
            ", imageLinks='" +
            getImageLinks() +
            "'" +
            ", status='" +
            getStatus() +
            "'" +
            ", termsAndConditions='" +
            getTermsAndConditions() +
            "'" +
            ", facilityId=" +
            getFacilityId() +
            "}"
        );
    }
}
