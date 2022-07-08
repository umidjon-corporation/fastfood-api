package com.project.fastfoodapi.dto.front;

import com.project.fastfoodapi.dto.AttachmentDto;
import com.project.fastfoodapi.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFrontDto {
    private Long id;
    private String nameUz, nameRu, description;
    private AttachmentDto photo;
    private BigDecimal price;
    private Category category;
}
