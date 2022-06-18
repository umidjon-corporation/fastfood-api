package com.project.fastfoodapi.dto.front;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class DeliveryFrontDto implements Serializable {
    private final BigDecimal price;
    private final Float longitude;
    private final Float latitude;
    private final String address;
    private final HumanFrontDto courier;
    private final Long id;
}
