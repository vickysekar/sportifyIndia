package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.OneTimeEvent;
import com.sportifyindia.app.domain.OneTimeEventSubscribers;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.service.dto.OneTimeEventDTO;
import com.sportifyindia.app.service.dto.OneTimeEventSubscribersDTO;
import com.sportifyindia.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OneTimeEventSubscribers} and its DTO {@link OneTimeEventSubscribersDTO}.
 */
@Mapper(componentModel = "spring")
public interface OneTimeEventSubscribersMapper extends EntityMapper<OneTimeEventSubscribersDTO, OneTimeEventSubscribers> {
    @Mapping(target = "oneTimeEvent", source = "oneTimeEvent", qualifiedByName = "oneTimeEventId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    OneTimeEventSubscribersDTO toDto(OneTimeEventSubscribers s);

    @Named("oneTimeEventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OneTimeEventDTO toDtoOneTimeEventId(OneTimeEvent oneTimeEvent);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
