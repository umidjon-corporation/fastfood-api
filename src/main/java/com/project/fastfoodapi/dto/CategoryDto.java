package com.project.fastfoodapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class CategoryDto implements Serializable {
    private final String nameUz, nameRu;
    private final Long parentId;
}
