package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.Utility;
import com.sportifyindia.app.domain.UtilityAvailableDays;
import com.sportifyindia.app.service.dto.FacilityDTO;
import com.sportifyindia.app.service.dto.UtilityAvailableDaysDTO;
import com.sportifyindia.app.service.dto.UtilityDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Utility} and its DTO {@link UtilityDTO}.
 */
@Mapper(componentModel = "spring")
public interface UtilityMapper extends EntityMapper<UtilityDTO, Utility> {
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityId")
    @Mapping(target = "utilityAvailableDays", source = "utilityAvailableDays", qualifiedByName = "utilityAvailableDaysIdSet")
    UtilityDTO toDto(Utility s);

    @Mapping(target = "removeUtilityAvailableDays", ignore = true)
    Utility toEntity(UtilityDTO utilityDTO);

    @Named("facilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityDTO toDtoFacilityId(Facility facility);

    @Named("utilityAvailableDaysId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UtilityAvailableDaysDTO toDtoUtilityAvailableDaysId(UtilityAvailableDays utilityAvailableDays);

    @Named("utilityAvailableDaysIdSet")
    default Set<UtilityAvailableDaysDTO> toDtoUtilityAvailableDaysIdSet(Set<UtilityAvailableDays> utilityAvailableDays) {
        return utilityAvailableDays.stream().map(this::toDtoUtilityAvailableDaysId).collect(Collectors.toSet());
    }
}
