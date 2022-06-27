package com.project.fastfoodapi.dto;

import com.project.fastfoodapi.entity.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryChildrenDto {
    private String nameUz, nameRu;
    private Long id;
    private Category parent;
    @Builder.Default
    private List<CategoryChildrenDto> children=new ArrayList<>();
}
