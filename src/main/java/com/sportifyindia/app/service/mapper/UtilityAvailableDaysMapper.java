package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.domain.UtilityAvailableDays;
import com.sportifyindia.app.service.dto.TimeSlotsDTO;
import com.sportifyindia.app.service.dto.UtilityAvailableDaysDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UtilityAvailableDays} and its DTO {@link UtilityAvailableDaysDTO}.
 */
@Mapper(componentModel = "spring")
public interface UtilityAvailableDaysMapper extends EntityMapper<UtilityAvailableDaysDTO, UtilityAvailableDays> {
    @Mapping(target = "timeSlots", source = "timeSlots", qualifiedByName = "timeSlotsId")
    UtilityAvailableDaysDTO toDto(UtilityAvailableDays s);

    @Named("timeSlotsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TimeSlotsDTO toDtoTimeSlotsId(TimeSlots timeSlots);
}
