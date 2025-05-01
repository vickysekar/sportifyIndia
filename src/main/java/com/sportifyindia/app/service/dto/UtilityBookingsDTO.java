package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.BookingStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.UtilityBookings} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UtilityBookingsDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal amountPaid;

    @NotNull
    private Integer bookedQuantity;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    @NotNull
    private BookingStatusEnum status;

    private UtilityDTO utility;

    private UtilitySlotsDTO utilitySlots;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Integer getBookedQuantity() {
        return bookedQuantity;
    }

    public void setBookedQuantity(Integer bookedQuantity) {
        this.bookedQuantity = bookedQuantity;
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

    public BookingStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BookingStatusEnum status) {
        this.status = status;
    }

    public UtilityDTO getUtility() {
        return utility;
    }

    public void setUtility(UtilityDTO utility) {
        this.utility = utility;
    }

    public UtilitySlotsDTO getUtilitySlots() {
        return utilitySlots;
    }

    public void setUtilitySlots(UtilitySlotsDTO utilitySlots) {
        this.utilitySlots = utilitySlots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilityBookingsDTO)) {
            return false;
        }

        UtilityBookingsDTO utilityBookingsDTO = (UtilityBookingsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, utilityBookingsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilityBookingsDTO{" +
            "id=" + getId() +
            ", amountPaid=" + getAmountPaid() +
            ", bookedQuantity=" + getBookedQuantity() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", utility=" + getUtility() +
            ", utilitySlots=" + getUtilitySlots() +
            "}";
    }
}
