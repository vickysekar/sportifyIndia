package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.UtilityStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Utility.
 */
@Entity
@Table(name = "utility")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "utility")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Utility extends AbstractAuditingEntity<Long> implements Serializable {

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

    @Column(name = "terms_and_conditions")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String termsAndConditions;

    @NotNull
    @Column(name = "price_per_slot", precision = 21, scale = 2, nullable = false)
    private BigDecimal pricePerSlot;

    @NotNull
    @Column(name = "max_capacity", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer maxCapacity;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "requirements")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String requirements;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private UtilityStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "user", "address", "facilityEmployees", "courses", "oneTimeEvents", "utilities", "saleLeads", "taxMasters", "discounts" },
        allowSetters = true
    )
    private Facility facility;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_utility__utility_available_days",
        joinColumns = @JoinColumn(name = "utility_id"),
        inverseJoinColumns = @JoinColumn(name = "utility_available_days_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "timeSlots", "utilities" }, allowSetters = true)
    private Set<UtilityAvailableDays> utilityAvailableDays = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "utility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "utility" }, allowSetters = true)
    private Set<UtilityExceptionDays> utilityExceptionDays = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "utility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "utility", "timeSlots", "utilityBookings" }, allowSetters = true)
    private Set<UtilitySlots> utilitySlots = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "utility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "utility", "utilitySlots" }, allowSetters = true)
    private Set<UtilityBookings> utilityBookings = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Utility id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Utility name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Utility description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsAndConditions() {
        return this.termsAndConditions;
    }

    public Utility termsAndConditions(String termsAndConditions) {
        this.setTermsAndConditions(termsAndConditions);
        return this;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public BigDecimal getPricePerSlot() {
        return this.pricePerSlot;
    }

    public Utility pricePerSlot(BigDecimal pricePerSlot) {
        this.setPricePerSlot(pricePerSlot);
        return this;
    }

    public void setPricePerSlot(BigDecimal pricePerSlot) {
        this.pricePerSlot = pricePerSlot;
    }

    public Integer getMaxCapacity() {
        return this.maxCapacity;
    }

    public Utility maxCapacity(Integer maxCapacity) {
        this.setMaxCapacity(maxCapacity);
        return this;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Utility latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Utility longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getRequirements() {
        return this.requirements;
    }

    public Utility requirements(String requirements) {
        this.setRequirements(requirements);
        return this;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public UtilityStatusEnum getStatus() {
        return this.status;
    }

    public Utility status(UtilityStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(UtilityStatusEnum status) {
        this.status = status;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Utility facility(Facility facility) {
        this.setFacility(facility);
        return this;
    }

    public Set<UtilityAvailableDays> getUtilityAvailableDays() {
        return this.utilityAvailableDays;
    }

    public void setUtilityAvailableDays(Set<UtilityAvailableDays> utilityAvailableDays) {
        this.utilityAvailableDays = utilityAvailableDays;
    }

    public Utility utilityAvailableDays(Set<UtilityAvailableDays> utilityAvailableDays) {
        this.setUtilityAvailableDays(utilityAvailableDays);
        return this;
    }

    public Utility addUtilityAvailableDays(UtilityAvailableDays utilityAvailableDays) {
        this.utilityAvailableDays.add(utilityAvailableDays);
        return this;
    }

    public Utility removeUtilityAvailableDays(UtilityAvailableDays utilityAvailableDays) {
        this.utilityAvailableDays.remove(utilityAvailableDays);
        return this;
    }

    public Set<UtilityExceptionDays> getUtilityExceptionDays() {
        return this.utilityExceptionDays;
    }

    public void setUtilityExceptionDays(Set<UtilityExceptionDays> utilityExceptionDays) {
        if (this.utilityExceptionDays != null) {
            this.utilityExceptionDays.forEach(i -> i.setUtility(null));
        }
        if (utilityExceptionDays != null) {
            utilityExceptionDays.forEach(i -> i.setUtility(this));
        }
        this.utilityExceptionDays = utilityExceptionDays;
    }

    public Utility utilityExceptionDays(Set<UtilityExceptionDays> utilityExceptionDays) {
        this.setUtilityExceptionDays(utilityExceptionDays);
        return this;
    }

    public Utility addUtilityExceptionDays(UtilityExceptionDays utilityExceptionDays) {
        this.utilityExceptionDays.add(utilityExceptionDays);
        utilityExceptionDays.setUtility(this);
        return this;
    }

    public Utility removeUtilityExceptionDays(UtilityExceptionDays utilityExceptionDays) {
        this.utilityExceptionDays.remove(utilityExceptionDays);
        utilityExceptionDays.setUtility(null);
        return this;
    }

    public Set<UtilitySlots> getUtilitySlots() {
        return this.utilitySlots;
    }

    public void setUtilitySlots(Set<UtilitySlots> utilitySlots) {
        if (this.utilitySlots != null) {
            this.utilitySlots.forEach(i -> i.setUtility(null));
        }
        if (utilitySlots != null) {
            utilitySlots.forEach(i -> i.setUtility(this));
        }
        this.utilitySlots = utilitySlots;
    }

    public Utility utilitySlots(Set<UtilitySlots> utilitySlots) {
        this.setUtilitySlots(utilitySlots);
        return this;
    }

    public Utility addUtilitySlots(UtilitySlots utilitySlots) {
        this.utilitySlots.add(utilitySlots);
        utilitySlots.setUtility(this);
        return this;
    }

    public Utility removeUtilitySlots(UtilitySlots utilitySlots) {
        this.utilitySlots.remove(utilitySlots);
        utilitySlots.setUtility(null);
        return this;
    }

    public Set<UtilityBookings> getUtilityBookings() {
        return this.utilityBookings;
    }

    public void setUtilityBookings(Set<UtilityBookings> utilityBookings) {
        if (this.utilityBookings != null) {
            this.utilityBookings.forEach(i -> i.setUtility(null));
        }
        if (utilityBookings != null) {
            utilityBookings.forEach(i -> i.setUtility(this));
        }
        this.utilityBookings = utilityBookings;
    }

    public Utility utilityBookings(Set<UtilityBookings> utilityBookings) {
        this.setUtilityBookings(utilityBookings);
        return this;
    }

    public Utility addUtilityBookings(UtilityBookings utilityBookings) {
        this.utilityBookings.add(utilityBookings);
        utilityBookings.setUtility(this);
        return this;
    }

    public Utility removeUtilityBookings(UtilityBookings utilityBookings) {
        this.utilityBookings.remove(utilityBookings);
        utilityBookings.setUtility(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Utility)) {
            return false;
        }
        return getId() != null && getId().equals(((Utility) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Utility{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", termsAndConditions='" + getTermsAndConditions() + "'" +
            ", pricePerSlot=" + getPricePerSlot() +
            ", maxCapacity=" + getMaxCapacity() +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", requirements='" + getRequirements() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
