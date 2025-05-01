package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.service.dto.FacilityDTO;
import com.sportifyindia.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Facility} and its DTO {@link FacilityDTO}.
 */
@Mapper(componentModel = "spring")
public interface FacilityMapper extends EntityMapper<FacilityDTO, Facility> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    FacilityDTO toDto(Facility s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
