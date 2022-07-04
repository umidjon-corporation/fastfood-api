package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.ProductDto;
import com.project.fastfoodapi.dto.front.ProductFrontDto;
import com.project.fastfoodapi.entity.Category;
import com.project.fastfoodapi.entity.Product;
import com.project.fastfoodapi.mapper.ProductMapper;
import com.project.fastfoodapi.repository.CategoryRepository;
import com.project.fastfoodapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    final ProductRepository productRepository;
    final ProductMapper productMapper;
    final CategoryRepository categoryRepository;


    public ApiResponse<ProductFrontDto> add(ProductDto dto) {
        if (dto.getPhoto() == null || dto.getPhoto().isEmpty()) {
            return ApiResponse.<ProductFrontDto>builder()
                    .message("Photo shouldn't be empty")
                    .build();
        }
        if (dto.getPhoto().getOriginalFilename() == null || dto.getPhoto().getOriginalFilename().matches("^(.+)\\.(png|jpeg|ico|jpg)$")) {
            return ApiResponse.<ProductFrontDto>builder()
                    .message("Photo type must be png, jpeg, ico, jpg")
                    .build();
        }
        Product product = productMapper.productDtoToProduct(dto);
        ApiResponse<ProductFrontDto> apiResponse = checkCategory(dto, product);
        if (!apiResponse.isSuccess()) {
            return apiResponse;
        }
        Product save = productRepository.save(product);
        return ApiResponse.<ProductFrontDto>builder()
                .success(true)
                .data(productMapper.toFrontDto(save))
                .message("Added!")
                .build();
    }

    public ApiResponse<ProductFrontDto> edit(Long id, ProductDto dto) {
        Optional<Product> optionalProduct = productRepository.findByIdAndActiveTrue(id);
        if (optionalProduct.isEmpty()) {
            return ApiResponse.<ProductFrontDto>builder()
                    .message("Product with id=(" + id + ") not found")
                    .build();
        }
        Product product = optionalProduct.get();
        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            if (dto.getPhoto().getOriginalFilename() == null || !dto.getPhoto().getOriginalFilename().matches("^(.+)\\.(png|jpeg|ico|jpg)$")) {
                return ApiResponse.<ProductFrontDto>builder()
                        .message("Photo type must be png, jpeg, ico, jpg")
                        .build();
            }
        }
        productMapper.updateProductFromProductDto(dto, product);
        ApiResponse<ProductFrontDto> apiResponse = checkCategory(dto, product);
        if (!apiResponse.isSuccess()) {
            return apiResponse;
        }
        Product save = productRepository.save(product);
        return ApiResponse.<ProductFrontDto>builder()
                .success(true)
                .data(productMapper.toFrontDto(save))
                .message("Edited!")
                .build();
    }

    private ApiResponse<ProductFrontDto> checkCategory(ProductDto dto, Product product) {
        Optional<Category> optionalCategory = categoryRepository.findByIdAndActiveTrue(dto.getCategoryId());
        List<Category> categoryChildren = categoryRepository.findByParent_IdAndActiveTrue(dto.getCategoryId());
        if (categoryChildren.isEmpty()) {
            product.setCategory(optionalCategory.orElse(product.getCategory()));
            return ApiResponse.<ProductFrontDto>builder()
                    .success(true)
                    .build();
        }
        return ApiResponse.<ProductFrontDto>builder()
                .message("You can't set as category which have children category")
                .build();
    }


    public ApiResponse<Object> delete(Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdAndActiveTrue(id);
        if (optionalProduct.isEmpty()) {
            return ApiResponse.builder()
                    .message("Product with id=(" + id + ") not found")
                    .build();
        }
        optionalProduct.get().setActive(false);
        productRepository.save(optionalProduct.get());
        return ApiResponse.builder()
                .success(true)
                .message("Deleted!")
                .build();
    }
}
