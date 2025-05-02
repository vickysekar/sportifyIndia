package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.SubscriptionPlanStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.SubscriptionPlan} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubscriptionPlanDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String validityType;

    @NotNull
    private Integer validityPeriod;

    @NotNull
    private Integer noOfPauseDays;

    @NotNull
    private Integer sessionLimit;

    @NotNull
    private Boolean isUnlimitedSessions;

    @NotNull
    private SubscriptionPlanStatusEnum status;

    private CourseDTO course;

    private Set<SubscriptionAvailableDayDTO> subscriptionAvailableDays = new HashSet<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getValidityType() {
        return validityType;
    }

    public void setValidityType(String validityType) {
        this.validityType = validityType;
    }

    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public Integer getNoOfPauseDays() {
        return noOfPauseDays;
    }

    public void setNoOfPauseDays(Integer noOfPauseDays) {
        this.noOfPauseDays = noOfPauseDays;
    }

    public Integer getSessionLimit() {
        return sessionLimit;
    }

    public void setSessionLimit(Integer sessionLimit) {
        this.sessionLimit = sessionLimit;
    }

    public Boolean getIsUnlimitedSessions() {
        return isUnlimitedSessions;
    }

    public void setIsUnlimitedSessions(Boolean isUnlimitedSessions) {
        this.isUnlimitedSessions = isUnlimitedSessions;
    }

    public SubscriptionPlanStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SubscriptionPlanStatusEnum status) {
        this.status = status;
    }

    public CourseDTO getCourse() {
        return course;
    }

    public void setCourse(CourseDTO course) {
        this.course = course;
    }

    public Set<SubscriptionAvailableDayDTO> getSubscriptionAvailableDays() {
        return subscriptionAvailableDays;
    }

    public void setSubscriptionAvailableDays(Set<SubscriptionAvailableDayDTO> subscriptionAvailableDays) {
        this.subscriptionAvailableDays = subscriptionAvailableDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionPlanDTO)) {
            return false;
        }

        SubscriptionPlanDTO subscriptionPlanDTO = (SubscriptionPlanDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subscriptionPlanDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubscriptionPlanDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", amount=" + getAmount() +
            ", validityType='" + getValidityType() + "'" +
            ", validityPeriod=" + getValidityPeriod() +
            ", noOfPauseDays=" + getNoOfPauseDays() +
            ", sessionLimit=" + getSessionLimit() +
            ", isUnlimitedSessions='" + getIsUnlimitedSessions() + "'" +
            ", status='" + getStatus() + "'" +
            ", course=" + getCourse() +
            ", subscriptionAvailableDays=" + getSubscriptionAvailableDays() +
            "}";
    }
}
