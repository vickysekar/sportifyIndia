package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Utility;
import com.sportifyindia.app.domain.UtilityExceptionDays;
import com.sportifyindia.app.service.dto.UtilityDTO;
import com.sportifyindia.app.service.dto.UtilityExceptionDaysDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UtilityExceptionDays} and its DTO {@link UtilityExceptionDaysDTO}.
 */
@Mapper(componentModel = "spring")
public interface UtilityExceptionDaysMapper extends EntityMapper<UtilityExceptionDaysDTO, UtilityExceptionDays> {
    @Mapping(target = "utility", source = "utility", qualifiedByName = "utilityId")
    UtilityExceptionDaysDTO toDto(UtilityExceptionDays s);

    @Named("utilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UtilityDTO toDtoUtilityId(Utility utility);
}
