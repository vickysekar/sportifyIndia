package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.OneTimeEvent;
import com.sportifyindia.app.service.dto.FacilityDTO;
import com.sportifyindia.app.service.dto.OneTimeEventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OneTimeEvent} and its DTO {@link OneTimeEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface OneTimeEventMapper extends EntityMapper<OneTimeEventDTO, OneTimeEvent> {
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityId")
    OneTimeEventDTO toDto(OneTimeEvent s);

    @Named("facilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityDTO toDtoFacilityId(Facility facility);
}
