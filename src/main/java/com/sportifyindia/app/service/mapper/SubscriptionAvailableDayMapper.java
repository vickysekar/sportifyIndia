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
    @Mapping(target = "startTime", source = "timeSlots.startTime")
    @Mapping(target = "endTime", source = "timeSlots.endTime")
    SubscriptionAvailableDayDTO toDto(SubscriptionAvailableDay s);

    @Mapping(target = "timeSlots", ignore = true)
    SubscriptionAvailableDay toEntity(SubscriptionAvailableDayDTO s);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "timeSlots", ignore = true)
    void partialUpdate(@MappingTarget SubscriptionAvailableDay entity, SubscriptionAvailableDayDTO dto);
}
