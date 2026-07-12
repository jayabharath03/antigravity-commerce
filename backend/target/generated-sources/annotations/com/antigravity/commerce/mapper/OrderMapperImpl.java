package com.antigravity.commerce.mapper;

import com.antigravity.commerce.dto.OrderDto;
import com.antigravity.commerce.dto.OrderItemDto;
import com.antigravity.commerce.entity.Order;
import com.antigravity.commerce.entity.OrderItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-12T21:30:41+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.10 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public OrderDto toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto orderDto = new OrderDto();

        orderDto.setId( order.getId() );
        orderDto.setOrderNumber( order.getOrderNumber() );
        orderDto.setStatus( order.getStatus() );
        orderDto.setSubTotal( order.getSubTotal() );
        orderDto.setTaxTotal( order.getTaxTotal() );
        orderDto.setShippingTotal( order.getShippingTotal() );
        orderDto.setGrandTotal( order.getGrandTotal() );
        orderDto.setShippingAddress( order.getShippingAddress() );
        orderDto.setPaymentStatus( order.getPaymentStatus() );
        orderDto.setItems( orderItemListToOrderItemDtoList( order.getItems() ) );
        orderDto.setCreatedAt( order.getCreatedAt() );

        return orderDto;
    }

    @Override
    public OrderItemDto toDto(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemDto orderItemDto = new OrderItemDto();

        orderItemDto.setId( orderItem.getId() );
        orderItemDto.setProduct( productMapper.toDto( orderItem.getProduct() ) );
        orderItemDto.setProductName( orderItem.getProductName() );
        orderItemDto.setPriceAtPurchase( orderItem.getPriceAtPurchase() );
        orderItemDto.setQuantity( orderItem.getQuantity() );

        return orderItemDto;
    }

    protected List<OrderItemDto> orderItemListToOrderItemDtoList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemDto> list1 = new ArrayList<OrderItemDto>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toDto( orderItem ) );
        }

        return list1;
    }
}
