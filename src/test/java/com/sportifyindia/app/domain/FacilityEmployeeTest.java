package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.FacilityEmployeeTestSamples.*;
import static com.sportifyindia.app.domain.FacilityTestSamples.*;
import static com.sportifyindia.app.domain.SaleLeadTestSamples.*;
import static com.sportifyindia.app.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FacilityEmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FacilityEmployee.class);
        FacilityEmployee facilityEmployee1 = getFacilityEmployeeSample1();
        FacilityEmployee facilityEmployee2 = new FacilityEmployee();
        assertThat(facilityEmployee1).isNotEqualTo(facilityEmployee2);

        facilityEmployee2.setId(facilityEmployee1.getId());
        assertThat(facilityEmployee1).isEqualTo(facilityEmployee2);

        facilityEmployee2 = getFacilityEmployeeSample2();
        assertThat(facilityEmployee1).isNotEqualTo(facilityEmployee2);
    }

    @Test
    void facilityTest() throws Exception {
        FacilityEmployee facilityEmployee = getFacilityEmployeeRandomSampleGenerator();
        Facility facilityBack = getFacilityRandomSampleGenerator();

        facilityEmployee.setFacility(facilityBack);
        assertThat(facilityEmployee.getFacility()).isEqualTo(facilityBack);

        facilityEmployee.facility(null);
        assertThat(facilityEmployee.getFacility()).isNull();
    }

    @Test
    void saleLeadTest() throws Exception {
        FacilityEmployee facilityEmployee = getFacilityEmployeeRandomSampleGenerator();
        SaleLead saleLeadBack = getSaleLeadRandomSampleGenerator();

        facilityEmployee.addSaleLead(saleLeadBack);
        assertThat(facilityEmployee.getSaleLeads()).containsOnly(saleLeadBack);
        assertThat(saleLeadBack.getFacilityEmployee()).isEqualTo(facilityEmployee);

        facilityEmployee.removeSaleLead(saleLeadBack);
        assertThat(facilityEmployee.getSaleLeads()).doesNotContain(saleLeadBack);
        assertThat(saleLeadBack.getFacilityEmployee()).isNull();

        facilityEmployee.saleLeads(new HashSet<>(Set.of(saleLeadBack)));
        assertThat(facilityEmployee.getSaleLeads()).containsOnly(saleLeadBack);
        assertThat(saleLeadBack.getFacilityEmployee()).isEqualTo(facilityEmployee);

        facilityEmployee.setSaleLeads(new HashSet<>());
        assertThat(facilityEmployee.getSaleLeads()).doesNotContain(saleLeadBack);
        assertThat(saleLeadBack.getFacilityEmployee()).isNull();
    }

    @Test
    void taskTest() throws Exception {
        FacilityEmployee facilityEmployee = getFacilityEmployeeRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        facilityEmployee.addTask(taskBack);
        assertThat(facilityEmployee.getTasks()).containsOnly(taskBack);
        assertThat(taskBack.getFacilityEmployee()).isEqualTo(facilityEmployee);

        facilityEmployee.removeTask(taskBack);
        assertThat(facilityEmployee.getTasks()).doesNotContain(taskBack);
        assertThat(taskBack.getFacilityEmployee()).isNull();

        facilityEmployee.tasks(new HashSet<>(Set.of(taskBack)));
        assertThat(facilityEmployee.getTasks()).containsOnly(taskBack);
        assertThat(taskBack.getFacilityEmployee()).isEqualTo(facilityEmployee);

        facilityEmployee.setTasks(new HashSet<>());
        assertThat(facilityEmployee.getTasks()).doesNotContain(taskBack);
        assertThat(taskBack.getFacilityEmployee()).isNull();
    }
}
