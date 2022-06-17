package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndActiveTrue(Long id);

    List<Product> findByActiveTrue();

    List<Product> findAllByCategory_Id(Long category_id);
}