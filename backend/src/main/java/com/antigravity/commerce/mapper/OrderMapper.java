package com.antigravity.commerce.mapper;

import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.dto.OrderItemDto;
import com.antigravity.commerce.entity.Order;
import com.antigravity.commerce.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductMapper.class}, builder = @org.mapstruct.Builder(disableBuilder = true))
public interface OrderMapper {
    OrderDto toDto(Order order);
    OrderItemDto toDto(OrderItem orderItem);
}
