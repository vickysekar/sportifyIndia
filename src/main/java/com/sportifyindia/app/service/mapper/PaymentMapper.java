package com.sportifyindia.app.service.mapper;

import com.sportifyindia.app.domain.Order;
import com.sportifyindia.app.domain.Payment;
import com.sportifyindia.app.domain.User;
import com.sportifyindia.app.service.dto.OrderDTO;
import com.sportifyindia.app.service.dto.PaymentDTO;
import com.sportifyindia.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    PaymentDTO toDto(Payment s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
