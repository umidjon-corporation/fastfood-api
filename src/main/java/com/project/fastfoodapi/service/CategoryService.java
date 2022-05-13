package com.project.fastfoodapi.service;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.CategoryChildrenDto;
import com.project.fastfoodapi.dto.CategoryDto;
import com.project.fastfoodapi.entity.Category;
import com.project.fastfoodapi.mapper.CategoryMapper;
import com.project.fastfoodapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    final CategoryMapper categoryMapper;
    final CategoryRepository categoryRepository;

    public ApiResponse<Category> add(CategoryDto dto) {
        Category category = categoryMapper.categoryDtoToCategory(dto);
        if (dto.getParentId() == null) {
            category.setParent(null);
        } else {
            category.setParent(categoryRepository.findByIdAndActiveTrue(dto.getParentId()).orElse(category.getParent()));
        }
        Category save = categoryRepository.save(category);
        return ApiResponse.<Category>builder()
                .success(true)
                .data(save)
                .message("Added!")
                .build();
    }

    public ApiResponse<Category> edit(Long id, CategoryDto dto) {
        Optional<Category> optionalCategory = categoryRepository.findByIdAndActiveTrue(id);
        if (optionalCategory.isEmpty()) {
            return ApiResponse.<Category>builder()
                    .message("Category with id=(" + id + ") not found")
                    .build();
        }
        Category category = optionalCategory.get();
        categoryMapper.updateCategoryFromCategoryDto(dto, category);
        if (dto.getParentId() == null) {
            category.setParent(null);
        } else {
            category.setParent(categoryRepository.findByIdAndActiveTrue(dto.getParentId()).orElse(category.getParent()));
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

    public List<CategoryChildrenDto> getChildren(Long id){
        List<Category> categories = categoryRepository.findByParent_IdAndActiveTrue(id);
        List<CategoryChildrenDto> result=new ArrayList<>();
        for (Category category : categories) {
            result.add(getChildren(category));
        }
        return result;
    }

    public CategoryChildrenDto getChildren(Category category){
        CategoryChildrenDto result=CategoryChildrenDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
        List<Category> categories = categoryRepository.findByParent_IdAndActiveTrue(category.getId());
        if(categories.isEmpty()){
            return result;
        }
        for (Category child : categories) {
            result.getChildren().add(getChildren(child));
        }
        return result;
    }
}
