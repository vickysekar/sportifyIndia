package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.SubscriptionStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A OneTimeEventSubscribers.
 */
@Entity
@Table(name = "one_time_event_subscribers")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "onetimeeventsubscribers")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OneTimeEventSubscribers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "paid_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal paidAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private SubscriptionStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "facility", "oneTimeEventSubscribers" }, allowSetters = true)
    private OneTimeEvent oneTimeEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OneTimeEventSubscribers id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }

    public OneTimeEventSubscribers paidAmount(BigDecimal paidAmount) {
        this.setPaidAmount(paidAmount);
        return this;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public SubscriptionStatusEnum getStatus() {
        return this.status;
    }

    public OneTimeEventSubscribers status(SubscriptionStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SubscriptionStatusEnum status) {
        this.status = status;
    }

    public OneTimeEvent getOneTimeEvent() {
        return this.oneTimeEvent;
    }

    public void setOneTimeEvent(OneTimeEvent oneTimeEvent) {
        this.oneTimeEvent = oneTimeEvent;
    }

    public OneTimeEventSubscribers oneTimeEvent(OneTimeEvent oneTimeEvent) {
        this.setOneTimeEvent(oneTimeEvent);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OneTimeEventSubscribers user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OneTimeEventSubscribers)) {
            return false;
        }
        return getId() != null && getId().equals(((OneTimeEventSubscribers) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OneTimeEventSubscribers{" +
            "id=" + getId() +
            ", paidAmount=" + getPaidAmount() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
