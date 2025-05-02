package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.BusinessEntityEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Charge.
 */
@Entity
@Table(name = "charge")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "charge")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Charge extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "be_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private BusinessEntityEnum beType;

    @NotNull
    @Column(name = "computed_charge", precision = 21, scale = 2, nullable = false)
    private BigDecimal computedCharge;

    @Column(name = "computed_discount", precision = 21, scale = 2)
    private BigDecimal computedDiscount;

    @NotNull
    @Column(name = "total", precision = 21, scale = 2, nullable = false)
    private BigDecimal total;

    @NotNull
    @Column(name = "currency", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String currency;

    @Column(name = "disc_reason")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String discReason;

    @Column(name = "exchange_rate", precision = 21, scale = 2)
    private BigDecimal exchangeRate;

    @NotNull
    @Column(name = "final_charge", precision = 21, scale = 2, nullable = false)
    private BigDecimal finalCharge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "charges", "payments" }, allowSetters = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "charge")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "charge" }, allowSetters = true)
    private Set<Tax> taxes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Charge id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessEntityEnum getBeType() {
        return this.beType;
    }

    public Charge beType(BusinessEntityEnum beType) {
        this.setBeType(beType);
        return this;
    }

    public void setBeType(BusinessEntityEnum beType) {
        this.beType = beType;
    }

    public BigDecimal getComputedCharge() {
        return this.computedCharge;
    }

    public Charge computedCharge(BigDecimal computedCharge) {
        this.setComputedCharge(computedCharge);
        return this;
    }

    public void setComputedCharge(BigDecimal computedCharge) {
        this.computedCharge = computedCharge;
    }

    public BigDecimal getComputedDiscount() {
        return this.computedDiscount;
    }

    public Charge computedDiscount(BigDecimal computedDiscount) {
        this.setComputedDiscount(computedDiscount);
        return this;
    }

    public void setComputedDiscount(BigDecimal computedDiscount) {
        this.computedDiscount = computedDiscount;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public Charge total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Charge currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDiscReason() {
        return this.discReason;
    }

    public Charge discReason(String discReason) {
        this.setDiscReason(discReason);
        return this;
    }

    public void setDiscReason(String discReason) {
        this.discReason = discReason;
    }

    public BigDecimal getExchangeRate() {
        return this.exchangeRate;
    }

    public Charge exchangeRate(BigDecimal exchangeRate) {
        this.setExchangeRate(exchangeRate);
        return this;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getFinalCharge() {
        return this.finalCharge;
    }

    public Charge finalCharge(BigDecimal finalCharge) {
        this.setFinalCharge(finalCharge);
        return this;
    }

    public void setFinalCharge(BigDecimal finalCharge) {
        this.finalCharge = finalCharge;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Charge order(Order order) {
        this.setOrder(order);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Charge user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Tax> getTaxes() {
        return this.taxes;
    }

    public void setTaxes(Set<Tax> taxes) {
        if (this.taxes != null) {
            this.taxes.forEach(i -> i.setCharge(null));
        }
        if (taxes != null) {
            taxes.forEach(i -> i.setCharge(this));
        }
        this.taxes = taxes;
    }

    public Charge taxes(Set<Tax> taxes) {
        this.setTaxes(taxes);
        return this;
    }

    public Charge addTax(Tax tax) {
        this.taxes.add(tax);
        tax.setCharge(this);
        return this;
    }

    public Charge removeTax(Tax tax) {
        this.taxes.remove(tax);
        tax.setCharge(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Charge)) {
            return false;
        }
        return getId() != null && getId().equals(((Charge) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Charge{" +
            "id=" + getId() +
            ", beType='" + getBeType() + "'" +
            ", computedCharge=" + getComputedCharge() +
            ", computedDiscount=" + getComputedDiscount() +
            ", total=" + getTotal() +
            ", currency='" + getCurrency() + "'" +
            ", discReason='" + getDiscReason() + "'" +
            ", exchangeRate=" + getExchangeRate() +
            ", finalCharge=" + getFinalCharge() +
            "}";
    }
}
