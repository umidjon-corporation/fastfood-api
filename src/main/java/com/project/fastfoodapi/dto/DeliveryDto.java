package com.project.fastfoodapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class DeliveryDto implements Serializable {
    private final Float longitude;
    private final Float latitude;
    private final String address;
}
