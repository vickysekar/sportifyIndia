package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.TaxMaster;
import com.sportifyindia.app.service.dto.FacilityDTO;
import com.sportifyindia.app.service.dto.TaxMasterDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaxMaster} and its DTO {@link TaxMasterDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaxMasterMapper extends EntityMapper<TaxMasterDTO, TaxMaster> {
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityId")
    TaxMasterDTO toDto(TaxMaster s);

    @Named("facilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityDTO toDtoFacilityId(Facility facility);
}
