package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UtilityExceptionDays.
 */
@Entity
@Table(name = "utility_exception_days")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "utilityexceptiondays")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtilityExceptionDays extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reason", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String reason;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "nested_timeslots")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String nestedTimeslots;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "facility", "utilityAvailableDays", "utilityExceptionDays", "utilitySlots", "utilityBookings" },
        allowSetters = true
    )
    private Utility utility;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UtilityExceptionDays id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return this.reason;
    }

    public UtilityExceptionDays reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getDate() {
        return this.date;
    }

    public UtilityExceptionDays date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getNestedTimeslots() {
        return this.nestedTimeslots;
    }

    public UtilityExceptionDays nestedTimeslots(String nestedTimeslots) {
        this.setNestedTimeslots(nestedTimeslots);
        return this;
    }

    public void setNestedTimeslots(String nestedTimeslots) {
        this.nestedTimeslots = nestedTimeslots;
    }

    public Utility getUtility() {
        return this.utility;
    }

    public void setUtility(Utility utility) {
        this.utility = utility;
    }

    public UtilityExceptionDays utility(Utility utility) {
        this.setUtility(utility);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilityExceptionDays)) {
            return false;
        }
        return getId() != null && getId().equals(((UtilityExceptionDays) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilityExceptionDays{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", date='" + getDate() + "'" +
            ", nestedTimeslots='" + getNestedTimeslots() + "'" +
            "}";
    }
}
