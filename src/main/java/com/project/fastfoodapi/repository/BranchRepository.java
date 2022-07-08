package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long>, JpaSpecificationExecutor<Branch> {
    List<Branch> findByActiveTrue();

    Optional<Branch> findByIdAndActiveTrue(Long id);
}