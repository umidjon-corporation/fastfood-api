package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.OrderDto;
import com.project.fastfoodapi.dto.PageableResponse;
import com.project.fastfoodapi.dto.front.*;
import com.project.fastfoodapi.entity.*;
import com.project.fastfoodapi.entity.enums.HumanStatus;
import com.project.fastfoodapi.entity.enums.OrderStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.DeliveryMapper;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.mapper.OrderMapper;
import com.project.fastfoodapi.repository.BranchRepository;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.repository.OrderRepository;
import com.project.fastfoodapi.repository.ProductRepository;
import com.project.fastfoodapi.specification.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    final BranchRepository branchRepository;
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
            if (optionalProduct.isEmpty() || orderProduct.getCount()==null) {
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
        Optional<Human> optionalClient = humanRepository.findByStatusIsNotAndId(HumanStatus.DELETED, dto.getClientId());
        optionalClient.ifPresent(order::setClient);
        Optional<Branch> optionalBranch = branchRepository.findByIdAndActiveTrue(dto.getBranchId());
        optionalBranch.ifPresent(order::setBranch);
        //TODO calc delivery price
        if(order.getDelivery()!=null){
            order.getDelivery().setPrice(BigDecimal.ZERO);
        }
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

    public PageableResponse<OrderFrontDto> getAll(String status, Long branch, Boolean delivery, int size, int page, boolean desc, String[] sort){
        SearchRequest.SearchRequestBuilder searchRequest = SearchRequest.builder();
        List<FilterRequest> filterRequests=new ArrayList<>();
        List<SortRequest> sortRequests=new ArrayList<>();
        defaultOrderFilter(status, branch, delivery, desc, sort, searchRequest, filterRequests, sortRequests);
        Page<Order> orderPage = orderRepository.findAll(
                new EntitySpecification<>(searchRequest.filters(filterRequests).build()),
                EntitySpecification.getPageable(page, size)
        );
        return PageableResponse.parsePage(orderPage, orderMapper.orderToOrderFrontDto(orderPage.getContent()));
    }

    public PageableResponse<OrderFrontDto> getAllToday(String status, Long branch, Boolean delivery, int size, int page, boolean desc, String[] sort){
        SearchRequest.SearchRequestBuilder searchRequest = SearchRequest.builder();
        List<FilterRequest> filterRequests=new ArrayList<>();
        List<SortRequest> sortRequests=new ArrayList<>();
        LocalDateTime from=LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime to=LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        filterRequests.add(FilterRequest.builder()
                        .key("time")
                        .value(from.toString())
                        .valueTo(to.toString())
                        .fieldType(FieldType.DATE)
                        .operator(Operator.BETWEEN)
                .build());
        defaultOrderFilter(status, branch, delivery, desc, sort, searchRequest, filterRequests, sortRequests);
        Page<Order> orderPage = orderRepository.findAll(
                new EntitySpecification<>(searchRequest.filters(filterRequests).build()),
                EntitySpecification.getPageable(page, size)
        );
        return PageableResponse.parsePage(orderPage, orderMapper.orderToOrderFrontDto(orderPage.getContent()));
    }

    private void defaultOrderFilter(String status, Long branch, Boolean delivery, boolean desc, String[] sort, SearchRequest.SearchRequestBuilder searchRequest, List<FilterRequest> filterRequests, List<SortRequest> sortRequests) {
        if(branch!=null){
            filterRequests.add(FilterRequest.builder()
                    .operator(Operator.EQUAL)
                    .value(branch)
                    .fieldType(FieldType.LONG)
                    .key("branch.id")
                    .build());
        }
        if(status != null) {
            try {
                OrderStatus.valueOf(status.toUpperCase());
                filterRequests.add(FilterRequest.builder()
                        .key("orderStatus")
                        .operator(Operator.EQUAL)
                        .value(OrderStatus.valueOf(status.toUpperCase()))
                        .fieldType(FieldType.OBJECT)
                        .build());
            }catch (Exception ignore){}

        }
        if(sort!=null){
            for (String s : sort) {
                sortRequests.add(SortRequest.builder()
                        .key(s)
                        .direction(desc?SortDirection.DESC:SortDirection.ASC)
                        .build());
            }
        }
        if(delivery!=null){
            filterRequests.add(FilterRequest.builder()
                    .key("delivery")
                    .value(null)
                    .fieldType(FieldType.OBJECT)
                    .operator(delivery?Operator.NOT_EQUAL:Operator.EQUAL)
                    .build());
        }
        searchRequest.sorts(sortRequests);
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
