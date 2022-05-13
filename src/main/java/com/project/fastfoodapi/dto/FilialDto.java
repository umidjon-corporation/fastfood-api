package com.project.fastfoodapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class FilialDto implements Serializable {
    private final String nameUz;
    private final String nameRu;
    private final String intended;
    private final String address;
    private final LocalTime start;
    private final LocalTime finish;
    private final Float latitude;
    private final Float longitude;
}
