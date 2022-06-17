package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.CategoryChildrenDto;
import com.project.fastfoodapi.dto.CategoryDto;
import com.project.fastfoodapi.entity.Category;
import com.project.fastfoodapi.repository.CategoryRepository;
import com.project.fastfoodapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    final CategoryService categoryService;
    final CategoryRepository categoryRepository;

    @GetMapping
    public HttpEntity<?> getAll() {
        return ResponseEntity.ok().body(categoryRepository.findByActiveIsTrue());
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getOneCategory(@PathVariable Long id,
                                        @RequestParam(required = false, defaultValue = "false") boolean children) {
        Optional<Category> optionalCategory = categoryRepository.findByIdAndActiveTrue(id);
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (children) {
            CategoryChildrenDto result = categoryService.getChildren(optionalCategory.get());
            return ResponseEntity.ok().body(result);
        }
        return ResponseEntity.ok().body(optionalCategory.get());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public HttpEntity<?> add(@RequestBody CategoryDto dto) {
        ApiResponse<Category> apiResponse = categoryService.add(dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public HttpEntity<?> edit(@RequestBody CategoryDto dto, @PathVariable Long id) {
        ApiResponse<Category> apiResponse = categoryService.edit(id, dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable Long id) {
        ApiResponse<Object> apiResponse = categoryService.delete(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @GetMapping("/parent")
    public HttpEntity<?> getAllCategoryParent(@RequestParam(required = false, defaultValue = "false") boolean children) {
        List<Category> categories = categoryRepository.findByParentNullAndActiveTrue();
        if(children){
            List<CategoryChildrenDto> result=new ArrayList<>();
            for (Category category : categories) {
                result.add(categoryService.getChildren(category));
            }
            return ResponseEntity.ok().body(result);
        }
        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/{id}/child")
    public HttpEntity<?> getCategoryChild(@PathVariable Long id) {
        List<Category> categories = categoryRepository.findByParent_IdAndActiveTrue(id);
        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/{id}/children")
    public HttpEntity<?> getCategoryChildren(@PathVariable Long id) {
        Optional<Category> optionalCategory = categoryRepository.findByIdAndActiveTrue(id);
        if (optionalCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<CategoryChildrenDto> result = categoryService.getChildren(id);
        return ResponseEntity.ok().body(result);
    }

}
