package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.EventStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.OneTimeEvent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OneTimeEventDTO implements Serializable {

    private Long id;

    @NotNull
    private String eventName;

    private String eventDesc;

    private Double eventLatitude;

    private Double eventLongitude;

    @NotNull
    private BigDecimal entryFee;

    @NotNull
    private Integer maxCapacity;

    @NotNull
    private Instant eventDate;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    private String imageLinks;

    @NotNull
    private EventStatusEnum status;

    @NotNull
    private Instant registrationDeadline;

    @NotNull
    private String category;

    private String tags;

    private String termsAndConditions;

    private FacilityDTO facility;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public Double getEventLatitude() {
        return eventLatitude;
    }

    public void setEventLatitude(Double eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    public Double getEventLongitude() {
        return eventLongitude;
    }

    public void setEventLongitude(Double eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

    public BigDecimal getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(BigDecimal entryFee) {
        this.entryFee = entryFee;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Instant getEventDate() {
        return eventDate;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
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

    public String getImageLinks() {
        return imageLinks;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    public EventStatusEnum getStatus() {
        return status;
    }

    public void setStatus(EventStatusEnum status) {
        this.status = status;
    }

    public Instant getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(Instant registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public FacilityDTO getFacility() {
        return facility;
    }

    public void setFacility(FacilityDTO facility) {
        this.facility = facility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OneTimeEventDTO)) {
            return false;
        }

        OneTimeEventDTO oneTimeEventDTO = (OneTimeEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, oneTimeEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OneTimeEventDTO{" +
            "id=" + getId() +
            ", eventName='" + getEventName() + "'" +
            ", eventDesc='" + getEventDesc() + "'" +
            ", eventLatitude=" + getEventLatitude() +
            ", eventLongitude=" + getEventLongitude() +
            ", entryFee=" + getEntryFee() +
            ", maxCapacity=" + getMaxCapacity() +
            ", eventDate='" + getEventDate() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", imageLinks='" + getImageLinks() + "'" +
            ", status='" + getStatus() + "'" +
            ", registrationDeadline='" + getRegistrationDeadline() + "'" +
            ", category='" + getCategory() + "'" +
            ", tags='" + getTags() + "'" +
            ", termsAndConditions='" + getTermsAndConditions() + "'" +
            ", facility=" + getFacility() +
            "}";
    }
}
