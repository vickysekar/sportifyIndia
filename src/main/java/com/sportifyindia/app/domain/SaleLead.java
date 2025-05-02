package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.CustomerTypeEnum;
import com.sportifyindia.app.domain.enumeration.LeadStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SaleLead.
 */
@Entity
@Table(name = "sale_lead")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "salelead")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleLead extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "full_name", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String fullName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastName;

    @NotNull
    @Column(name = "email", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phoneNumber;

    @Column(name = "title")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private CustomerTypeEnum customerType;

    @Column(name = "lead_source")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String leadSource;

    @Column(name = "description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "lead_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private LeadStatusEnum leadStatus;

    @NotNull
    @Column(name = "deal_expiry_date", nullable = false)
    private Instant dealExpiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "user", "address", "facilityEmployees", "courses", "oneTimeEvents", "utilities", "saleLeads", "taxMasters", "discounts" },
        allowSetters = true
    )
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "facility", "saleLeads", "tasks" }, allowSetters = true)
    private FacilityEmployee facilityEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "saleLead")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "saleLead", "facilityEmployee", "notes" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "saleLead")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "saleLead" }, allowSetters = true)
    private Set<LeadActivity> leadActivities = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SaleLead id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public SaleLead fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public SaleLead lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public SaleLead email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public SaleLead phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTitle() {
        return this.title;
    }

    public SaleLead title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CustomerTypeEnum getCustomerType() {
        return this.customerType;
    }

    public SaleLead customerType(CustomerTypeEnum customerType) {
        this.setCustomerType(customerType);
        return this;
    }

    public void setCustomerType(CustomerTypeEnum customerType) {
        this.customerType = customerType;
    }

    public String getLeadSource() {
        return this.leadSource;
    }

    public SaleLead leadSource(String leadSource) {
        this.setLeadSource(leadSource);
        return this;
    }

    public void setLeadSource(String leadSource) {
        this.leadSource = leadSource;
    }

    public String getDescription() {
        return this.description;
    }

    public SaleLead description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LeadStatusEnum getLeadStatus() {
        return this.leadStatus;
    }

    public SaleLead leadStatus(LeadStatusEnum leadStatus) {
        this.setLeadStatus(leadStatus);
        return this;
    }

    public void setLeadStatus(LeadStatusEnum leadStatus) {
        this.leadStatus = leadStatus;
    }

    public Instant getDealExpiryDate() {
        return this.dealExpiryDate;
    }

    public SaleLead dealExpiryDate(Instant dealExpiryDate) {
        this.setDealExpiryDate(dealExpiryDate);
        return this;
    }

    public void setDealExpiryDate(Instant dealExpiryDate) {
        this.dealExpiryDate = dealExpiryDate;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public SaleLead facility(Facility facility) {
        this.setFacility(facility);
        return this;
    }

    public FacilityEmployee getFacilityEmployee() {
        return this.facilityEmployee;
    }

    public void setFacilityEmployee(FacilityEmployee facilityEmployee) {
        this.facilityEmployee = facilityEmployee;
    }

    public SaleLead facilityEmployee(FacilityEmployee facilityEmployee) {
        this.setFacilityEmployee(facilityEmployee);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SaleLead user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setSaleLead(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setSaleLead(this));
        }
        this.tasks = tasks;
    }

    public SaleLead tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public SaleLead addTask(Task task) {
        this.tasks.add(task);
        task.setSaleLead(this);
        return this;
    }

    public SaleLead removeTask(Task task) {
        this.tasks.remove(task);
        task.setSaleLead(null);
        return this;
    }

    public Set<LeadActivity> getLeadActivities() {
        return this.leadActivities;
    }

    public void setLeadActivities(Set<LeadActivity> leadActivities) {
        if (this.leadActivities != null) {
            this.leadActivities.forEach(i -> i.setSaleLead(null));
        }
        if (leadActivities != null) {
            leadActivities.forEach(i -> i.setSaleLead(this));
        }
        this.leadActivities = leadActivities;
    }

    public SaleLead leadActivities(Set<LeadActivity> leadActivities) {
        this.setLeadActivities(leadActivities);
        return this;
    }

    public SaleLead addLeadActivity(LeadActivity leadActivity) {
        this.leadActivities.add(leadActivity);
        leadActivity.setSaleLead(this);
        return this;
    }

    public SaleLead removeLeadActivity(LeadActivity leadActivity) {
        this.leadActivities.remove(leadActivity);
        leadActivity.setSaleLead(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaleLead)) {
            return false;
        }
        return getId() != null && getId().equals(((SaleLead) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleLead{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", title='" + getTitle() + "'" +
            ", customerType='" + getCustomerType() + "'" +
            ", leadSource='" + getLeadSource() + "'" +
            ", description='" + getDescription() + "'" +
            ", leadStatus='" + getLeadStatus() + "'" +
            ", dealExpiryDate='" + getDealExpiryDate() + "'" +
            "}";
    }
}
