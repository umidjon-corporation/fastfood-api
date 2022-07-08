package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByIdAndActiveTrue(Long id);

//    List<Product> findByActiveTrue();
    Page<Product> findByActiveTrue(Pageable pageable);

    List<Product> findAllByCategory_IdAndActiveTrue(Long category_id);
}