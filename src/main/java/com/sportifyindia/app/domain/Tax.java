package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.TaxSourceEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tax.
 */
@Entity
@Table(name = "tax")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "tax")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tax implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "net_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal netAmount;

    @NotNull
    @Column(name = "computed_slab", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String computedSlab;

    @NotNull
    @Column(name = "computed_amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal computedAmount;

    @NotNull
    @Column(name = "tax_type", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String taxType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tax_source", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private TaxSourceEnum taxSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "order", "user", "taxes" }, allowSetters = true)
    private Charge charge;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tax id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getNetAmount() {
        return this.netAmount;
    }

    public Tax netAmount(BigDecimal netAmount) {
        this.setNetAmount(netAmount);
        return this;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getComputedSlab() {
        return this.computedSlab;
    }

    public Tax computedSlab(String computedSlab) {
        this.setComputedSlab(computedSlab);
        return this;
    }

    public void setComputedSlab(String computedSlab) {
        this.computedSlab = computedSlab;
    }

    public BigDecimal getComputedAmount() {
        return this.computedAmount;
    }

    public Tax computedAmount(BigDecimal computedAmount) {
        this.setComputedAmount(computedAmount);
        return this;
    }

    public void setComputedAmount(BigDecimal computedAmount) {
        this.computedAmount = computedAmount;
    }

    public String getTaxType() {
        return this.taxType;
    }

    public Tax taxType(String taxType) {
        this.setTaxType(taxType);
        return this;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public TaxSourceEnum getTaxSource() {
        return this.taxSource;
    }

    public Tax taxSource(TaxSourceEnum taxSource) {
        this.setTaxSource(taxSource);
        return this;
    }

    public void setTaxSource(TaxSourceEnum taxSource) {
        this.taxSource = taxSource;
    }

    public Charge getCharge() {
        return this.charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }

    public Tax charge(Charge charge) {
        this.setCharge(charge);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tax)) {
            return false;
        }
        return getId() != null && getId().equals(((Tax) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tax{" +
            "id=" + getId() +
            ", netAmount=" + getNetAmount() +
            ", computedSlab='" + getComputedSlab() + "'" +
            ", computedAmount=" + getComputedAmount() +
            ", taxType='" + getTaxType() + "'" +
            ", taxSource='" + getTaxSource() + "'" +
            "}";
    }
}
