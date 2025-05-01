package com.sportifyindia.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.Notes} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotesDTO implements Serializable {

    private Long id;

    @NotNull
    private String noteText;

    private TaskDTO task;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotesDTO)) {
            return false;
        }

        NotesDTO notesDTO = (NotesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotesDTO{" +
            "id=" + getId() +
            ", noteText='" + getNoteText() + "'" +
            ", task=" + getTask() +
            "}";
    }
}
