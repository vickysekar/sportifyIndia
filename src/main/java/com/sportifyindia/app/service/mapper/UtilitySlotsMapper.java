package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.domain.Utility;
import com.sportifyindia.app.domain.UtilitySlots;
import com.sportifyindia.app.service.dto.TimeSlotsDTO;
import com.sportifyindia.app.service.dto.UtilityDTO;
import com.sportifyindia.app.service.dto.UtilitySlotsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UtilitySlots} and its DTO {@link UtilitySlotsDTO}.
 */
@Mapper(componentModel = "spring")
public interface UtilitySlotsMapper extends EntityMapper<UtilitySlotsDTO, UtilitySlots> {
    @Mapping(target = "utility", source = "utility", qualifiedByName = "utilityId")
    @Mapping(target = "timeSlots", source = "timeSlots", qualifiedByName = "timeSlotsId")
    UtilitySlotsDTO toDto(UtilitySlots s);

    @Named("utilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UtilityDTO toDtoUtilityId(Utility utility);

    @Named("timeSlotsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TimeSlotsDTO toDtoTimeSlotsId(TimeSlots timeSlots);
}
