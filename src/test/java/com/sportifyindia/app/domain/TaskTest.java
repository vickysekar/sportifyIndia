package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.FacilityEmployeeTestSamples.*;
import static com.sportifyindia.app.domain.NotesTestSamples.*;
import static com.sportifyindia.app.domain.SaleLeadTestSamples.*;
import static com.sportifyindia.app.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = getTaskSample1();
        Task task2 = new Task();
        assertThat(task1).isNotEqualTo(task2);

        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);

        task2 = getTaskSample2();
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    void saleLeadTest() throws Exception {
        Task task = getTaskRandomSampleGenerator();
        SaleLead saleLeadBack = getSaleLeadRandomSampleGenerator();

        task.setSaleLead(saleLeadBack);
        assertThat(task.getSaleLead()).isEqualTo(saleLeadBack);

        task.saleLead(null);
        assertThat(task.getSaleLead()).isNull();
    }

    @Test
    void facilityEmployeeTest() throws Exception {
        Task task = getTaskRandomSampleGenerator();
        FacilityEmployee facilityEmployeeBack = getFacilityEmployeeRandomSampleGenerator();

        task.setFacilityEmployee(facilityEmployeeBack);
        assertThat(task.getFacilityEmployee()).isEqualTo(facilityEmployeeBack);

        task.facilityEmployee(null);
        assertThat(task.getFacilityEmployee()).isNull();
    }

    @Test
    void notesTest() throws Exception {
        Task task = getTaskRandomSampleGenerator();
        Notes notesBack = getNotesRandomSampleGenerator();

        task.addNotes(notesBack);
        assertThat(task.getNotes()).containsOnly(notesBack);
        assertThat(notesBack.getTask()).isEqualTo(task);

        task.removeNotes(notesBack);
        assertThat(task.getNotes()).doesNotContain(notesBack);
        assertThat(notesBack.getTask()).isNull();

        task.notes(new HashSet<>(Set.of(notesBack)));
        assertThat(task.getNotes()).containsOnly(notesBack);
        assertThat(notesBack.getTask()).isEqualTo(task);

        task.setNotes(new HashSet<>());
        assertThat(task.getNotes()).doesNotContain(notesBack);
        assertThat(notesBack.getTask()).isNull();
    }
}
