package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.NotesTestSamples.*;
import static com.sportifyindia.app.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notes.class);
        Notes notes1 = getNotesSample1();
        Notes notes2 = new Notes();
        assertThat(notes1).isNotEqualTo(notes2);

        notes2.setId(notes1.getId());
        assertThat(notes1).isEqualTo(notes2);

        notes2 = getNotesSample2();
        assertThat(notes1).isNotEqualTo(notes2);
    }

    @Test
    void taskTest() throws Exception {
        Notes notes = getNotesRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        notes.setTask(taskBack);
        assertThat(notes.getTask()).isEqualTo(taskBack);

        notes.task(null);
        assertThat(notes.getTask()).isNull();
    }
}
