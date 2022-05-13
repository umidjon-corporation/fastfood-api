package com.project.fastfoodapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class ProductDto implements Serializable {
    private final MultipartFile photo;
    private final String nameUz;
    private final String nameRu;
    private final BigDecimal price;
    private final String description;
    private final Long categoryId;
}
