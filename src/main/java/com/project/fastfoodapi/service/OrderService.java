package com.project.fastfoodapi.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.OrderDto;
import com.project.fastfoodapi.dto.front.DeliveryFrontDto;
import com.project.fastfoodapi.dto.front.GroupedDataDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
        order.getProducts().removeIf(orderProduct -> orderProduct.getAmount() == null);
        order.setAmount(BigDecimal.valueOf(all));
        Optional<Human> optionalClient = humanRepository.findByStatusIsNotAndId(ClientStatus.DELETED, dto.getClientId());
        optionalClient.ifPresent(order::setClient);
        Optional<Filial> optionalFilial = filialRepository.findByIdAndActiveTrue(dto.getFilialId());
        optionalFilial.ifPresent(order::setFilial);
        //TODO calc delivery price
        order.getDelivery().setPrice(BigDecimal.ZERO);
        Order save = orderRepository.save(order);
        //TODO choose operator
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
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.OPERATOR
                || optionalHuman.get().getUserType() != UserType.ADMIN) {
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

    public List<OrderFrontDto> getAll(String status, Long filial, Boolean delivery, Integer size, Integer page, boolean desc) {
        List<Order> all;
        OrderStatus orderStatus = null;
        Pageable pageable=getPageable(page, size, desc?Sort.Direction.DESC: Sort.Direction.ASC, "time", "id");
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ignore) {
        }
        if (orderStatus == null && filial == null && delivery == null) {
            return orderMapper.orderToOrderFrontDto(orderRepository.findAll(pageable).getContent());
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

    public List<OrderFrontDto> getAllToday(String status, Long filial, Boolean delivery, Integer size, Integer page, boolean desc) {
        List<Order> all;
        OrderStatus orderStatus = null;
        Pageable pageable=getPageable(page, size, desc?Sort.Direction.DESC: Sort.Direction.ASC, "time", "id");
        LocalDateTime from=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime to=LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ignore) {
        }
        if (orderStatus == null && filial == null && delivery == null) {
            return orderMapper.orderToOrderFrontDto(orderRepository.findByTimeIsBetween(from ,to, pageable));
        }
        if (delivery == null) {
            if (filial == null) {
                all = orderRepository.findByOrderStatusAndTimeIsBetween(orderStatus, from, to, pageable);
            } else {
                all = orderRepository.findByOrderStatusAndFilial_IdAndTimeIsBetween(orderStatus, filial, from, to, pageable);
            }
        } else {
            if (delivery) {
                if (filial == null) {
                    all = orderRepository.findByOrderStatusAndDelivery_Courier_idIsNotNullAndTimeIsBetween(orderStatus, from, to, pageable);
                } else {
                    all = orderRepository.findByOrderStatusAndFilial_IdAndDelivery_Courier_IdIsNotNullAndTimeIsBetween(orderStatus, filial, from, to, pageable);
                }
            } else {
                if (filial == null) {
                    all = orderRepository.findByOrderStatusAndDelivery_Courier_idIsNullAndTimeIsBetween(orderStatus, from, to, pageable);
                } else {
                    all = orderRepository.findByOrderStatusAndFilial_IdAndDelivery_Courier_IdIsNullAndTimeIsBetween(orderStatus, filial, from, to, pageable);
                }
            }

        }
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        System.out.println(all);
        return orderMapper.orderToOrderFrontDto(all);
    }

    public Pageable getPageable(int page, int size, Sort.Direction sort, String... properties){
        return PageRequest.of(page, size, Sort.by(sort, properties));
    }

    public List<GroupedDataDto<OrderStatus, OrderFrontDto>> getAllAndGroupByStatus(Integer size, Integer page, boolean desc) {
        List<GroupedDataDto<OrderStatus, OrderFrontDto>> result=new ArrayList<>();
        Pageable pageable = getPageable(page, size, desc ? Sort.Direction.DESC : Sort.Direction.ASC, "time", "id");
        for (OrderStatus value : OrderStatus.values()) {
            GroupedDataDto<OrderStatus, OrderFrontDto> data=new GroupedDataDto<>();
            data.setTitle(value.getTitle());
            data.setGroupedBy(value);
            List<Order> order = orderRepository.findByOrderStatus(value, pageable);
            if(order.isEmpty()){
                data.setContent(new ArrayList<>());
            }else{
                data.setContent(orderMapper.orderToOrderFrontDto(order));
            }
            result.add(data);
        }
        return result;
    }

    public List<GroupedDataDto<OrderStatus, OrderFrontDto>> getTodayAllAndGroupByStatus(Integer size, Integer page, boolean desc) {
        List<GroupedDataDto<OrderStatus, OrderFrontDto>> result=new ArrayList<>();
        Pageable pageable = getPageable(page, size, desc ? Sort.Direction.DESC : Sort.Direction.ASC, "time", "id");
        LocalDateTime from=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime to=LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        for (OrderStatus value : OrderStatus.values()) {
            GroupedDataDto<OrderStatus, OrderFrontDto> data=new GroupedDataDto<>();
            data.setTitle(value.getTitle());
            data.setGroupedBy(value);
            List<Order> order = orderRepository.findByOrderStatusAndTimeIsBetween(value, from, to, pageable);
            if(order.isEmpty()){
                data.setContent(new ArrayList<>());
            }else{
                data.setContent(orderMapper.orderToOrderFrontDto(order));
            }
            result.add(data);
        }
        return result;
    }
}
