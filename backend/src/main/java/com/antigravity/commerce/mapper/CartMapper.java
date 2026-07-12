package com.antigravity.commerce.mapper;

import com.antigravity.commerce.dto.CartDto;
import com.antigravity.commerce.dto.CartItemDto;
import com.antigravity.commerce.entity.Cart;
import com.antigravity.commerce.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class}, builder = @org.mapstruct.Builder(disableBuilder = true))
public interface CartMapper {
    
    CartDto toDto(Cart cart);
    CartItemDto toDto(CartItem cartItem);
}
