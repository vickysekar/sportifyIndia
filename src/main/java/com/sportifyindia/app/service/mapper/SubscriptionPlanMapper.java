package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Course;
import com.sportifyindia.app.domain.SubscriptionAvailableDay;
import com.sportifyindia.app.domain.SubscriptionPlan;
import com.sportifyindia.app.service.dto.CourseDTO;
import com.sportifyindia.app.service.dto.SubscriptionAvailableDayDTO;
import com.sportifyindia.app.service.dto.SubscriptionPlanDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SubscriptionPlan} and its DTO {@link SubscriptionPlanDTO}.
 */
@Mapper(componentModel = "spring", uses = { CourseMapper.class, SubscriptionAvailableDayMapper.class })
public interface SubscriptionPlanMapper extends EntityMapper<SubscriptionPlanDTO, SubscriptionPlan> {
    @Mapping(target = "course", source = "course", qualifiedByName = "id")
    @Mapping(target = "subscriptionAvailableDays", ignore = true)
    SubscriptionPlanDTO toDto(SubscriptionPlan s);

    @Mapping(target = "course", source = "course", qualifiedByName = "id")
    @Mapping(target = "subscriptionAvailableDays", ignore = true)
    SubscriptionPlan toEntity(SubscriptionPlanDTO s);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "subscriptionAvailableDays", ignore = true)
    void partialUpdate(@MappingTarget SubscriptionPlan entity, SubscriptionPlanDTO dto);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);

    @Named("withAvailableDays")
    @Mapping(target = "subscriptionAvailableDays", source = "subscriptionAvailableDays")
    SubscriptionPlanDTO toDtoWithAvailableDays(SubscriptionPlan s);

    @Named("withAvailableDays")
    @Mapping(target = "subscriptionAvailableDays", source = "subscriptionAvailableDays")
    SubscriptionPlan toEntityWithAvailableDays(SubscriptionPlanDTO s);

    @Named("subscriptionAvailableDayId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "daysOfWeek", source = "daysOfWeek")
    @Mapping(target = "startTime", source = "timeSlots.startTime")
    @Mapping(target = "endTime", source = "timeSlots.endTime")
    SubscriptionAvailableDayDTO toDtoSubscriptionAvailableDayId(SubscriptionAvailableDay subscriptionAvailableDay);

    @Named("subscriptionAvailableDayIdSet")
    default Set<SubscriptionAvailableDayDTO> toDtoSubscriptionAvailableDayIdSet(Set<SubscriptionAvailableDay> subscriptionAvailableDay) {
        return subscriptionAvailableDay.stream().map(this::toDtoSubscriptionAvailableDayId).collect(Collectors.toSet());
    }
}
