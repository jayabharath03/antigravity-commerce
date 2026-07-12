package com.antigravity.commerce.mapper;

import com.antigravity.commerce.dto.CartDto;
import com.antigravity.commerce.dto.CartItemDto;
import com.antigravity.commerce.entity.Cart;
import com.antigravity.commerce.entity.CartItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-12T21:30:40+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public CartDto toDto(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartDto cartDto = new CartDto();

        cartDto.setId( cart.getId() );
        cartDto.setSessionId( cart.getSessionId() );
        cartDto.setItems( cartItemListToCartItemDtoList( cart.getItems() ) );
        cartDto.setCreatedAt( cart.getCreatedAt() );
        cartDto.setUpdatedAt( cart.getUpdatedAt() );

        return cartDto;
    }

    @Override
    public CartItemDto toDto(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemDto cartItemDto = new CartItemDto();

        cartItemDto.setId( cartItem.getId() );
        cartItemDto.setProduct( productMapper.toDto( cartItem.getProduct() ) );
        cartItemDto.setQuantity( cartItem.getQuantity() );

        return cartItemDto;
    }

    protected List<CartItemDto> cartItemListToCartItemDtoList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemDto> list1 = new ArrayList<CartItemDto>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( toDto( cartItem ) );
        }

        return list1;
    }
}
