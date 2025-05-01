package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.FacilityEmployee;
import com.sportifyindia.app.service.dto.FacilityDTO;
import com.sportifyindia.app.service.dto.FacilityEmployeeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FacilityEmployee} and its DTO {@link FacilityEmployeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface FacilityEmployeeMapper extends EntityMapper<FacilityEmployeeDTO, FacilityEmployee> {
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityId")
    FacilityEmployeeDTO toDto(FacilityEmployee s);

    @Named("facilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityDTO toDtoFacilityId(Facility facility);
}
