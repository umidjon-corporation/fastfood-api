package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.CategoryChildrenDto;
import com.project.fastfoodapi.dto.CategoryDto;
import com.project.fastfoodapi.dto.PageableResponse;
import com.project.fastfoodapi.entity.Category;
import com.project.fastfoodapi.entity.Product;
import com.project.fastfoodapi.mapper.CategoryMapper;
import com.project.fastfoodapi.repository.CategoryRepository;
import com.project.fastfoodapi.repository.ProductRepository;
import com.project.fastfoodapi.specification.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    final CategoryMapper categoryMapper;
    final CategoryRepository categoryRepository;
    final ProductRepository productRepository;

    public ApiResponse<Category> add(CategoryDto dto) {
        Category category = categoryMapper.categoryDtoToCategory(dto);
        if (dto.getParentId() == null) {
            category.setParent(null);
        } else {
            category.setParent(categoryRepository.findByIdAndActiveTrue(dto.getParentId()).orElse(category.getParent()));
            List<Product> productByParentCategory = productRepository.findAllByCategory_IdAndActiveTrue(category.getParent().getId());
            if (!productByParentCategory.isEmpty()) {
                return ApiResponse.<Category>builder()
                        .message("There are products in the parent category. You must change their category or change the parent category")
                        .build();
            }
        }

        Category save = categoryRepository.save(category);
        return ApiResponse.<Category>builder()
                .success(true)
                .data(save)
                .message("Added!")
                .build();
    }

    public boolean checkCategoryToInfinityConnection(Category category, Category parentCategory){
        if(category==null || parentCategory==null){
            return false;
        }
        if(category.getId().equals(parentCategory.getId())){
            return true;
        }
        if(parentCategory.getParent()==null){
            return false;
        }
        if(parentCategory.getParent().getId().equals(category.getId())){
            return true;
        }
        return checkCategoryToInfinityConnection(category, parentCategory.getParent());
    }

    public ApiResponse<Category> edit(Long id, CategoryDto dto) {
        Optional<Category> optionalCategory = categoryRepository.findByIdAndActiveTrue(id);
        if (optionalCategory.isEmpty()) {
            return ApiResponse.<Category>builder()
                    .message("Category with id=(" + id + ") not found")
                    .build();
        }
        Category category = optionalCategory.get();
        category.setNameRu(dto.getNameRu());
        category.setNameUz(dto.getNameUz());
        if (dto.getParentId() == null) {
            category.setParent(null);
        } else {
            Category parent = categoryRepository.findByIdAndActiveTrue(dto.getParentId()).orElse(null);
            if (parent!=null){
                if(checkCategoryToInfinityConnection(category, parent)){
                    return ApiResponse.<Category>builder()
                            .message("You can't set category parent which parent equals to this category")
                            .build();
                }
                List<Product> productByParentCategory = productRepository.findAllByCategory_IdAndActiveTrue(parent.getId());
                if (!productByParentCategory.isEmpty()) {
                    return ApiResponse.<Category>builder()
                            .message("There are products in the parent category. You must change their category or change the parent category")
                            .build();
                }
                category.setParent(parent);
            }
        }

        Category save = categoryRepository.save(category);
        return ApiResponse.<Category>builder()
                .success(true)
                .data(save)
                .message("Edited!")
                .build();
    }

    public ApiResponse<Object> delete(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findByIdAndActiveTrue(id);
        if (optionalCategory.isEmpty()) {
            return ApiResponse.builder()
                    .message("Category with id=(" + id + ") not found")
                    .build();
        }
        optionalCategory.get().setActive(false);
        categoryRepository.save(optionalCategory.get());
        return ApiResponse.builder()
                .success(true)
                .message("Deleted!")
                .build();
    }

    public List<CategoryChildrenDto> getChildren(Long id) {
        List<Category> categories = categoryRepository.findByParent_IdAndActiveTrue(id);
        List<CategoryChildrenDto> result = new ArrayList<>();
        for (Category category : categories) {
            result.add(getChildren(category));
        }
        return result;
    }

    public CategoryChildrenDto getChildren(Category category) {
        CategoryChildrenDto result = CategoryChildrenDto.builder()
                .id(category.getId())
                .nameUz(category.getNameUz())
                .nameRu(category.getNameRu())
                .build();
        List<Category> categories = categoryRepository.findByParent_IdAndActiveTrue(category.getId());
        if (categories.isEmpty()) {
            return result;
        }
        for (Category child : categories) {
            result.getChildren().add(getChildren(child));
        }
        return result;
    }

    public PageableResponse<Category> getAll(String q, String[] sort, boolean desc, int page, int size){
        SearchRequest.SearchRequestBuilder searchRequest = SearchRequest.builder();
        List<FilterRequest> filterRequests=new ArrayList<>();
        filterRequests.add(FilterRequest.builder()
                        .key("active")
                        .operator(Operator.EQUAL)
                        .value(true)
                        .fieldType(FieldType.BOOLEAN)
                .build());
        if(q!=null && !q.equals("")){
            filterRequests.add(FilterRequest.builder()
                            .operator(Operator.LIKE)
                            .value(q)
                            .key("nameUz")
                            .fieldType(FieldType.STRING)
                    .build());
            filterRequests.add(FilterRequest.builder()
                    .operator(Operator.LIKE)
                    .value(q)
                    .or(true)
                    .fieldType(FieldType.STRING)
                    .key("nameRu")
                    .build());
        }
        searchRequest.filters(filterRequests);
        if(sort!=null){
            List<SortRequest> sortRequests=new ArrayList<>();
            for (String s : sort) {
                sortRequests.add(SortRequest.builder()
                                .key(s)
                                .direction(desc?SortDirection.DESC:SortDirection.ASC)
                        .build());
            }
            searchRequest.sorts(sortRequests);
        }
        Page<Category> all = categoryRepository.findAll(
                new EntitySpecification<>(searchRequest.build()),
                EntitySpecification.getPageable(page, size)
        );
        return PageableResponse.<Category>builder()
                        .totalPages(all.getTotalPages())
                        .currentPage(all.getNumber())
                        .totalItems(all.getTotalElements())
                        .content(all.getContent())
                .build();
    }
}
