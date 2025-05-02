package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.TaskStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Task.
 */
@Entity
@Table(name = "task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "task")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Task extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "task_title", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String taskTitle;

    @Column(name = "task_description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String taskDescription;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private TaskStatusEnum taskStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "facility", "facilityEmployee", "user", "tasks", "leadActivities" }, allowSetters = true)
    private SaleLead saleLead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "facility", "saleLeads", "tasks" }, allowSetters = true)
    private FacilityEmployee facilityEmployee;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "task" }, allowSetters = true)
    private Set<Notes> notes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Task id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskTitle() {
        return this.taskTitle;
    }

    public Task taskTitle(String taskTitle) {
        this.setTaskTitle(taskTitle);
        return this;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return this.taskDescription;
    }

    public Task taskDescription(String taskDescription) {
        this.setTaskDescription(taskDescription);
        return this;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Instant getDueDate() {
        return this.dueDate;
    }

    public Task dueDate(Instant dueDate) {
        this.setDueDate(dueDate);
        return this;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatusEnum getTaskStatus() {
        return this.taskStatus;
    }

    public Task taskStatus(TaskStatusEnum taskStatus) {
        this.setTaskStatus(taskStatus);
        return this;
    }

    public void setTaskStatus(TaskStatusEnum taskStatus) {
        this.taskStatus = taskStatus;
    }

    public SaleLead getSaleLead() {
        return this.saleLead;
    }

    public void setSaleLead(SaleLead saleLead) {
        this.saleLead = saleLead;
    }

    public Task saleLead(SaleLead saleLead) {
        this.setSaleLead(saleLead);
        return this;
    }

    public FacilityEmployee getFacilityEmployee() {
        return this.facilityEmployee;
    }

    public void setFacilityEmployee(FacilityEmployee facilityEmployee) {
        this.facilityEmployee = facilityEmployee;
    }

    public Task facilityEmployee(FacilityEmployee facilityEmployee) {
        this.setFacilityEmployee(facilityEmployee);
        return this;
    }

    public Set<Notes> getNotes() {
        return this.notes;
    }

    public void setNotes(Set<Notes> notes) {
        if (this.notes != null) {
            this.notes.forEach(i -> i.setTask(null));
        }
        if (notes != null) {
            notes.forEach(i -> i.setTask(this));
        }
        this.notes = notes;
    }

    public Task notes(Set<Notes> notes) {
        this.setNotes(notes);
        return this;
    }

    public Task addNotes(Notes notes) {
        this.notes.add(notes);
        notes.setTask(this);
        return this;
    }

    public Task removeNotes(Notes notes) {
        this.notes.remove(notes);
        notes.setTask(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return getId() != null && getId().equals(((Task) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", taskTitle='" + getTaskTitle() + "'" +
            ", taskDescription='" + getTaskDescription() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", taskStatus='" + getTaskStatus() + "'" +
            "}";
    }
}
