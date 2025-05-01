package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.EventStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A OneTimeEvent.
 */
@Entity
@Table(name = "one_time_event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "onetimeevent")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OneTimeEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "event_name", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String eventName;

    @Column(name = "event_desc")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String eventDesc;

    @Column(name = "event_latitude")
    private Double eventLatitude;

    @Column(name = "event_longitude")
    private Double eventLongitude;

    @NotNull
    @Column(name = "entry_fee", precision = 21, scale = 2, nullable = false)
    private BigDecimal entryFee;

    @NotNull
    @Column(name = "max_capacity", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer maxCapacity;

    @NotNull
    @Column(name = "event_date", nullable = false)
    private Instant eventDate;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "image_links")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String imageLinks;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private EventStatusEnum status;

    @NotNull
    @Column(name = "registration_deadline", nullable = false)
    private Instant registrationDeadline;

    @NotNull
    @Column(name = "category", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String category;

    @Column(name = "tags")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String tags;

    @Column(name = "terms_and_conditions")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String termsAndConditions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "user", "address", "facilityEmployees", "courses", "oneTimeEvents", "utilities", "saleLeads", "taxMasters", "discounts" },
        allowSetters = true
    )
    private Facility facility;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "oneTimeEvent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "oneTimeEvent", "user" }, allowSetters = true)
    private Set<OneTimeEventSubscribers> oneTimeEventSubscribers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OneTimeEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return this.eventName;
    }

    public OneTimeEvent eventName(String eventName) {
        this.setEventName(eventName);
        return this;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDesc() {
        return this.eventDesc;
    }

    public OneTimeEvent eventDesc(String eventDesc) {
        this.setEventDesc(eventDesc);
        return this;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public Double getEventLatitude() {
        return this.eventLatitude;
    }

    public OneTimeEvent eventLatitude(Double eventLatitude) {
        this.setEventLatitude(eventLatitude);
        return this;
    }

    public void setEventLatitude(Double eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    public Double getEventLongitude() {
        return this.eventLongitude;
    }

    public OneTimeEvent eventLongitude(Double eventLongitude) {
        this.setEventLongitude(eventLongitude);
        return this;
    }

    public void setEventLongitude(Double eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

    public BigDecimal getEntryFee() {
        return this.entryFee;
    }

    public OneTimeEvent entryFee(BigDecimal entryFee) {
        this.setEntryFee(entryFee);
        return this;
    }

    public void setEntryFee(BigDecimal entryFee) {
        this.entryFee = entryFee;
    }

    public Integer getMaxCapacity() {
        return this.maxCapacity;
    }

    public OneTimeEvent maxCapacity(Integer maxCapacity) {
        this.setMaxCapacity(maxCapacity);
        return this;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Instant getEventDate() {
        return this.eventDate;
    }

    public OneTimeEvent eventDate(Instant eventDate) {
        this.setEventDate(eventDate);
        return this;
    }

    public void setEventDate(Instant eventDate) {
        this.eventDate = eventDate;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public OneTimeEvent startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public OneTimeEvent endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public String getImageLinks() {
        return this.imageLinks;
    }

    public OneTimeEvent imageLinks(String imageLinks) {
        this.setImageLinks(imageLinks);
        return this;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    public EventStatusEnum getStatus() {
        return this.status;
    }

    public OneTimeEvent status(EventStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EventStatusEnum status) {
        this.status = status;
    }

    public Instant getRegistrationDeadline() {
        return this.registrationDeadline;
    }

    public OneTimeEvent registrationDeadline(Instant registrationDeadline) {
        this.setRegistrationDeadline(registrationDeadline);
        return this;
    }

    public void setRegistrationDeadline(Instant registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public String getCategory() {
        return this.category;
    }

    public OneTimeEvent category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return this.tags;
    }

    public OneTimeEvent tags(String tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTermsAndConditions() {
        return this.termsAndConditions;
    }

    public OneTimeEvent termsAndConditions(String termsAndConditions) {
        this.setTermsAndConditions(termsAndConditions);
        return this;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public OneTimeEvent facility(Facility facility) {
        this.setFacility(facility);
        return this;
    }

    public Set<OneTimeEventSubscribers> getOneTimeEventSubscribers() {
        return this.oneTimeEventSubscribers;
    }

    public void setOneTimeEventSubscribers(Set<OneTimeEventSubscribers> oneTimeEventSubscribers) {
        if (this.oneTimeEventSubscribers != null) {
            this.oneTimeEventSubscribers.forEach(i -> i.setOneTimeEvent(null));
        }
        if (oneTimeEventSubscribers != null) {
            oneTimeEventSubscribers.forEach(i -> i.setOneTimeEvent(this));
        }
        this.oneTimeEventSubscribers = oneTimeEventSubscribers;
    }

    public OneTimeEvent oneTimeEventSubscribers(Set<OneTimeEventSubscribers> oneTimeEventSubscribers) {
        this.setOneTimeEventSubscribers(oneTimeEventSubscribers);
        return this;
    }

    public OneTimeEvent addOneTimeEventSubscribers(OneTimeEventSubscribers oneTimeEventSubscribers) {
        this.oneTimeEventSubscribers.add(oneTimeEventSubscribers);
        oneTimeEventSubscribers.setOneTimeEvent(this);
        return this;
    }

    public OneTimeEvent removeOneTimeEventSubscribers(OneTimeEventSubscribers oneTimeEventSubscribers) {
        this.oneTimeEventSubscribers.remove(oneTimeEventSubscribers);
        oneTimeEventSubscribers.setOneTimeEvent(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OneTimeEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((OneTimeEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OneTimeEvent{" +
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
            "}";
    }
}
