package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Course;
import com.sportifyindia.app.service.dto.CourseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Course} and its DTO {@link CourseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseMapper extends EntityMapper<CourseDTO, Course> {
    @Mapping(target = "facilityId", source = "facility.id")
    CourseDTO toDto(Course s);

    @Mapping(target = "facility.id", source = "facilityId")
    Course toEntity(CourseDTO s);

    @Named("toDtoFacilityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoFacilityId(Course course);
}
