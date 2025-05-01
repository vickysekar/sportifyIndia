package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Charge;
import com.sportifyindia.app.domain.Order;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.service.dto.ChargeDTO;
import com.sportifyindia.app.service.dto.OrderDTO;
import com.sportifyindia.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Charge} and its DTO {@link ChargeDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChargeMapper extends EntityMapper<ChargeDTO, Charge> {
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    ChargeDTO toDto(Charge s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
