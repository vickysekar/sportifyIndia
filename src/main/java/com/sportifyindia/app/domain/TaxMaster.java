package com.sportifyindia.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sportifyindia.app.domain.enumeration.TaxSourceEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TaxMaster.
 */
@Entity
@Table(name = "tax_master")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "taxmaster")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaxMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tax_slab", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String taxSlab;

    @NotNull
    @Column(name = "tax_name", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String taxName;

    @NotNull
    @Column(name = "is_active", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

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
    @JsonIgnoreProperties(
        value = { "user", "address", "facilityEmployees", "courses", "oneTimeEvents", "utilities", "saleLeads", "taxMasters", "discounts" },
        allowSetters = true
    )
    private Facility facility;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TaxMaster id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaxSlab() {
        return this.taxSlab;
    }

    public TaxMaster taxSlab(String taxSlab) {
        this.setTaxSlab(taxSlab);
        return this;
    }

    public void setTaxSlab(String taxSlab) {
        this.taxSlab = taxSlab;
    }

    public String getTaxName() {
        return this.taxName;
    }

    public TaxMaster taxName(String taxName) {
        this.setTaxName(taxName);
        return this;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public TaxMaster isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getTaxType() {
        return this.taxType;
    }

    public TaxMaster taxType(String taxType) {
        this.setTaxType(taxType);
        return this;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public TaxSourceEnum getTaxSource() {
        return this.taxSource;
    }

    public TaxMaster taxSource(TaxSourceEnum taxSource) {
        this.setTaxSource(taxSource);
        return this;
    }

    public void setTaxSource(TaxSourceEnum taxSource) {
        this.taxSource = taxSource;
    }

    public Facility getFacility() {
        return this.facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public TaxMaster facility(Facility facility) {
        this.setFacility(facility);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaxMaster)) {
            return false;
        }
        return getId() != null && getId().equals(((TaxMaster) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaxMaster{" +
            "id=" + getId() +
            ", taxSlab='" + getTaxSlab() + "'" +
            ", taxName='" + getTaxName() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", taxType='" + getTaxType() + "'" +
            ", taxSource='" + getTaxSource() + "'" +
            "}";
    }
}
