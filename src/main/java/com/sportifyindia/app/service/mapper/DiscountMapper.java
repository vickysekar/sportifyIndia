package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Discount;
import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.service.dto.DiscountDTO;
import com.sportifyindia.app.service.dto.FacilityDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Discount} and its DTO {@link DiscountDTO}.
 */
@Mapper(componentModel = "spring")
public interface DiscountMapper extends EntityMapper<DiscountDTO, Discount> {
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityId")
    DiscountDTO toDto(Discount s);

    @Named("facilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityDTO toDtoFacilityId(Facility facility);
}
