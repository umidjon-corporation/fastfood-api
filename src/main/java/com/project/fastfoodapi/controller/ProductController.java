package com.project.fastfoodapi.controller;

import com.project.fastfoodapi.dto.ApiResponse;
import com.project.fastfoodapi.dto.PageableResponse;
import com.project.fastfoodapi.dto.ProductDto;
import com.project.fastfoodapi.dto.front.ProductFrontDto;
import com.project.fastfoodapi.entity.Attachment;
import com.project.fastfoodapi.entity.Product;
import com.project.fastfoodapi.mapper.ProductMapper;
import com.project.fastfoodapi.repository.OrderProductRepository;
import com.project.fastfoodapi.repository.ProductRepository;
import com.project.fastfoodapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    final ProductRepository productRepository;
    final ProductService productService;
    final ProductMapper productMapper;
    final OrderProductRepository orderProductRepository;


    @GetMapping
    public HttpEntity<?> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false, defaultValue = "false") boolean desc
    ) {
       return productService.getAll(categoryId, q, sort, page, size, desc);
    }

    @GetMapping("/{id}")
    public HttpEntity<?> getOne(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdAndActiveTrue(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(productMapper.toFrontDto(optionalProduct.get()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public HttpEntity<?> add(@ModelAttribute ProductDto dto) {
        ApiResponse<ProductFrontDto> apiResponse = productService.add(dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public HttpEntity<?> edit(@ModelAttribute ProductDto dto, @PathVariable Long id) {
        ApiResponse<ProductFrontDto> apiResponse = productService.edit(id, dto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable Long id) {
        ApiResponse<Object> apiResponse = productService.delete(id);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 400).body(apiResponse);
    }

    @GetMapping("/{id}/photo")
    public HttpEntity<?> getPhoto(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findByIdAndActiveTrue(id);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Attachment photo = optionalProduct.get().getPhoto();

        return ResponseEntity.ok()
                .contentLength(photo.getSize())
                .contentType(MediaType.parseMediaType(photo.getType()))
                .body(photo.getBytes());
    }

    @GetMapping("/top")
    public HttpEntity<?> getTopProducts(@RequestParam Integer limit) {
        List<Long> products = orderProductRepository.findTopProducts(limit);
        List<Product> productList = productRepository.findAllById(products);
        return ResponseEntity.ok().body(productMapper.toFrontDto(productList));
    }
}
