package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.FacilityEmployee;
import com.sportifyindia.app.domain.SaleLead;
import com.sportifyindia.app.domain.Task;
import com.sportifyindia.app.service.dto.FacilityEmployeeDTO;
import com.sportifyindia.app.service.dto.SaleLeadDTO;
import com.sportifyindia.app.service.dto.TaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "saleLead", source = "saleLead", qualifiedByName = "saleLeadId")
    @Mapping(target = "facilityEmployee", source = "facilityEmployee", qualifiedByName = "facilityEmployeeId")
    TaskDTO toDto(Task s);

    @Named("saleLeadId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SaleLeadDTO toDtoSaleLeadId(SaleLead saleLead);

    @Named("facilityEmployeeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityEmployeeDTO toDtoFacilityEmployeeId(FacilityEmployee facilityEmployee);
}
