package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.LeadActivity;
import com.sportifyindia.app.domain.SaleLead;
import com.sportifyindia.app.service.dto.LeadActivityDTO;
import com.sportifyindia.app.service.dto.SaleLeadDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LeadActivity} and its DTO {@link LeadActivityDTO}.
 */
@Mapper(componentModel = "spring")
public interface LeadActivityMapper extends EntityMapper<LeadActivityDTO, LeadActivity> {
    @Mapping(target = "saleLead", source = "saleLead", qualifiedByName = "saleLeadId")
    LeadActivityDTO toDto(LeadActivity s);

    @Named("saleLeadId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleLeadDTO toDtoSaleLeadId(SaleLead saleLead);
}
