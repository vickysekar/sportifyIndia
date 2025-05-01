package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Charge;
import com.sportifyindia.app.domain.Tax;
import com.sportifyindia.app.service.dto.ChargeDTO;
import com.sportifyindia.app.service.dto.TaxDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tax} and its DTO {@link TaxDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaxMapper extends EntityMapper<TaxDTO, Tax> {
    @Mapping(target = "charge", source = "charge", qualifiedByName = "chargeId")
    TaxDTO toDto(Tax s);

    @Named("chargeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ChargeDTO toDtoChargeId(Charge charge);
}
