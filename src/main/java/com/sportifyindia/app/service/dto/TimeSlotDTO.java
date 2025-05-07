package com.sportifyindia.app.service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class TimeSlotDTO {

    private Instant startTime;
    private Instant endTime;

    public TimeSlotDTO() {}

    public TimeSlotDTO(Instant startTime, Instant endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
