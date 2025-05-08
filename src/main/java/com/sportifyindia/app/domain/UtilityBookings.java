package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.BookingStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UtilityBookings.
 */
@Entity
@Table(name = "utility_bookings")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "utilitybookings")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtilityBookings extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "amount_paid", precision = 21, scale = 2, nullable = false)
    private BigDecimal amountPaid;

    @NotNull
    @Column(name = "booked_quantity", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer bookedQuantity;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private BookingStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "facility", "utilityAvailableDays", "utilityExceptionDays", "utilitySlots", "utilityBookings" },
        allowSetters = true
    )
    private Utility utility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "utility", "timeSlots", "utilityBookings" }, allowSetters = true)
    private UtilitySlots utilitySlots;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties(value = { "authorities", "createdBy", "lastModifiedBy" }, allowSetters = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UtilityBookings id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmountPaid() {
        return this.amountPaid;
    }

    public UtilityBookings amountPaid(BigDecimal amountPaid) {
        this.setAmountPaid(amountPaid);
        return this;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Integer getBookedQuantity() {
        return this.bookedQuantity;
    }

    public UtilityBookings bookedQuantity(Integer bookedQuantity) {
        this.setBookedQuantity(bookedQuantity);
        return this;
    }

    public void setBookedQuantity(Integer bookedQuantity) {
        this.bookedQuantity = bookedQuantity;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public UtilityBookings startTime(Instant startTime) {
        this.setStartTime(startTime);
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public UtilityBookings endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BookingStatusEnum getStatus() {
        return this.status;
    }

    public UtilityBookings status(BookingStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BookingStatusEnum status) {
        this.status = status;
    }

    public Utility getUtility() {
        return this.utility;
    }

    public void setUtility(Utility utility) {
        this.utility = utility;
    }

    public UtilityBookings utility(Utility utility) {
        this.setUtility(utility);
        return this;
    }

    public UtilitySlots getUtilitySlots() {
        return this.utilitySlots;
    }

    public void setUtilitySlots(UtilitySlots utilitySlots) {
        this.utilitySlots = utilitySlots;
    }

    public UtilityBookings utilitySlots(UtilitySlots utilitySlots) {
        this.setUtilitySlots(utilitySlots);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UtilityBookings user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilityBookings)) {
            return false;
        }
        return getId() != null && getId().equals(((UtilityBookings) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilityBookings{" +
            "id=" + getId() +
            ", amountPaid=" + getAmountPaid() +
            ", bookedQuantity=" + getBookedQuantity() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
