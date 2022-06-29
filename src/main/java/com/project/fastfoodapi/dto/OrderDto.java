package com.project.fastfoodapi.dto;

import com.project.fastfoodapi.entity.enums.PayType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
public class OrderDto implements Serializable {
    private final List<OrderProductDto> products;
    private final PayType payType;
    private final DeliveryDto delivery;
    private final Long branchId;
    private final Long clientId;
}
