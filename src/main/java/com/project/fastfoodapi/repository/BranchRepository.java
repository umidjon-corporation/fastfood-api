package com.project.fastfoodapi.repository;

import com.project.fastfoodapi.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByActiveTrue();

    Optional<Branch> findByIdAndActiveTrue(Long id);
}