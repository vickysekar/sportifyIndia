package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.FacilityStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Facility.
 */
@Entity
@Table(name = "facility")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "facility")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Facility extends AbstractAuditingEntity<Long> implements Serializable {

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
    @Column(name = "contact_num", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String contactNum;

    @NotNull
    @Column(name = "email_id", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String emailId;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "image_links")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String imageLinks;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private FacilityStatusEnum status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @JsonIgnoreProperties(value = { "facility" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "facility")
    @org.springframework.data.annotation.Transient
    private Address address;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "facility", "saleLeads", "tasks" }, allowSetters = true)
    private Set<FacilityEmployee> facilityEmployees = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "facility", "subscriptionPlans" }, allowSetters = true)
    private Set<Course> courses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "facility", "oneTimeEventSubscribers" }, allowSetters = true)
    private Set<OneTimeEvent> oneTimeEvents = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = { "facility", "utilityAvailableDays", "utilityExceptionDays", "utilitySlots", "utilityBookings" },
        allowSetters = true
    )
    private Set<Utility> utilities = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "facility", "facilityEmployee", "user", "tasks", "leadActivities" }, allowSetters = true)
    private Set<SaleLead> saleLeads = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "facility" }, allowSetters = true)
    private Set<TaxMaster> taxMasters = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "facility" }, allowSetters = true)
    private Set<Discount> discounts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Facility id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Facility name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Facility description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactNum() {
        return this.contactNum;
    }

    public Facility contactNum(String contactNum) {
        this.setContactNum(contactNum);
        return this;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public Facility emailId(String emailId) {
        this.setEmailId(emailId);
        return this;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Facility latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Facility longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageLinks() {
        return this.imageLinks;
    }

    public Facility imageLinks(String imageLinks) {
        this.setImageLinks(imageLinks);
        return this;
    }

    public void setImageLinks(String imageLinks) {
        this.imageLinks = imageLinks;
    }

    public FacilityStatusEnum getStatus() {
        return this.status;
    }

    public Facility status(FacilityStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(FacilityStatusEnum status) {
        this.status = status;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Facility user(User user) {
        this.setUser(user);
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        if (this.address != null) {
            this.address.setFacility(null);
        }
        if (address != null) {
            address.setFacility(this);
        }
        this.address = address;
    }

    public Facility address(Address address) {
        this.setAddress(address);
        return this;
    }

    public Set<FacilityEmployee> getFacilityEmployees() {
        return this.facilityEmployees;
    }

    public void setFacilityEmployees(Set<FacilityEmployee> facilityEmployees) {
        if (this.facilityEmployees != null) {
            this.facilityEmployees.forEach(i -> i.setFacility(null));
        }
        if (facilityEmployees != null) {
            facilityEmployees.forEach(i -> i.setFacility(this));
        }
        this.facilityEmployees = facilityEmployees;
    }

    public Facility facilityEmployees(Set<FacilityEmployee> facilityEmployees) {
        this.setFacilityEmployees(facilityEmployees);
        return this;
    }

    public Facility addFacilityEmployee(FacilityEmployee facilityEmployee) {
        this.facilityEmployees.add(facilityEmployee);
        facilityEmployee.setFacility(this);
        return this;
    }

    public Facility removeFacilityEmployee(FacilityEmployee facilityEmployee) {
        this.facilityEmployees.remove(facilityEmployee);
        facilityEmployee.setFacility(null);
        return this;
    }

    public Set<Course> getCourses() {
        return this.courses;
    }

    public void setCourses(Set<Course> courses) {
        if (this.courses != null) {
            this.courses.forEach(i -> i.setFacility(null));
        }
        if (courses != null) {
            courses.forEach(i -> i.setFacility(this));
        }
        this.courses = courses;
    }

    public Facility courses(Set<Course> courses) {
        this.setCourses(courses);
        return this;
    }

    public Facility addCourse(Course course) {
        this.courses.add(course);
        course.setFacility(this);
        return this;
    }

    public Facility removeCourse(Course course) {
        this.courses.remove(course);
        course.setFacility(null);
        return this;
    }

    public Set<OneTimeEvent> getOneTimeEvents() {
        return this.oneTimeEvents;
    }

    public void setOneTimeEvents(Set<OneTimeEvent> oneTimeEvents) {
        if (this.oneTimeEvents != null) {
            this.oneTimeEvents.forEach(i -> i.setFacility(null));
        }
        if (oneTimeEvents != null) {
            oneTimeEvents.forEach(i -> i.setFacility(this));
        }
        this.oneTimeEvents = oneTimeEvents;
    }

    public Facility oneTimeEvents(Set<OneTimeEvent> oneTimeEvents) {
        this.setOneTimeEvents(oneTimeEvents);
        return this;
    }

    public Facility addOneTimeEvent(OneTimeEvent oneTimeEvent) {
        this.oneTimeEvents.add(oneTimeEvent);
        oneTimeEvent.setFacility(this);
        return this;
    }

    public Facility removeOneTimeEvent(OneTimeEvent oneTimeEvent) {
        this.oneTimeEvents.remove(oneTimeEvent);
        oneTimeEvent.setFacility(null);
        return this;
    }

    public Set<Utility> getUtilities() {
        return this.utilities;
    }

    public void setUtilities(Set<Utility> utilities) {
        if (this.utilities != null) {
            this.utilities.forEach(i -> i.setFacility(null));
        }
        if (utilities != null) {
            utilities.forEach(i -> i.setFacility(this));
        }
        this.utilities = utilities;
    }

    public Facility utilities(Set<Utility> utilities) {
        this.setUtilities(utilities);
        return this;
    }

    public Facility addUtility(Utility utility) {
        this.utilities.add(utility);
        utility.setFacility(this);
        return this;
    }

    public Facility removeUtility(Utility utility) {
        this.utilities.remove(utility);
        utility.setFacility(null);
        return this;
    }

    public Set<SaleLead> getSaleLeads() {
        return this.saleLeads;
    }

    public void setSaleLeads(Set<SaleLead> saleLeads) {
        if (this.saleLeads != null) {
            this.saleLeads.forEach(i -> i.setFacility(null));
        }
        if (saleLeads != null) {
            saleLeads.forEach(i -> i.setFacility(this));
        }
        this.saleLeads = saleLeads;
    }

    public Facility saleLeads(Set<SaleLead> saleLeads) {
        this.setSaleLeads(saleLeads);
        return this;
    }

    public Facility addSaleLead(SaleLead saleLead) {
        this.saleLeads.add(saleLead);
        saleLead.setFacility(this);
        return this;
    }

    public Facility removeSaleLead(SaleLead saleLead) {
        this.saleLeads.remove(saleLead);
        saleLead.setFacility(null);
        return this;
    }

    public Set<TaxMaster> getTaxMasters() {
        return this.taxMasters;
    }

    public void setTaxMasters(Set<TaxMaster> taxMasters) {
        if (this.taxMasters != null) {
            this.taxMasters.forEach(i -> i.setFacility(null));
        }
        if (taxMasters != null) {
            taxMasters.forEach(i -> i.setFacility(this));
        }
        this.taxMasters = taxMasters;
    }

    public Facility taxMasters(Set<TaxMaster> taxMasters) {
        this.setTaxMasters(taxMasters);
        return this;
    }

    public Facility addTaxMaster(TaxMaster taxMaster) {
        this.taxMasters.add(taxMaster);
        taxMaster.setFacility(this);
        return this;
    }

    public Facility removeTaxMaster(TaxMaster taxMaster) {
        this.taxMasters.remove(taxMaster);
        taxMaster.setFacility(null);
        return this;
    }

    public Set<Discount> getDiscounts() {
        return this.discounts;
    }

    public void setDiscounts(Set<Discount> discounts) {
        if (this.discounts != null) {
            this.discounts.forEach(i -> i.setFacility(null));
        }
        if (discounts != null) {
            discounts.forEach(i -> i.setFacility(this));
        }
        this.discounts = discounts;
    }

    public Facility discounts(Set<Discount> discounts) {
        this.setDiscounts(discounts);
        return this;
    }

    public Facility addDiscount(Discount discount) {
        this.discounts.add(discount);
        discount.setFacility(this);
        return this;
    }

    public Facility removeDiscount(Discount discount) {
        this.discounts.remove(discount);
        discount.setFacility(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Facility)) {
            return false;
        }
        return getId() != null && getId().equals(((Facility) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Facility{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", contactNum='" + getContactNum() + "'" +
            ", emailId='" + getEmailId() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", imageLinks='" + getImageLinks() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
