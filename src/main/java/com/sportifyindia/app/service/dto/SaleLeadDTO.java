package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.CustomerTypeEnum;
import com.sportifyindia.app.domain.enumeration.LeadStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.SaleLead} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SaleLeadDTO implements Serializable {

    private Long id;

    @NotNull
    private String fullName;

    @NotNull
    private String lastName;

    @NotNull
    private String email;

    @NotNull
    private String phoneNumber;

    private String title;

    @NotNull
    private CustomerTypeEnum customerType;

    private String leadSource;

    private String description;

    @NotNull
    private LeadStatusEnum leadStatus;

    @NotNull
    private Instant dealExpiryDate;

    private FacilityDTO facility;

    private FacilityEmployeeDTO facilityEmployee;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CustomerTypeEnum getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerTypeEnum customerType) {
        this.customerType = customerType;
    }

    public String getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(String leadSource) {
        this.leadSource = leadSource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LeadStatusEnum getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(LeadStatusEnum leadStatus) {
        this.leadStatus = leadStatus;
    }

    public Instant getDealExpiryDate() {
        return dealExpiryDate;
    }

    public void setDealExpiryDate(Instant dealExpiryDate) {
        this.dealExpiryDate = dealExpiryDate;
    }

    public FacilityDTO getFacility() {
        return facility;
    }

    public void setFacility(FacilityDTO facility) {
        this.facility = facility;
    }

    public FacilityEmployeeDTO getFacilityEmployee() {
        return facilityEmployee;
    }

    public void setFacilityEmployee(FacilityEmployeeDTO facilityEmployee) {
        this.facilityEmployee = facilityEmployee;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SaleLeadDTO)) {
            return false;
        }

        SaleLeadDTO saleLeadDTO = (SaleLeadDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, saleLeadDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SaleLeadDTO{" +
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
            ", facility=" + getFacility() +
            ", facilityEmployee=" + getFacilityEmployee() +
            ", user=" + getUser() +
            "}";
    }
}
