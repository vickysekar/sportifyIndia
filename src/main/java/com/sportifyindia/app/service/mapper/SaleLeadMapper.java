package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.FacilityEmployee;
import com.sportifyindia.app.domain.SaleLead;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.service.dto.FacilityDTO;
import com.sportifyindia.app.service.dto.FacilityEmployeeDTO;
import com.sportifyindia.app.service.dto.SaleLeadDTO;
import com.sportifyindia.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SaleLead} and its DTO {@link SaleLeadDTO}.
 */
@Mapper(componentModel = "spring")
public interface SaleLeadMapper extends EntityMapper<SaleLeadDTO, SaleLead> {
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityId")
    @Mapping(target = "facilityEmployee", source = "facilityEmployee", qualifiedByName = "facilityEmployeeId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    SaleLeadDTO toDto(SaleLead s);

    @Named("facilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityDTO toDtoFacilityId(Facility facility);

    @Named("facilityEmployeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityEmployeeDTO toDtoFacilityEmployeeId(FacilityEmployee facilityEmployee);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
