package com.project.fastfoodapi.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    NEW("Yangi"),
    ACCEPTED("Qabul qilingan"),
    SENT("Jo’natilgan"),
    CLOSED("Yopilgan");

    private final String title;
}
