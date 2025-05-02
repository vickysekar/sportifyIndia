package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.EmployeeRoleEnum;
import com.sportifyindia.app.domain.enumeration.EmployeeStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FacilityEmployee.
 */
@Entity
@Table(name = "facility_employee")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "facilityemployee")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FacilityEmployee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "position", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String position;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private EmployeeStatusEnum status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private EmployeeRoleEnum role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    @JsonIgnoreProperties(
        value = { "user", "address", "facilityEmployees", "courses", "oneTimeEvents", "utilities", "saleLeads", "taxMasters", "discounts" },
        allowSetters = true
    )
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facilityEmployee")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "facility", "facilityEmployee", "user", "tasks", "leadActivities" }, allowSetters = true)
    private Set<SaleLead> saleLeads = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facilityEmployee")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "saleLead", "facilityEmployee", "notes" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FacilityEmployee id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosition() {
        return this.position;
    }

    public FacilityEmployee position(String position) {
        this.setPosition(position);
        return this;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public EmployeeStatusEnum getStatus() {
        return this.status;
    }

    public FacilityEmployee status(EmployeeStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EmployeeStatusEnum status) {
        this.status = status;
    }

    public EmployeeRoleEnum getRole() {
        return this.role;
    }

    public FacilityEmployee role(EmployeeRoleEnum role) {
        this.setRole(role);
        return this;
    }

    public void setRole(EmployeeRoleEnum role) {
        this.role = role;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public FacilityEmployee facility(Facility facility) {
        this.setFacility(facility);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FacilityEmployee user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<SaleLead> getSaleLeads() {
        return this.saleLeads;
    }

    public void setSaleLeads(Set<SaleLead> saleLeads) {
        if (this.saleLeads != null) {
            this.saleLeads.forEach(i -> i.setFacilityEmployee(null));
        }
        if (saleLeads != null) {
            saleLeads.forEach(i -> i.setFacilityEmployee(this));
        }
        this.saleLeads = saleLeads;
    }

    public FacilityEmployee saleLeads(Set<SaleLead> saleLeads) {
        this.setSaleLeads(saleLeads);
        return this;
    }

    public FacilityEmployee addSaleLead(SaleLead saleLead) {
        this.saleLeads.add(saleLead);
        saleLead.setFacilityEmployee(this);
        return this;
    }

    public FacilityEmployee removeSaleLead(SaleLead saleLead) {
        this.saleLeads.remove(saleLead);
        saleLead.setFacilityEmployee(null);
        return this;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setFacilityEmployee(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setFacilityEmployee(this));
        }
        this.tasks = tasks;
    }

    public FacilityEmployee tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public FacilityEmployee addTask(Task task) {
        this.tasks.add(task);
        task.setFacilityEmployee(this);
        return this;
    }

    public FacilityEmployee removeTask(Task task) {
        this.tasks.remove(task);
        task.setFacilityEmployee(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacilityEmployee)) {
            return false;
        }
        return getId() != null && getId().equals(((FacilityEmployee) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FacilityEmployee{" +
            "id=" + getId() +
            ", position='" + getPosition() + "'" +
            ", status='" + getStatus() + "'" +
            ", role='" + getRole() + "'" +
            "}";
    }
}
