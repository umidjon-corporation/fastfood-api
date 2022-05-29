package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.OrderDto;
import com.project.fastfoodapi.dto.front.DeliveryFrontDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.dto.front.OrderFrontDto;
import com.project.fastfoodapi.entity.*;
import com.project.fastfoodapi.entity.enums.ClientStatus;
import com.project.fastfoodapi.entity.enums.OrderStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.DeliveryMapper;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.mapper.OrderMapper;
import com.project.fastfoodapi.repository.FilialRepository;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.repository.OrderRepository;
import com.project.fastfoodapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.source.spi.Sortable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    final OrderRepository orderRepository;
    final OrderMapper orderMapper;
    final ProductRepository productRepository;
    final HumanRepository humanRepository;
    final FilialRepository filialRepository;
    final HumanMapper humanMapper;
    final DeliveryMapper deliveryMapper;

    public ApiResponse<OrderFrontDto> add(OrderDto dto) {
        if (dto.getProducts().isEmpty()) {
            return ApiResponse.<OrderFrontDto>builder()
                    .message("Not any product in bucket")
                    .build();
        }
        Order order = orderMapper.orderDtoToOrder(dto);
        double all = 0;
        for (OrderProduct orderProduct : order.getProducts()) {
            Optional<Product> optionalProduct = productRepository.findById(orderProduct.getProduct().getId());
            if (optionalProduct.isEmpty()) {
                continue;
            }
            orderProduct.setProduct(optionalProduct.get());
            BigDecimal price = optionalProduct.get().getPrice().multiply(BigDecimal.valueOf(orderProduct.getCount()));
            orderProduct.setAmount(price);
            orderProduct.setPrice(optionalProduct.get().getPrice());
            all += price.doubleValue();
        }
        order.getProducts().removeIf(orderProduct -> orderProduct.getAmount()==null);
        order.setAmount(BigDecimal.valueOf(all));
        Optional<Human> optionalClient = humanRepository.findByStatusIsNotAndId(ClientStatus.DELETED, dto.getClientId());
        optionalClient.ifPresent(order::setClient);
        Optional<Filial> optionalFilial = filialRepository.findByIdAndActiveTrue(dto.getFilialId());
        optionalFilial.ifPresent(order::setFilial);
        //TODO calc delivery price
        order.getDelivery().setPrice(BigDecimal.ZERO);
        Order save = orderRepository.save(order);
        //TODO choose operator & send notification to operator
        return ApiResponse.<OrderFrontDto>builder()
                .data(orderMapper.orderToOrderFrontDto(save))
                .success(true)
                .message("Order created!")
                .build();
    }

    public ApiResponse<Object> changeStatus(Long id, OrderStatus status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return ApiResponse.builder()
                    .message("Order with id=(" + id + ") not found")
                    .build();
        }
        Order order = optionalOrder.get();
        order.setOrderStatus(status);
        orderRepository.save(order);
        return ApiResponse.builder()
                .success(true)
                .message("Order status changed to " + status)
                .build();
    }

    public ApiResponse<HumanFrontDto> changeOperator(Long id, Long operatorId) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Order with id=(" + id + ") not found")
                    .build();
        }
        Optional<Human> optionalHuman = humanRepository.findById(operatorId);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.OPERATOR) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Operator with id=(" + operatorId + ") not found")
                    .build();
        }
        optionalOrder.get().setOperator(optionalHuman.get());
        orderRepository.save(optionalOrder.get());
        return ApiResponse.<HumanFrontDto>builder()
                .data(humanMapper.humanToHumanFrontDto(optionalHuman.get()))
                .success(true)
                .message("Operator changed")
                .build();
    }

    public ApiResponse<HumanFrontDto> changeCourier(Long id, Long courierId) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Order with id=(" + id + ") not found")
                    .build();
        }
        Optional<Human> optionalHuman = humanRepository.findById(courierId);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.COURIER) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Courier with id=(" + courierId + ") not found")
                    .build();
        }
        Order order = optionalOrder.get();
        if (order.getDelivery() == null) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Order hasn't got delivery")
                    .build();
        }
        order.getDelivery().setCourier(optionalHuman.get());
        orderRepository.save(optionalOrder.get());
        return ApiResponse.<HumanFrontDto>builder()
                .data(humanMapper.humanToHumanFrontDto(optionalHuman.get()))
                .success(true)
                .message("Courier changed")
                .build();
    }

    public ApiResponse<DeliveryFrontDto> getDelivery(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return ApiResponse.<DeliveryFrontDto>builder()
                    .message("Order with id=(" + id + ") not found")
                    .build();
        }
        if (optionalOrder.get().getDelivery() == null) {
            return ApiResponse.<DeliveryFrontDto>builder()
                    .message("Order hasn't got delivery")
                    .build();
        }
        return ApiResponse.<DeliveryFrontDto>builder()
                .success(true)
                .data(deliveryMapper.toDto(optionalOrder.get().getDelivery()))
                .message("Founded!")
                .build();
    }

    public List<OrderFrontDto> getAll(String status, Long filial, Boolean delivery, Integer size, Integer page ,boolean desc) {
        List<Order> all;
        OrderStatus orderStatus = null;
        Pageable pageable = Pageable.ofSize(size);
        pageable.withPage(page);
        if(desc){
            pageable.getSortOr(Sort.by(Sort.Direction.DESC, "time"));
        }

        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ignore) {}
        if (orderStatus == null && filial == null && delivery == null) {
            return orderMapper.orderToOrderFrontDto(orderRepository.findAll());
        }
        if (delivery == null) {
            if (filial == null) {
                all = orderRepository.findByOrderStatus(orderStatus, pageable);
            } else {
                all = orderRepository.findByOrderStatusAndFilial_Id(orderStatus, filial, pageable);
            }
        } else {
            if (delivery) {
                if (filial == null) {
                    all = orderRepository.findByOrderStatusAndDelivery_Courier_idIsNotNull(orderStatus, pageable);
                } else {
                    all = orderRepository.findByOrderStatusAndFilial_IdAndDelivery_Courier_IdIsNotNull(orderStatus, filial, pageable);
                }
            } else {
                if (filial == null) {
                    all = orderRepository.findByOrderStatusAndDelivery_Courier_idIsNull(orderStatus, pageable);
                } else {
                    all = orderRepository.findByOrderStatusAndFilial_IdAndDelivery_Courier_IdIsNull(orderStatus, filial, pageable);
                }
            }

        }

        return orderMapper.orderToOrderFrontDto(all);
    }
}
