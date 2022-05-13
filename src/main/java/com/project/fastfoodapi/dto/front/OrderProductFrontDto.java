package com.project.fastfoodapi.dto.front;

import com.project.fastfoodapi.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class OrderProductFrontDto implements Serializable {
    private final ProductFrontDto product;
    private final Integer count;
    private final BigDecimal price, amount;
}
