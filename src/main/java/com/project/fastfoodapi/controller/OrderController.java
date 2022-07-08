package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.OrderDto;
import com.project.fastfoodapi.dto.front.DeliveryFrontDto;
import com.project.fastfoodapi.dto.front.HumanFrontDto;
import com.project.fastfoodapi.dto.front.OrderFrontDto;
import com.project.fastfoodapi.entity.Order;
import com.project.fastfoodapi.entity.enums.OrderStatus;
import com.project.fastfoodapi.mapper.OrderMapper;
import com.project.fastfoodapi.repository.BranchRepository;
import com.project.fastfoodapi.repository.OrderRepository;
import com.project.fastfoodapi.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    final OrderRepository orderRepository;
    final OrderMapper orderMapper;
    final OrderService orderService;
    final BranchRepository branchRepository;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'COURIER')")
    @GetMapping
    public HttpEntity<?> getAll(@RequestParam(required = false, defaultValue = "") String status,
                                @RequestParam(required = false) Long branch,
                                @RequestParam(required = false) Boolean delivery,
                                @RequestParam(required = false, defaultValue = "0") Integer page,
                                @RequestParam(required = false, defaultValue = "40") Integer size,
                                @RequestParam(required = false) boolean desc,
                                @RequestParam(required = false) String[] sort
    ) {
        return ResponseEntity.ok().body(orderService.getAll(status, branch, delivery, size, page, desc, sort));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'COURIER')")
    @GetMapping("/group/status")
    public HttpEntity<?> getGroupByStatus(
                                @RequestParam(required = false, defaultValue = "0") Integer page,
                                @RequestParam(required = false, defaultValue = "40") Integer size,
                                @RequestParam(required = false) boolean desc
    ) {
        return ResponseEntity.ok().body(orderService.getAllAndGroupByStatus(size, page, desc));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'COURIER')")
    @GetMapping("/today/group/status")
    public HttpEntity<?> getTodayGroupByStatus(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "40") Integer size,
            @RequestParam(required = false) boolean desc
    ) {
        return ResponseEntity.ok().body(orderService.getTodayAllAndGroupByStatus(size, page, desc));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'OPERATOR', 'COURIER')")
    @GetMapping("/today")
    public HttpEntity<?> getToday(@RequestParam(required = false, defaultValue = "") String status,
                                  @RequestParam(required = false) Long branch,
                                  @RequestParam(required = false) Boolean delivery,
                                  @RequestParam(required = false, defaultValue = "0") Integer page,
                                  @RequestParam(required = false, defaultValue = "50") Integer size,
                                  @RequestParam(required = false) boolean desc,
                                  @RequestParam(required = false) String[] sort
    ) {
        return ResponseEntity.ok().body(orderService.getAllToday(status, branch, delivery, size, page, desc, sort));
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getOne(@PathVariable Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(orderMapper.orderToOrderFrontDto(optionalOrder.get()));
    }

    @PostMapping
    public HttpEntity<?> add(@RequestBody OrderDto dto) {
        ApiResponse<OrderFrontDto> apiResponse = orderService.add(dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PatchMapping("/{id}/status")
    public HttpEntity<?> changeStatus(@RequestBody Map<String, String> body, @PathVariable Long id) {
        if (!body.containsKey("status")) {
            return ResponseEntity.badRequest().body(ApiResponse.builder().message("status field required").build());
        }
        ApiResponse<Object> apiResponse = orderService.changeStatus(id, OrderStatus.valueOf(body.get("status").toUpperCase()));
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PatchMapping("/{id}/operator")
    public HttpEntity<?> changeOperator(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        if (!body.containsKey("id")) {
            return ResponseEntity.badRequest().body(ApiResponse.builder().message("id field required").build());
        }
        ApiResponse<HumanFrontDto> apiResponse = orderService.changeOperator(id, body.get("id"));
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PatchMapping("/{id}/courier")
    public HttpEntity<?> changeCourier(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        if (!body.containsKey("id")) {
            return ResponseEntity.badRequest().body(ApiResponse.builder().message("Id field required").build());
        }
        ApiResponse<HumanFrontDto> apiResponse = orderService.changeCourier(id, body.get("id"));
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @GetMapping("/{id}/delivery")
    public HttpEntity<?> getDelivery(@PathVariable Long id) {
        ApiResponse<DeliveryFrontDto> apiResponse = orderService.getDelivery(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }
}
