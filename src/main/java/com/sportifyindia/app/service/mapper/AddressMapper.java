package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Address;
import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.service.dto.AddressDTO;
import com.sportifyindia.app.service.dto.FacilityDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring")
public interface AddressMapper extends EntityMapper<AddressDTO, Address> {
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityId")
    AddressDTO toDto(Address s);

    @Named("facilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityDTO toDtoFacilityId(Facility facility);
}
