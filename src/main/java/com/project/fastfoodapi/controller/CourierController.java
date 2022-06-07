package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.CourierEditDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.entity.Human;
import com.project.fastfoodapi.entity.enums.ClientStatus;
import com.project.fastfoodapi.mapper.HumanMapper;
import com.project.fastfoodapi.mapper.OrderMapper;
import com.project.fastfoodapi.repository.HumanRepository;
import com.project.fastfoodapi.repository.OrderRepository;
import com.project.fastfoodapi.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class CourierController {
    final HumanRepository humanRepository;
    final HumanMapper humanMapper;
    final OrderRepository orderRepository;
    final OrderMapper orderMapper;
    final CourierService courierService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COURIER')")
    @GetMapping("/{number}")
    public HttpEntity<?> getSelf(@PathVariable String number) {
        Optional<Human> optionalHuman = humanRepository.findByNumber(number);
        if (optionalHuman.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(humanMapper.humanToHumanFrontDto(optionalHuman.get()));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COURIER')")
    @GetMapping("/{id}/orders")
    public HttpEntity<?> getOrders(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Optional<Human> optionalHuman = humanRepository.findByStatusIsNotAndId(ClientStatus.DELETED, id);
        if (optionalHuman.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (from == null && to == null) {
            return ResponseEntity.ok().body(orderMapper.orderToOrderFrontDto(orderRepository.findByDelivery_Courier_Id(id)));
        } else if (from != null && to != null) {
            return ResponseEntity.ok().body(orderMapper.orderToOrderFrontDto(
                    orderRepository.findByDelivery_Courier_IdAndTimeIsBetween(id,
                            LocalDateTime.of(from, LocalTime.parse("00:00")),
                            LocalDateTime.of(to, LocalTime.parse("23:59")))
            ));
        }
        if (from != null) {
            return ResponseEntity.ok().body(orderMapper.orderToOrderFrontDto(
                    orderRepository.findByDelivery_Courier_IdAndTimeIsBetween(id,
                            LocalDateTime.of(from, LocalTime.parse("00:00")), LocalDateTime.now())
            ));
        }
        return ResponseEntity.ok().body(orderMapper.orderToOrderFrontDto(
                orderRepository.findByDelivery_Courier_Id(id)
        ));
    }

    @PutMapping("/{id}/photo")
    public HttpEntity<?> changePhoto(@PathVariable Long id, MultipartHttpServletRequest req) {
        ApiResponse<HumanFrontDto> apiResponse = courierService.changePhoto(id, req.getFile("photo"));
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COURIER')")
    @PutMapping("/{id}")
    public HttpEntity<?> editData(@PathVariable Long id, @RequestBody CourierEditDto dto) {
        ApiResponse<HumanFrontDto> apiResponse = courierService.edit(id, dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

}
