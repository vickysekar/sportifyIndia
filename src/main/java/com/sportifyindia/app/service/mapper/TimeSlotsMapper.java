package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.service.dto.TimeSlotsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TimeSlots} and its DTO {@link TimeSlotsDTO}.
 */
@Mapper(componentModel = "spring")
public interface TimeSlotsMapper extends EntityMapper<TimeSlotsDTO, TimeSlots> {
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    TimeSlotsDTO toDto(TimeSlots s);

    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    TimeSlots toEntity(TimeSlotsDTO s);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    void partialUpdate(@MappingTarget TimeSlots entity, TimeSlotsDTO dto);
}
