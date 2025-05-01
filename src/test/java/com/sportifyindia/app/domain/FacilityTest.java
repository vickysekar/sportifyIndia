package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.AddressTestSamples.*;
import static com.sportifyindia.app.domain.CourseTestSamples.*;
import static com.sportifyindia.app.domain.DiscountTestSamples.*;
import static com.sportifyindia.app.domain.FacilityEmployeeTestSamples.*;
import static com.sportifyindia.app.domain.FacilityTestSamples.*;
import static com.sportifyindia.app.domain.OneTimeEventTestSamples.*;
import static com.sportifyindia.app.domain.SaleLeadTestSamples.*;
import static com.sportifyindia.app.domain.TaxMasterTestSamples.*;
import static com.sportifyindia.app.domain.UtilityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FacilityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Facility.class);
        Facility facility1 = getFacilitySample1();
        Facility facility2 = new Facility();
        assertThat(facility1).isNotEqualTo(facility2);

        facility2.setId(facility1.getId());
        assertThat(facility1).isEqualTo(facility2);

        facility2 = getFacilitySample2();
        assertThat(facility1).isNotEqualTo(facility2);
    }

    @Test
    void addressTest() throws Exception {
        Facility facility = getFacilityRandomSampleGenerator();
        Address addressBack = getAddressRandomSampleGenerator();

        facility.setAddress(addressBack);
        assertThat(facility.getAddress()).isEqualTo(addressBack);
        assertThat(addressBack.getFacility()).isEqualTo(facility);

        facility.address(null);
        assertThat(facility.getAddress()).isNull();
        assertThat(addressBack.getFacility()).isNull();
    }

    @Test
    void facilityEmployeeTest() throws Exception {
        Facility facility = getFacilityRandomSampleGenerator();
        FacilityEmployee facilityEmployeeBack = getFacilityEmployeeRandomSampleGenerator();

        facility.addFacilityEmployee(facilityEmployeeBack);
        assertThat(facility.getFacilityEmployees()).containsOnly(facilityEmployeeBack);
        assertThat(facilityEmployeeBack.getFacility()).isEqualTo(facility);

        facility.removeFacilityEmployee(facilityEmployeeBack);
        assertThat(facility.getFacilityEmployees()).doesNotContain(facilityEmployeeBack);
        assertThat(facilityEmployeeBack.getFacility()).isNull();

        facility.facilityEmployees(new HashSet<>(Set.of(facilityEmployeeBack)));
        assertThat(facility.getFacilityEmployees()).containsOnly(facilityEmployeeBack);
        assertThat(facilityEmployeeBack.getFacility()).isEqualTo(facility);

        facility.setFacilityEmployees(new HashSet<>());
        assertThat(facility.getFacilityEmployees()).doesNotContain(facilityEmployeeBack);
        assertThat(facilityEmployeeBack.getFacility()).isNull();
    }

    @Test
    void courseTest() throws Exception {
        Facility facility = getFacilityRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        facility.addCourse(courseBack);
        assertThat(facility.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getFacility()).isEqualTo(facility);

        facility.removeCourse(courseBack);
        assertThat(facility.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getFacility()).isNull();

        facility.courses(new HashSet<>(Set.of(courseBack)));
        assertThat(facility.getCourses()).containsOnly(courseBack);
        assertThat(courseBack.getFacility()).isEqualTo(facility);

        facility.setCourses(new HashSet<>());
        assertThat(facility.getCourses()).doesNotContain(courseBack);
        assertThat(courseBack.getFacility()).isNull();
    }

    @Test
    void oneTimeEventTest() throws Exception {
        Facility facility = getFacilityRandomSampleGenerator();
        OneTimeEvent oneTimeEventBack = getOneTimeEventRandomSampleGenerator();

        facility.addOneTimeEvent(oneTimeEventBack);
        assertThat(facility.getOneTimeEvents()).containsOnly(oneTimeEventBack);
        assertThat(oneTimeEventBack.getFacility()).isEqualTo(facility);

        facility.removeOneTimeEvent(oneTimeEventBack);
        assertThat(facility.getOneTimeEvents()).doesNotContain(oneTimeEventBack);
        assertThat(oneTimeEventBack.getFacility()).isNull();

        facility.oneTimeEvents(new HashSet<>(Set.of(oneTimeEventBack)));
        assertThat(facility.getOneTimeEvents()).containsOnly(oneTimeEventBack);
        assertThat(oneTimeEventBack.getFacility()).isEqualTo(facility);

        facility.setOneTimeEvents(new HashSet<>());
        assertThat(facility.getOneTimeEvents()).doesNotContain(oneTimeEventBack);
        assertThat(oneTimeEventBack.getFacility()).isNull();
    }

    @Test
    void utilityTest() throws Exception {
        Facility facility = getFacilityRandomSampleGenerator();
        Utility utilityBack = getUtilityRandomSampleGenerator();

        facility.addUtility(utilityBack);
        assertThat(facility.getUtilities()).containsOnly(utilityBack);
        assertThat(utilityBack.getFacility()).isEqualTo(facility);

        facility.removeUtility(utilityBack);
        assertThat(facility.getUtilities()).doesNotContain(utilityBack);
        assertThat(utilityBack.getFacility()).isNull();

        facility.utilities(new HashSet<>(Set.of(utilityBack)));
        assertThat(facility.getUtilities()).containsOnly(utilityBack);
        assertThat(utilityBack.getFacility()).isEqualTo(facility);

        facility.setUtilities(new HashSet<>());
        assertThat(facility.getUtilities()).doesNotContain(utilityBack);
        assertThat(utilityBack.getFacility()).isNull();
    }

    @Test
    void saleLeadTest() throws Exception {
        Facility facility = getFacilityRandomSampleGenerator();
        SaleLead saleLeadBack = getSaleLeadRandomSampleGenerator();

        facility.addSaleLead(saleLeadBack);
        assertThat(facility.getSaleLeads()).containsOnly(saleLeadBack);
        assertThat(saleLeadBack.getFacility()).isEqualTo(facility);

        facility.removeSaleLead(saleLeadBack);
        assertThat(facility.getSaleLeads()).doesNotContain(saleLeadBack);
        assertThat(saleLeadBack.getFacility()).isNull();

        facility.saleLeads(new HashSet<>(Set.of(saleLeadBack)));
        assertThat(facility.getSaleLeads()).containsOnly(saleLeadBack);
        assertThat(saleLeadBack.getFacility()).isEqualTo(facility);

        facility.setSaleLeads(new HashSet<>());
        assertThat(facility.getSaleLeads()).doesNotContain(saleLeadBack);
        assertThat(saleLeadBack.getFacility()).isNull();
    }

    @Test
    void taxMasterTest() throws Exception {
        Facility facility = getFacilityRandomSampleGenerator();
        TaxMaster taxMasterBack = getTaxMasterRandomSampleGenerator();

        facility.addTaxMaster(taxMasterBack);
        assertThat(facility.getTaxMasters()).containsOnly(taxMasterBack);
        assertThat(taxMasterBack.getFacility()).isEqualTo(facility);

        facility.removeTaxMaster(taxMasterBack);
        assertThat(facility.getTaxMasters()).doesNotContain(taxMasterBack);
        assertThat(taxMasterBack.getFacility()).isNull();

        facility.taxMasters(new HashSet<>(Set.of(taxMasterBack)));
        assertThat(facility.getTaxMasters()).containsOnly(taxMasterBack);
        assertThat(taxMasterBack.getFacility()).isEqualTo(facility);

        facility.setTaxMasters(new HashSet<>());
        assertThat(facility.getTaxMasters()).doesNotContain(taxMasterBack);
        assertThat(taxMasterBack.getFacility()).isNull();
    }

    @Test
    void discountTest() throws Exception {
        Facility facility = getFacilityRandomSampleGenerator();
        Discount discountBack = getDiscountRandomSampleGenerator();

        facility.addDiscount(discountBack);
        assertThat(facility.getDiscounts()).containsOnly(discountBack);
        assertThat(discountBack.getFacility()).isEqualTo(facility);

        facility.removeDiscount(discountBack);
        assertThat(facility.getDiscounts()).doesNotContain(discountBack);
        assertThat(discountBack.getFacility()).isNull();

        facility.discounts(new HashSet<>(Set.of(discountBack)));
        assertThat(facility.getDiscounts()).containsOnly(discountBack);
        assertThat(discountBack.getFacility()).isEqualTo(facility);

        facility.setDiscounts(new HashSet<>());
        assertThat(facility.getDiscounts()).doesNotContain(discountBack);
        assertThat(discountBack.getFacility()).isNull();
    }
}
