package com.sportifyindia.app.service.dto;

import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class BookingRequestDTO implements Serializable {

    @NotNull
    private Long utilityId;

    @NotNull
    private Long slotId;

    @NotNull
    @Min(1)
    private Integer quantity;

    public Long getUtilityId() {
        return utilityId;
    }

    public void setUtilityId(Long utilityId) {
        this.utilityId = utilityId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
