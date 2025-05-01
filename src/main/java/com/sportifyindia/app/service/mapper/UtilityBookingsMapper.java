package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Utility;
import com.sportifyindia.app.domain.UtilityBookings;
import com.sportifyindia.app.domain.UtilitySlots;
import com.sportifyindia.app.service.dto.UtilityBookingsDTO;
import com.sportifyindia.app.service.dto.UtilityDTO;
import com.sportifyindia.app.service.dto.UtilitySlotsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UtilityBookings} and its DTO {@link UtilityBookingsDTO}.
 */
@Mapper(componentModel = "spring")
public interface UtilityBookingsMapper extends EntityMapper<UtilityBookingsDTO, UtilityBookings> {
    @Mapping(target = "utility", source = "utility", qualifiedByName = "utilityId")
    @Mapping(target = "utilitySlots", source = "utilitySlots", qualifiedByName = "utilitySlotsId")
    UtilityBookingsDTO toDto(UtilityBookings s);

    @Named("utilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UtilityDTO toDtoUtilityId(Utility utility);

    @Named("utilitySlotsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UtilitySlotsDTO toDtoUtilitySlotsId(UtilitySlots utilitySlots);
}
