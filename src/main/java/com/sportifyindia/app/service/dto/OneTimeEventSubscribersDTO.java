package com.sportifyindia.app.service.dto;

import com.sportifyindia.app.domain.enumeration.SubscriptionStatusEnum;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.sportifyindia.app.domain.OneTimeEventSubscribers} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OneTimeEventSubscribersDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal paidAmount;

    @NotNull
    private SubscriptionStatusEnum status;

    private OneTimeEventDTO oneTimeEvent;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public SubscriptionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatusEnum status) {
        this.status = status;
    }

    public OneTimeEventDTO getOneTimeEvent() {
        return oneTimeEvent;
    }

    public void setOneTimeEvent(OneTimeEventDTO oneTimeEvent) {
        this.oneTimeEvent = oneTimeEvent;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OneTimeEventSubscribersDTO)) {
            return false;
        }

        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO = (OneTimeEventSubscribersDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, oneTimeEventSubscribersDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OneTimeEventSubscribersDTO{" +
            "id=" + getId() +
            ", paidAmount=" + getPaidAmount() +
            ", status='" + getStatus() + "'" +
            ", oneTimeEvent=" + getOneTimeEvent() +
            ", user=" + getUser() +
            "}";
    }
}
