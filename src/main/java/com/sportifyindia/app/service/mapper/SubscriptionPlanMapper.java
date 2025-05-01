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
@Mapper(componentModel = "spring")
public interface SubscriptionPlanMapper extends EntityMapper<SubscriptionPlanDTO, SubscriptionPlan> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    @Mapping(target = "subscriptionAvailableDays", source = "subscriptionAvailableDays", qualifiedByName = "subscriptionAvailableDayIdSet")
    SubscriptionPlanDTO toDto(SubscriptionPlan s);

    @Mapping(target = "removeSubscriptionAvailableDay", ignore = true)
    SubscriptionPlan toEntity(SubscriptionPlanDTO subscriptionPlanDTO);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);

    @Named("subscriptionAvailableDayId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SubscriptionAvailableDayDTO toDtoSubscriptionAvailableDayId(SubscriptionAvailableDay subscriptionAvailableDay);

    @Named("subscriptionAvailableDayIdSet")
    default Set<SubscriptionAvailableDayDTO> toDtoSubscriptionAvailableDayIdSet(Set<SubscriptionAvailableDay> subscriptionAvailableDay) {
        return subscriptionAvailableDay.stream().map(this::toDtoSubscriptionAvailableDayId).collect(Collectors.toSet());
    }
}
