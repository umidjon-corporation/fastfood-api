package com.project.fastfoodapi.mapper;

import com.project.fastfoodapi.dto.CategoryDto;
import com.project.fastfoodapi.entity.Category;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "parentId", target = "parent.id")
    Category categoryDtoToCategory(CategoryDto categoryDto);

    @Mapping(source = "parentId", target = "parent.id", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromCategoryDto(CategoryDto categoryDto, @MappingTarget Category category);
}
