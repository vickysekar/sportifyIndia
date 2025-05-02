package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LeadActivity.
 */
@Entity
@Table(name = "lead_activity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "leadactivity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LeadActivity extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "activity_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String activityType;

    @Column(name = "description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "facility", "facilityEmployee", "user", "tasks", "leadActivities" }, allowSetters = true)
    private SaleLead saleLead;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LeadActivity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityType() {
        return this.activityType;
    }

    public LeadActivity activityType(String activityType) {
        this.setActivityType(activityType);
        return this;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDescription() {
        return this.description;
    }

    public LeadActivity description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SaleLead getSaleLead() {
        return this.saleLead;
    }

    public void setSaleLead(SaleLead saleLead) {
        this.saleLead = saleLead;
    }

    public LeadActivity saleLead(SaleLead saleLead) {
        this.setSaleLead(saleLead);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LeadActivity)) {
            return false;
        }
        return getId() != null && getId().equals(((LeadActivity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LeadActivity{" +
            "id=" + getId() +
            ", activityType='" + getActivityType() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
