package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.CourierEditDto;
import com.project.fastfoodapi.dto.PageableResponse;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.Order;
import com.project.fastfoodapi.entity.enums.OrderStatus;
import com.project.fastfoodapi.entity.enums.UserType;
import com.project.fastfoodapi.mapper.AttachmentMapper;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.mapper.OrderMapper;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.repository.OrderRepository;
import com.project.fastfoodapi.specification.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourierService {
    final HumanRepository humanRepository;
    final HumanMapper humanMapper;
    final AttachmentMapper attachmentMapper;
    final OrderRepository orderRepository;
    final OrderMapper orderMapper;

    @SneakyThrows
    public ApiResponse<HumanFrontDto> changePhoto(Long id, MultipartFile photo) {
        Optional<Human> optionalHuman = humanRepository.findById(id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.COURIER) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Courier with id=(" + id + ") not found")
                    .build();
        }
        if (photo == null || photo.isEmpty() || photo.getOriginalFilename() == null) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("File must be in field \"photo\" and not be empty")
                    .build();
        }
        if (!photo.getOriginalFilename().matches("^(.+)\\.(jpg|png|jpeg)$")) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("File type must be jpg, png or jpeg")
                    .build();
        }
        optionalHuman.get().setPhoto(attachmentMapper.toEntity(photo));
        Human save = humanRepository.save(optionalHuman.get());
        return ApiResponse.<HumanFrontDto>builder()
                .success(true)
                .message("Photo changed!")
                .data(humanMapper.humanToHumanFrontDto(save))
                .build();
    }

    @SneakyThrows
    public ApiResponse<HumanFrontDto> edit(Long id, CourierEditDto dto, Human principal) {
        if(principal.getUserType()==UserType.COURIER && !principal.getId().equals(id)){
            throw new AccessDeniedException("Not access for this information");
        }
        Optional<Human> optionalHuman = humanRepository.findById(id);
        if (optionalHuman.isEmpty() || optionalHuman.get().getUserType() != UserType.COURIER) {
            return ApiResponse.<HumanFrontDto>builder()
                    .message("Courier with id=(" + id + ") not found")
                    .build();
        }
        Human human = optionalHuman.get();
        human.setName(dto.getName());
        human.setNumber(dto.getNumber());
        humanRepository.save(human);
        return ApiResponse.<HumanFrontDto>builder()
                .success(true)
                .message("Edited!")
                .build();
    }

    public ResponseEntity<?> getOrders(Long id, LocalDate from, LocalDate to, int page, int size, String[] sort, boolean desc) {
        SearchRequest.SearchRequestBuilder searchRequest = SearchRequest.builder();
        List<FilterRequest> requiredFilterRequests=new ArrayList<>();
        FilterRequest filterRequest=null;
        requiredFilterRequests.add(FilterRequest.builder()
                        .fieldType(FieldType.LONG)
                        .operator(Operator.EQUAL)
                        .key("delivery.courier.id")
                        .value(id)
                .build());
        requiredFilterRequests.add(FilterRequest.builder()
                        .operator(Operator.NOT_EQUAL)
                        .fieldType(FieldType.OBJECT)
                        .value(OrderStatus.CANCELED)
                        .key("orderStatus")
                .build());
        if(from!=null){
            filterRequest=FilterRequest.builder()
                    .key("time")
                    .value(LocalDateTime.of(from, LocalTime.MIN))
                    .fieldType(FieldType.DATE)
                    .operator(Operator.BETWEEN)
                    .valueTo(LocalDateTime.of(LocalDate.now(), LocalTime.MAX))
                    .build();
        }
        if(to!=null){
            filterRequest= FilterRequest.builder()
                    .key("time")
                    .value(LocalDateTime.MIN)
                    .fieldType(FieldType.DATE)
                    .operator(Operator.BETWEEN)
                    .valueTo(LocalDateTime.of(to, LocalTime.MAX))
                    .build();
        }
        if(from!=null && to!=null){
            filterRequest= FilterRequest.builder()
                    .key("time")
                    .value(LocalDateTime.of(from, LocalTime.MIN))
                    .fieldType(FieldType.DATE)
                    .operator(Operator.BETWEEN)
                    .valueTo(LocalDateTime.of(to, LocalTime.MAX))
                    .build();
        }
        Page<Order> all = orderRepository.findAll(
                new EntitySpecification<Order>(searchRequest.filters(requiredFilterRequests).build())
                        .and(new EntitySpecification<>(searchRequest.filters(filterRequest!=null?List.of(filterRequest):List.of()).build())),
                EntitySpecification.getPageable(page, size)
        );
        return ResponseEntity.ok().body(PageableResponse.parsePage(all, orderMapper.orderToOrderFrontDto(all.getContent())));
    }
}
