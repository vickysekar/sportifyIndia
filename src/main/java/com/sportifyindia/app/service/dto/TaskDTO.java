package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.TaskStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.Task} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskDTO implements Serializable {

    private Long id;

    @NotNull
    private String taskTitle;

    private String taskDescription;

    @NotNull
    private Instant dueDate;

    @NotNull
    private TaskStatusEnum taskStatus;

    private SaleLeadDTO saleLead;

    private FacilityEmployeeDTO facilityEmployee;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatusEnum getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatusEnum taskStatus) {
        this.taskStatus = taskStatus;
    }

    public SaleLeadDTO getSaleLead() {
        return saleLead;
    }

    public void setSaleLead(SaleLeadDTO saleLead) {
        this.saleLead = saleLead;
    }

    public FacilityEmployeeDTO getFacilityEmployee() {
        return facilityEmployee;
    }

    public void setFacilityEmployee(FacilityEmployeeDTO facilityEmployee) {
        this.facilityEmployee = facilityEmployee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskDTO)) {
            return false;
        }

        TaskDTO taskDTO = (TaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskDTO{" +
            "id=" + getId() +
            ", taskTitle='" + getTaskTitle() + "'" +
            ", taskDescription='" + getTaskDescription() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", taskStatus='" + getTaskStatus() + "'" +
            ", saleLead=" + getSaleLead() +
            ", facilityEmployee=" + getFacilityEmployee() +
            "}";
    }
}
