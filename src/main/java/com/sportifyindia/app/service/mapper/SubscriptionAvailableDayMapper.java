package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.SubscriptionAvailableDay;
import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.service.dto.SubscriptionAvailableDayDTO;
import com.sportifyindia.app.service.dto.TimeSlotsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SubscriptionAvailableDay} and its DTO {@link SubscriptionAvailableDayDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubscriptionAvailableDayMapper extends EntityMapper<SubscriptionAvailableDayDTO, SubscriptionAvailableDay> {
    @Mapping(target = "timeSlots", source = "timeSlots", qualifiedByName = "timeSlotsId")
    SubscriptionAvailableDayDTO toDto(SubscriptionAvailableDay s);

    @Named("timeSlotsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TimeSlotsDTO toDtoTimeSlotsId(TimeSlots timeSlots);
}
