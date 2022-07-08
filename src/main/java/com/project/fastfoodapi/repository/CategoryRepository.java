package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    List<Category> findByActiveIsTrue();

    Optional<Category> findByIdAndActiveTrue(Long id);

    List<Category> findByParent_IdAndActiveTrue(Long id);

    List<Category> findByParentNullAndActiveTrue();
}