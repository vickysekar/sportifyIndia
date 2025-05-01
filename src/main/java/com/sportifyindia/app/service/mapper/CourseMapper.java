package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Course;
import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.service.dto.CourseDTO;
import com.sportifyindia.app.service.dto.FacilityDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Course} and its DTO {@link CourseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper extends EntityMapper<CourseDTO, Course> {
    @Mapping(target = "facility", source = "facility", qualifiedByName = "facilityId")
    CourseDTO toDto(Course s);

    @Named("facilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FacilityDTO toDtoFacilityId(Facility facility);
}
