package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Course;
import com.sportifyindia.app.domain.CourseSubscription;
import com.sportifyindia.app.domain.SubscriptionPlan;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.service.dto.CourseDTO;
import com.sportifyindia.app.service.dto.CourseSubscriptionDTO;
import com.sportifyindia.app.service.dto.SubscriptionPlanDTO;
import com.sportifyindia.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CourseSubscription} and its DTO {@link CourseSubscriptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface CourseSubscriptionMapper extends EntityMapper<CourseSubscriptionDTO, CourseSubscription> {
    @Mapping(target = "course", source = "course", qualifiedByName = "courseId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "subscriptionPlan", source = "subscriptionPlan", qualifiedByName = "subscriptionPlanId")
    CourseSubscriptionDTO toDto(CourseSubscription s);

    @Named("courseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseDTO toDtoCourseId(Course course);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("subscriptionPlanId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SubscriptionPlanDTO toDtoSubscriptionPlanId(SubscriptionPlan subscriptionPlan);
}
