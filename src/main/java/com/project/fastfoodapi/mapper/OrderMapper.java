package com.project.fastfoodapi.mapper;

import com.project.fastfoodapi.dto.OrderDto;
import com.project.fastfoodapi.dto.front.OrderFrontDto;
import com.project.fastfoodapi.dto.OrderProductDto;
import com.project.fastfoodapi.dto.front.OrderProductFrontDto;
import com.project.fastfoodapi.entity.Order;
import com.project.fastfoodapi.entity.OrderProduct;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {
        DeliveryMapper.class, FilialMapper.class, HumanMapper.class, ProductMapper.class
})
public interface OrderMapper {
    @Mapping(source = "productId", target = "product.id")
    OrderProduct orderProductDtoToOrderProduct(OrderProductDto dto);
    OrderProductFrontDto orderProductToOrderProductFrontDto(OrderProduct orderProduct);

    @Mapping(source = "filialId", target = "filial.id")
    @Mapping(source = "clientId", target = "client.id")
    Order orderDtoToOrder(OrderDto orderDto);

    @InheritInverseConfiguration(name = "orderDtoToOrder")
    OrderDto orderToOrderDto(Order order);

    @InheritConfiguration(name = "orderDtoToOrder")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderFromOrderDto(OrderDto orderDto, @MappingTarget Order order);


    OrderFrontDto orderToOrderFrontDto(Order order);
    List<OrderFrontDto> orderToOrderFrontDto(List<Order> order);
}
