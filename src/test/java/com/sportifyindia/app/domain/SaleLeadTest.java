package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.FacilityEmployeeTestSamples.*;
import static com.sportifyindia.app.domain.FacilityTestSamples.*;
import static com.sportifyindia.app.domain.LeadActivityTestSamples.*;
import static com.sportifyindia.app.domain.SaleLeadTestSamples.*;
import static com.sportifyindia.app.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SaleLeadTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleLead.class);
        SaleLead saleLead1 = getSaleLeadSample1();
        SaleLead saleLead2 = new SaleLead();
        assertThat(saleLead1).isNotEqualTo(saleLead2);

        saleLead2.setId(saleLead1.getId());
        assertThat(saleLead1).isEqualTo(saleLead2);

        saleLead2 = getSaleLeadSample2();
        assertThat(saleLead1).isNotEqualTo(saleLead2);
    }

    @Test
    void facilityTest() throws Exception {
        SaleLead saleLead = getSaleLeadRandomSampleGenerator();
        Facility facilityBack = getFacilityRandomSampleGenerator();

        saleLead.setFacility(facilityBack);
        assertThat(saleLead.getFacility()).isEqualTo(facilityBack);

        saleLead.facility(null);
        assertThat(saleLead.getFacility()).isNull();
    }

    @Test
    void facilityEmployeeTest() throws Exception {
        SaleLead saleLead = getSaleLeadRandomSampleGenerator();
        FacilityEmployee facilityEmployeeBack = getFacilityEmployeeRandomSampleGenerator();

        saleLead.setFacilityEmployee(facilityEmployeeBack);
        assertThat(saleLead.getFacilityEmployee()).isEqualTo(facilityEmployeeBack);

        saleLead.facilityEmployee(null);
        assertThat(saleLead.getFacilityEmployee()).isNull();
    }

    @Test
    void taskTest() throws Exception {
        SaleLead saleLead = getSaleLeadRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        saleLead.addTask(taskBack);
        assertThat(saleLead.getTasks()).containsOnly(taskBack);
        assertThat(taskBack.getSaleLead()).isEqualTo(saleLead);

        saleLead.removeTask(taskBack);
        assertThat(saleLead.getTasks()).doesNotContain(taskBack);
        assertThat(taskBack.getSaleLead()).isNull();

        saleLead.tasks(new HashSet<>(Set.of(taskBack)));
        assertThat(saleLead.getTasks()).containsOnly(taskBack);
        assertThat(taskBack.getSaleLead()).isEqualTo(saleLead);

        saleLead.setTasks(new HashSet<>());
        assertThat(saleLead.getTasks()).doesNotContain(taskBack);
        assertThat(taskBack.getSaleLead()).isNull();
    }

    @Test
    void leadActivityTest() throws Exception {
        SaleLead saleLead = getSaleLeadRandomSampleGenerator();
        LeadActivity leadActivityBack = getLeadActivityRandomSampleGenerator();

        saleLead.addLeadActivity(leadActivityBack);
        assertThat(saleLead.getLeadActivities()).containsOnly(leadActivityBack);
        assertThat(leadActivityBack.getSaleLead()).isEqualTo(saleLead);

        saleLead.removeLeadActivity(leadActivityBack);
        assertThat(saleLead.getLeadActivities()).doesNotContain(leadActivityBack);
        assertThat(leadActivityBack.getSaleLead()).isNull();

        saleLead.leadActivities(new HashSet<>(Set.of(leadActivityBack)));
        assertThat(saleLead.getLeadActivities()).containsOnly(leadActivityBack);
        assertThat(leadActivityBack.getSaleLead()).isEqualTo(saleLead);

        saleLead.setLeadActivities(new HashSet<>());
        assertThat(saleLead.getLeadActivities()).doesNotContain(leadActivityBack);
        assertThat(leadActivityBack.getSaleLead()).isNull();
    }
}
