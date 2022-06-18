package com.project.fastfoodapi.mapper;

import com.project.fastfoodapi.dto.ProductDto;
import com.project.fastfoodapi.dto.front.ProductFrontDto;
import com.project.fastfoodapi.entity.Product;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {AttachmentMapper.class})
public interface ProductMapper {
    @Mapping(source = "categoryId", target = "category.id")
    Product productDtoToProduct(ProductDto productDto);

    @Mapping(source = "id", target = "photo.url", qualifiedByName = "url")
    @Mapping(source = "id", target = "id")
    ProductFrontDto toFrontDto(Product product);

    List<ProductFrontDto> toFrontDto(List<Product> products);

    @Mapping(source = "categoryId", target = "category.id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromProductDto(ProductDto productDto, @MappingTarget Product product);

    @Named("url")
    default String urlGenerate(Long id) {
        return "/api/product/" + id + "/photo";
    }
}
