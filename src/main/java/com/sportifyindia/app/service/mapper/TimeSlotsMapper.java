package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.service.dto.TimeSlotsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TimeSlots} and its DTO {@link TimeSlotsDTO}.
 */
@Mapper(componentModel = "spring")
public interface TimeSlotsMapper extends EntityMapper<TimeSlotsDTO, TimeSlots> {}
